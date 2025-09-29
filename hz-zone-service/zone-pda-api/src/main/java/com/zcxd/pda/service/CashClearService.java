package com.zcxd.pda.service;

import com.zcxd.common.constant.*;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import com.zcxd.db.model.*;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.*;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.exception.SystemErrorType;
import com.zcxd.pda.service.impl.*;
import com.zcxd.pda.vo.BatchBoxNosVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName CashClearService
 * @Description 清分管理服务
 * @author shijin
 * @Date 2021年5月28日上午10:50:05
 */
@Service
@Slf4j
public class CashClearService {

    @Resource
    private AtmClearTaskService atmClearTaskService;
    @Resource
    private AtmClearErrorService atmClearErrorService;
    @Resource
    private AtmTaskReturnService atmTaskReturnService;
    @Resource
    private AtmTaskService atmTaskService;
    @Resource
    private BankService bankService;
    @Resource
    private AtmDeviceService atmDeviceService;
    @Resource
    private RouteService routeService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private CashboxService cashboxService;
    @Resource
    private CashboxPackRecordService cashboxPackRecordService;
    @Resource
    private CashboxScanRecordService cashboxScanRecordService;
    @Resource
    private DenomService denomService;
    @Resource
    private EmployeeService employeeService;

    @Resource
    private RedisUtil redisUtil;
    /**
     * 根据钞盒或钞袋编号，查询清分任务列表
     * @param boxNo
     * @return
     */
    public AtmClearTaskRouteDTO listRouteClearTask(String boxNo,String taskDay) throws BusinessException {

        List<AtmTaskReturn> returnList = atmTaskReturnService.listByBoxNo(boxNo,taskDay);
        if (returnList.size() == 0) {
            log.info("无此钞袋记录: " + boxNo + " taskDay: " + taskDay);
            throw new BusinessException("无此钞袋记录");
        }
        //如果只有一个，采用该记录，如果有两个以上，取没有清点的
        AtmTaskReturn atmTaskReturn = returnList.get(0);
        if (returnList.size() > 1) {
            for (AtmTaskReturn tmptaskReturn : returnList) {
                if (tmptaskReturn.getClearFlag() == 0) {
                    atmTaskReturn = tmptaskReturn;
                }
            }
        }
        AtmTask atmTask = atmTaskService.getById(atmTaskReturn.getTaskId());
        if (null == atmTask) {
            log.error("无效任务id：" + atmTaskReturn.toString());
            throw new BusinessException("无效任务ID");
        }
        Route route = routeService.getById(atmTask.getRouteId());
        if (null == route) {
            log.error("无效线路ID：" + atmTask.toString());
            throw new BusinessException("无效线路ID");
        }
        Bank bank = bankService.getById(atmTaskReturn.getBankId());
        if (null == bank) {
            log.error("无效银行ID：" + atmTaskReturn.toString());
            throw new BusinessException("无效银行ID");
        }

        AtmClearTaskRouteDTO atmClearTaskRouteDTO = new AtmClearTaskRouteDTO();
        atmClearTaskRouteDTO.setTaskDate(taskDay);
        atmClearTaskRouteDTO.setBankName(bank.getFullName());
        atmClearTaskRouteDTO.setBankId(bank.getId());
        atmClearTaskRouteDTO.setRouteNo(route.getRouteNo());
        atmClearTaskRouteDTO.setRouteId(route.getId());
        List<AtmClearTask> atmClearTaskList = atmClearTaskService.listByRouteId(atmTask.getRouteId(),atmTaskReturn.getBankId());
        if (atmClearTaskList.size() == 0) {
            atmClearTaskRouteDTO.setTaskRecordList(new ArrayList<>());
            return atmClearTaskRouteDTO;
        }

        Set<Long> atmIdSet = atmClearTaskList.stream().map(AtmClearTask::getAtmId).collect(Collectors.toSet());
        List<AtmDevice> atmDeviceList = atmDeviceService.listByIds(atmIdSet);
        Map<Long,AtmDevice> atmDeviceMap = atmDeviceList.stream().collect(Collectors.toMap(AtmDevice::getId, Function.identity(),(key1,key2) -> key2));

        List<AtmClearTaskRecord> taskRecords = atmClearTaskList.stream().map(atmClearTask -> {
            AtmClearTaskRecord taskRecord = new AtmClearTaskRecord();
            BeanUtils.copyProperties(atmClearTask,taskRecord);
            AtmDevice atmDevice = atmDeviceMap.get(atmClearTask.getAtmId());
            if (null != atmDevice) {
                taskRecord.setAtmNo(atmDevice.getTerNo());
            }
            return taskRecord;
        }).collect(Collectors.toList());
        atmClearTaskRouteDTO.setTaskRecordList(taskRecords);

        return atmClearTaskRouteDTO;
    }

    /**
     * 根据钞盒或钞袋编号，查询清分任务列表
     * @param boxBarCode
     * @param taskDay
     * @return
     */
    public AtmClearTaskDTO getClearTaskDetail(String boxBarCode,String taskDay) throws BusinessException {

        List<AtmTaskReturn> returnList = atmTaskReturnService.listByBoxNo(boxBarCode,taskDay);
        if (returnList.size() == 0 ) {
            log.info("无此钞袋记录: " + boxBarCode + " taskDay: " + taskDay);
            throw new BusinessException("无此钞袋记录");
        }

        //如果有多个回笼记录，取第一个未清分的回笼记录，否则返回第一个
        AtmTaskReturn atmTaskReturn = returnList.get(0);
        for (AtmTaskReturn atmTaskReturn1 : returnList) {
            if (atmTaskReturn1.getClearFlag() == 0) {
                atmTaskReturn = atmTaskReturn1;
                break;
            }
        }

        Route route = routeService.getById(atmTaskReturn.getRouteId());
        if (null == route) {
            log.error("无效线路id：" + atmTaskReturn.toString());
            throw new BusinessException("无效线路ID");
        }
        //查询当日所有未清分任务
        List<AtmClearTask> atmClearTaskList = atmClearTaskService.listByAtmId(atmTaskReturn.getAtmId(),taskDay);
        if (atmClearTaskList.size() == 0) {
            log.info("无此ATM清分任务: " + atmTaskReturn.getAtmId() + ",taskDay:" + taskDay);
            throw new BusinessException("无此ATM清分任务");
        }
        List<AtmClearTask> unFinishClearTask = new ArrayList<>();
        for (AtmClearTask task : atmClearTaskList) {
            if (task.getTaskId() == atmTaskReturn.getTaskId().longValue() && task.getStatusT() == 1) {
                ;
            } else {
                unFinishClearTask.add(task);
            }
        }
        if (unFinishClearTask.size() == 0) {
            log.info("钞袋已清点: "+boxBarCode +",taskDay:" + taskDay);
            throw new BusinessException("钞袋已清点");
        }

        //返回清分信息
        AtmClearTaskDTO atmClearTaskDTO = new AtmClearTaskDTO();
        atmClearTaskDTO.setTaskDate(taskDay);
        atmClearTaskDTO.setRouteId(atmTaskReturn.getRouteId());
        atmClearTaskDTO.setRouteNo(route.getRouteNo());
        atmClearTaskDTO.setBankId(atmTaskReturn.getBankId());
        atmClearTaskDTO.setTaskId(atmTaskReturn.getTaskId());
        Bank bank = bankService.getById(atmTaskReturn.getBankId());
        if (null != bank) {
            atmClearTaskDTO.setBankName(bank.getFullName());
        }
        AtmDevice atmDevice = atmDeviceService.getById(atmTaskReturn.getAtmId());
        if (null != bank) {
            atmClearTaskDTO.setAtmNo(atmDevice.getTerNo());
        }
        List<AtmClearTaskRecord> taskRecords = unFinishClearTask.stream().map(atmClearTask -> {
            AtmClearTaskRecord taskRecord = new AtmClearTaskRecord();
            BeanUtils.copyProperties(atmClearTask, taskRecord);
            return taskRecord;
        }).collect(Collectors.toList());
        atmClearTaskDTO.setTaskRecordList(taskRecords);

        List<AtmTaskReturn> atmTaskReturnList = atmTaskReturnService.listReturnBoxs(atmTaskReturn.getTaskId());
        Set<String> stringSet = atmTaskReturnList.stream().map(AtmTaskReturn::getBoxBarCode).collect(Collectors.toSet());
        if (stringSet.size() > 0) {
            atmClearTaskDTO.setRetBoxList(new ArrayList<>(stringSet));
        }
        return atmClearTaskDTO;
    }

    /**
     * 获取清机或加钞任务（包括撤销的）
     * @param atmTasks
     * @return
     */
    private AtmTask getCleanAtmTask(List<AtmTask> atmTasks) {
        if(atmTasks.size() > 0) {
            for (AtmTask atmTask : atmTasks) {
                if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                        || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
                    return atmTask;
                }
            }
        }
        return null;
    }
    /**
     * 根据id获取任务详情
     * @param id
     * @return
     */
    public AtmClearTaskDTO getClearTaskDetail(Long id) throws BusinessException {

        AtmClearTask atmClearTask = atmClearTaskService.getById(id);
        if (null == atmClearTask) {
            log.error("无效任务id="+id);
            throw new BusinessException("无效参数任务Id");
        }

        String taskDay = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(),"yyyy-MM-dd");
        List<AtmTaskReturn> atmTaskReturnList = atmTaskReturnService.listReturnByAtm(atmClearTask.getAtmId(),taskDay,0);
        if (atmTaskReturnList.size() > 0) {
            Set<Long> atmTaskIds = atmTaskReturnList.stream().map(AtmTaskReturn::getTaskId).collect(Collectors.toSet());
            if (atmTaskIds.size() > 1) { //一个ATM有两个加钞任务，无法知道哪个钞袋是对应本清分任务
                log.info("该设备有多条清机任务，请扫码钞袋开始");
                throw new BusinessException("该设备有多条清机任务，请扫码钞袋开始");
            }
        }

        Route route = routeService.getById(atmClearTask.getRouteId());
        if (null == route) {
            log.error("无效线路id = " + atmClearTask.getRouteId());
            throw new BusinessException("无线路信息");
        }
        //返回清分信息
        AtmClearTaskDTO atmClearTaskDTO = new AtmClearTaskDTO();
        atmClearTaskDTO.setTaskDate(taskDay);
        atmClearTaskDTO.setRouteId(route.getId());
        atmClearTaskDTO.setRouteNo(route.getRouteNo());
        atmClearTaskDTO.setBankId(atmClearTask.getBankId());
        List<String> stringList = atmTaskReturnList.stream().map(AtmTaskReturn::getBoxBarCode).collect(Collectors.toList());
        atmClearTaskDTO.setRetBoxList(stringList);
        if (atmTaskReturnList.size() > 0) { //撤销任务的ATM可能存在清分对账单，但是没有回笼钞袋
            atmClearTaskDTO.setTaskId(atmTaskReturnList.get(0).getTaskId());
        } else {
            //没有回笼钞袋，可能是撤销任务，从任务列表中查找
            List<AtmTask> atmTaskList = atmTaskService.listTaskByAtmId(route.getId(),atmClearTask.getAtmId());
            AtmTask atmTask = getCleanAtmTask(atmTaskList);
            if (null != atmTask) {
                atmClearTaskDTO.setTaskId(atmTask.getId());
            }
        }
        Bank bank = bankService.getById(atmClearTask.getBankId());
        if (null != bank) {
            atmClearTaskDTO.setBankName(bank.getFullName());
        }
        AtmDevice atmDevice = atmDeviceService.getById(atmClearTask.getAtmId());
        if (null != bank) {
            atmClearTaskDTO.setAtmNo(atmDevice.getTerNo());
        }
        List<AtmClearTaskRecord> taskRecords = new ArrayList<>();
        AtmClearTaskRecord atmClearTaskRecord = new AtmClearTaskRecord();
        BeanUtils.copyProperties(atmClearTask,atmClearTaskRecord);
        atmClearTaskRecord.setAtmNo(atmDevice.getTerNo());
        taskRecords.add(atmClearTaskRecord);
        atmClearTaskDTO.setTaskRecordList(taskRecords);
        return atmClearTaskDTO;
    }

    /**
     * 保存差错明细
     * @param errorDTOList
     */
    private void saveAtmClearError(AtmClearTask atmClearTask,List<AtmClearErrorDTO> errorDTOList, ClearErrorDetailTypeEnum clearErrorDetailTypeEnum) {
        if (null != errorDTOList && errorDTOList.size() > 0) {
            List<AtmClearError> atmClearErrors = errorDTOList.stream().map(atmClearErrorDTO -> {
                AtmClearError atmClearError = new AtmClearError();
                atmClearError.setAtmId(atmClearTask.getAtmId());
                atmClearError.setTaskId(atmClearTask.getId());
                atmClearError.setDenomId(atmClearErrorDTO.getDenomId());
                if (clearErrorDetailTypeEnum == ClearErrorDetailTypeEnum.FAKE) {
                    atmClearError.setCount(1);
                } else {
                    atmClearError.setCount(atmClearErrorDTO.getCount());
                }
                atmClearError.setDetailType(clearErrorDetailTypeEnum.getValue());
                Denom denom = denomService.getById(atmClearErrorDTO.getDenomId());
                if (denom != null) {
                    atmClearError.setAmount(denom.getValue().multiply(BigDecimal.valueOf(atmClearErrorDTO.getCount())));
                }
                if (clearErrorDetailTypeEnum == ClearErrorDetailTypeEnum.FAKE) {
                    if (!StringUtils.isEmpty(atmClearErrorDTO.getRmbSn())) {
                        atmClearError.setRmbSn(atmClearErrorDTO.getRmbSn());
                    }
                }
                return atmClearError;
            }).collect(Collectors.toList());

            atmClearErrorService.saveBatch(atmClearErrors);
        }
    }

//    private void updateReturnBoxClearFlag()

    /**
     * 保存atm清分结果
     * @param atmClearResultDTO
     */
    @Transactional
    public void saveAtmClearResult(AtmClearResultDTO atmClearResultDTO) throws BusinessException{
        AtmClearTask atmClearTask = atmClearTaskService.getById(atmClearResultDTO.getTaskId());
        if (null == atmClearTask || atmClearTask.getStatusT() != AtmClearTaskStatusEnum.UNDO.getValue()) {
            log.error("无效任务,data = "+atmClearResultDTO.toString());
            throw new BusinessException("无效任务");
        }
        AtmClearTask newAtmClearTask = new AtmClearTask();
        newAtmClearTask.setId(atmClearTask.getId());
        newAtmClearTask.setClearAmount(atmClearResultDTO.getClearAmount());
        newAtmClearTask.setErrorAmount(atmClearResultDTO.getClearAmount().subtract(atmClearTask.getPlanAmount()));
        newAtmClearTask.setClearMan(atmClearResultDTO.getClearMan());
        newAtmClearTask.setCheckMan(atmClearResultDTO.getCheckMan());
        newAtmClearTask.setClearTime(System.currentTimeMillis());
        newAtmClearTask.setStatusT(AtmClearTaskStatusEnum.FINISH.getValue());
        newAtmClearTask.setErrorType(atmClearResultDTO.getErrorType());
        newAtmClearTask.setTaskId(atmClearResultDTO.getAtmTaskId());
        boolean bExistError = atmClearResultDTO.getErrorType() != ClearErrorTypeEnum.NONE.getValue();
        if (bExistError && atmClearResultDTO.getErrorConfirmMan() != null) {
            newAtmClearTask.setErrorConfirmMan(atmClearResultDTO.getErrorConfirmMan());
        }
        /**如果是当日一台ATM两个清机任务分给不同的线路执行，则导入时，无法确定该清分任务对应哪条线路
        *清分时由清点员选定清分任务与清机线路
         */
        if (atmClearTask.getRouteId() == 0L) {
            newAtmClearTask.setRouteId(atmClearResultDTO.getRouteId());
        }
        atmClearTaskService.updateById(newAtmClearTask);

        this.saveAtmClearError(atmClearTask,atmClearResultDTO.getFakeErrorList(),ClearErrorDetailTypeEnum.FAKE);
        this.saveAtmClearError(atmClearTask,atmClearResultDTO.getBadErrorList(),ClearErrorDetailTypeEnum.BAD);
        this.saveAtmClearError(atmClearTask,atmClearResultDTO.getCarryErrorList(),ClearErrorDetailTypeEnum.CARRY);

        /**
         * 回笼钞盒变成已清分状态
         */
        List<AtmTaskReturn>  atmTaskReturnList = atmTaskReturnService.listReturnBoxs(atmClearResultDTO.getAtmTaskId());
        if (atmTaskReturnList.size() > 0) {
            List<AtmTaskReturn> newAtmTaskReturnList = new ArrayList<>();
            for (AtmTaskReturn atmTaskReturn : atmTaskReturnList) {
                AtmTaskReturn newAtmTaskReturn = new AtmTaskReturn();
                newAtmTaskReturn.setId(atmTaskReturn.getId());
                newAtmTaskReturn.setClearFlag(1);
                newAtmTaskReturnList.add(newAtmTaskReturn);
            }
            atmTaskReturnService.updateBatchById(newAtmTaskReturnList);
        }
    }


    /**
     * 保存钞盒封装记录
     * @param device
     * @param denom
     * @param cashboxMap
     * @param cashboxClearDTO
     * @throws BusinessException
     */
    @Transactional
    public void saveCashboxPackRecord(Long departmentId, Device device,Denom denom,Map<String,Cashbox> cashboxMap,CashboxClearDTO cashboxClearDTO) throws BusinessException {

        List<CashboxScanRecord> scanRecords = new ArrayList<>();
        cashboxClearDTO.getCashboxClearTimeDTOS().forEach(cashboxClearTimeDTO -> {
            Cashbox cashbox = cashboxMap.get(cashboxClearTimeDTO.getBoxNo());
            if (null == cashbox) {
                BusinessException exception = new BusinessException("无效钞盒:"+cashboxClearTimeDTO.getBoxNo());
                log.warn(exception.getMessage());
                throw exception;
            }
            CashboxPackRecord cashboxPackRecord = new CashboxPackRecord();
            cashboxPackRecord.setDepartmentId(departmentId);
            cashboxPackRecord.setBoxNo(cashbox.getBoxNo());
            cashboxPackRecord.setClearManId(cashboxClearDTO.getClearMan());
            cashboxPackRecord.setCheckManId(cashboxClearDTO.getCheckMan());
            cashboxPackRecord.setBankId(cashboxClearDTO.getBankId());
            cashboxPackRecord.setDevId(device.getId());
            cashboxPackRecord.setDenomId(denom.getId());
            BigDecimal amount = denom.getValue().multiply(BigDecimal.valueOf(denom.getWadSize() * denom.getBundleSize()));
            cashboxPackRecord.setAmount(amount);
            cashboxPackRecord.setPackTime(cashboxClearTimeDTO.getClearTime());
            cashboxPackRecord.setTaskDate(cashboxClearTimeDTO.getClearTime());
            cashboxPackRecord.setStatusT(BoxStatusEnum.PACK.getValue());
            cashboxPackRecordService.save(cashboxPackRecord);

            //将当前packid回写到 cashbox中
            Cashbox newCashbox = new Cashbox();
            newCashbox.setId(cashbox.getId());
            newCashbox.setPackId(cashboxPackRecord.getId());
            cashboxService.updateById(newCashbox);

            //TODO 可以考虑采用消息队列异步处理
            //记录扫描过程
            CashboxScanRecord scanRecord = new CashboxScanRecord();
            scanRecord.setPackId(cashboxPackRecord.getId());
            scanRecord.setScanUser(UserContextHolder.getUserId());
            scanRecord.setScanTime(System.currentTimeMillis());
            scanRecord.setScanNode(BoxScanNodeEnum.PACK.getValue());
            scanRecords.add(scanRecord);
        });
        cashboxScanRecordService.saveBatch(scanRecords);
    }

    /**
     * 查重 2 秒内同一台设备提交为重复提交
     * @return
     */
    private synchronized boolean checkBoxMappingRepeat(String deviceNo) {
        if (!StringUtils.isEmpty(deviceNo)) {
            String key = CacheKeysDef.PdaBoxMaping+deviceNo;
            boolean bHasKey = redisUtil.hasKey(key);
            if (bHasKey) {
                return true;
            } else {
                redisUtil.setEx(key,"0",2, TimeUnit.SECONDS);
            }
        }
        return false;
    }

    /**
     * 保存钞盒清分绑定记录
     * @param cashboxClearDTO
     */
    public void bindCashbox(CashboxClearDTO cashboxClearDTO) throws BusinessException {
        Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
        if (employee == null) {
            log.warn("系统错误: employee == null");
            throw new BusinessException("系统错误");
        }
        if (checkBoxMappingRepeat(cashboxClearDTO.getDeviceNo())) {
            log.warn("重复请求: "+ cashboxClearDTO.toString());
            throw new BusinessException("重复操作");
        }
        if (cashboxClearDTO.getCashboxClearTimeDTOS() == null || cashboxClearDTO.getCashboxClearTimeDTOS().size() == 0) {
            log.warn("无效参数: "+ cashboxClearDTO.toString());
            throw new BusinessException("无效参数");
        }

        Device device = deviceService.getByDevNo(cashboxClearDTO.getDeviceNo());
        if (null == device) {
            log.warn("清分设备不存在: "+ cashboxClearDTO.getDeviceNo());
            throw new BusinessException("清分设备不存在");
        }
        //TODO
        //防重处理
        List<String> boxNoList = cashboxClearDTO.getCashboxClearTimeDTOS().stream().map(CashboxClearTimeDTO::getBoxNo).collect(Collectors.toList());
        List<Cashbox> cashboxList = cashboxService.listByBatchBoxNo(boxNoList);
        Map<String,Cashbox> cashboxMap = cashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo, Function.identity(),(key1,key2)->key2));
        Set<Long> packIds = cashboxList.stream().map(Cashbox::getPackId).collect(Collectors.toSet());
        for (String boxNo : boxNoList) {
            if (cashboxMap.get(boxNo) == null) {
                BusinessException exception = new BusinessException("无效钞盒："+ boxNo);
                log.warn(exception.getMessage());
                throw exception;
            }
        }
        //查询原有绑定钞盒状态
        packIds.remove(0L); //过滤初始化packid
        if (packIds.size() > 0) {
            Long taskDate = DateTimeUtil.getDailyStartTimeMs(System.currentTimeMillis());
            List<String> repackList = new ArrayList<>(); //当日重绑列表
            List<String> unOpenList = new ArrayList<>(); //未拆封重绑列表

            List<CashboxPackRecord> cashboxPackRecordList = cashboxPackRecordService.listByIds(packIds);
            for (CashboxPackRecord packRecord : cashboxPackRecordList) {
                if (taskDate.equals(packRecord.getTaskDate())) {
                    //当日绑定未使用，则提示重复绑定
                    if (packRecord.getStatusT() != BoxStatusEnum.OPEN.getValue()) {
                        repackList.add(packRecord.getBoxNo());
                    }
                } else if (packRecord.getStatusT() != BoxStatusEnum.OPEN.getValue()) {
                    //不是当日绑定的钞盒，原因是上次出库未扫码，需要重置钞盒状态处理
                    unOpenList.add(packRecord.getBoxNo());
                }
            }
            if (repackList.size() > 0) {
                BusinessException exception = new BusinessException("重复绑定："+repackList.toString());
                log.warn(exception.getMessage());
                throw exception;
            }
            if (unOpenList.size() > 0) {
                BusinessException exception = new BusinessException("重复绑定或加钞未扫码："+unOpenList.toString());
                log.warn(exception.getMessage());
                throw exception;
            }
        }

        Denom denom = denomService.getByAtmDenomValue(cashboxClearDTO.getDenomValue());
        if (null == denom) {
            BusinessException exception = new BusinessException("券别不存在: "+cashboxClearDTO.getDenomValue());
            log.error(exception.getMessage());
            throw exception;
        }
        this.saveCashboxPackRecord(employee.getDepartmentId(),device,denom,cashboxMap,cashboxClearDTO);
    }


    /**
     * 原装钞盒归还登记
     * @param batchBoxNosVO
     */
    public void orgPackingBoxReturn(BatchBoxNosVO batchBoxNosVO)  throws BusinessException{
        List<String> boxNos = batchBoxNosVO.getBoxNos();
        if (boxNos.size() > 0) {
            List<Cashbox> cashboxList = cashboxService.listByBatchBoxNo(boxNos);
            Map<String,Cashbox> cashboxMap = cashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
            Set<Long> packIds = new HashSet<>();
            for (String boxNo : boxNos) {
                Cashbox cashbox = cashboxMap.get(boxNo);
                if (null == cashbox) {
                    BusinessException exception = new BusinessException("钞盒:"+boxNo+"不存在");
                    log.warn(exception.getMessage());
                    throw exception;
                }
                if (cashbox.getPackId() == 0L) {
                    BusinessException exception = new BusinessException("钞盒:"+cashbox.getBoxNo()+"未包装");
                    log.warn(exception.getMessage());
                    throw exception;
                }
                packIds.add(cashbox.getPackId());
            }
            if (packIds.size() > 0) {
                List<CashboxPackRecord> packRecords = cashboxPackRecordService.listByIds(packIds);
                List<CashboxPackRecord> newPackRecords = new ArrayList<>();
                List<CashboxScanRecord> scanRecords = new ArrayList<>();
                for (CashboxPackRecord packRecord : packRecords) {
                    if (packRecord.getStatusT() == BoxStatusEnum.OPEN.getValue()) {
                        BusinessException exception = new BusinessException("钞盒:"+packRecord.getBoxNo()+"已使用");
                        log.warn(exception.getMessage());
                        throw exception;
                    }
                    Long curTime = System.currentTimeMillis();
                    Long today = DateTimeUtil.getDailyStartTimeMs(curTime);
                    Long taskDate = DateTimeUtil.getDailyStartTimeMs(packRecord.getTaskDate());
                    if (packRecord.getStatusT() == BoxScanNodeEnum.PACK.getValue()
                        && taskDate.equals(today)) {
                        BusinessException exception = new BusinessException("钞盒:"+packRecord.getBoxNo()+"重复操作");
                        log.warn(exception.getMessage());
                        throw exception;
                    }
                    CashboxPackRecord newPackRecord = new CashboxPackRecord();
                    newPackRecord.setId(packRecord.getId());
                    newPackRecord.setStatusT(BoxStatusEnum.PACK.getValue());
                    newPackRecord.setRecvCheckManId(batchBoxNosVO.getCheckMan());
                    newPackRecord.setRecvClearManId(batchBoxNosVO.getClearMan());
                    newPackRecord.setTaskDate(curTime);
                    newPackRecords.add(newPackRecord);

                    //TODO 可以考虑采用消息队列异步处理
                    //记录扫描过程
                    CashboxScanRecord scanRecord = new CashboxScanRecord();
                    scanRecord.setPackId(packRecord.getId());
                    scanRecord.setScanUser(UserContextHolder.getUserId());
                    scanRecord.setScanTime(curTime);
                    scanRecord.setScanNode(BoxScanNodeEnum.RETURN.getValue());
                    scanRecords.add(scanRecord);
                }
                cashboxPackRecordService.updateBatchById(newPackRecords); //恢复状态
                cashboxScanRecordService.saveBatch(scanRecords); //记录扫描过程
            }
        }
    }

    /**
     * 解绑钞盒
     * @param boxNos
     * @throws BusinessException
     */
    public void unBindCashbox(List<String> boxNos)  throws BusinessException{
        if (boxNos.size() > 0) {
            List<Cashbox> cashboxList = cashboxService.listByBatchBoxNo(boxNos);
            Map<String,Cashbox> cashboxMap = cashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
            Set<Long> packIds = new HashSet<>();
            for (String boxNo : boxNos) {
                Cashbox cashbox = cashboxMap.get(boxNo);
                if (null == cashbox) {
                    BusinessException exception = new BusinessException("钞盒:"+boxNo+"不存在");
                    log.warn(exception.getMessage());
                    throw exception;
                }
                if (cashbox.getPackId() != 0L) {
                    packIds.add(cashbox.getPackId());
                }
            }
            if (packIds.size() > 0) {
                List<CashboxPackRecord> packRecords = cashboxPackRecordService.listByIds(packIds);
                List<CashboxPackRecord> newPackRecords = new ArrayList<>();
                List<CashboxScanRecord> scanRecords = new ArrayList<>();
                for (CashboxPackRecord packRecord : packRecords) {
                    //未拆封的钞盒都可做解绑操作
                    if (packRecord.getStatusT() != BoxStatusEnum.OPEN.getValue()) {
                        CashboxPackRecord newPackRecord = new CashboxPackRecord();
                        newPackRecord.setId(packRecord.getId());
                        newPackRecord.setStatusT(BoxStatusEnum.OPEN.getValue());
                        newPackRecords.add(newPackRecord);

                        //TODO 可以考虑采用消息队列异步处理
                        //记录扫描过程
                        CashboxScanRecord scanRecord = new CashboxScanRecord();
                        scanRecord.setPackId(packRecord.getId());
                        scanRecord.setScanUser(UserContextHolder.getUserId());
                        scanRecord.setScanTime(System.currentTimeMillis());
                        scanRecord.setScanNode(BoxScanNodeEnum.UNBIND.getValue());
                        scanRecords.add(scanRecord);
                    }
                }
                cashboxPackRecordService.updateBatchById(newPackRecords);
                cashboxScanRecordService.saveBatch(scanRecords); //记录扫描过程
            }
        }
    }

    /**
     * 根据版本标志查询券别列表
     * @param version - 版本标志
     * @param attr - 纸硬币属性
     * @return
     */
    private List<AtmClearDenomRecord> listDenomByVersion(int version,String attr) {
        Denom where = new Denom();
        where.setVersion(version);
        where.setAttr(attr);
        List<Denom> denoms = denomService.listByCondition(where);
        List<AtmClearDenomRecord> denomRecords = denoms.stream().map(denom -> {
            AtmClearDenomRecord denomRecord = new AtmClearDenomRecord();
            denomRecord.setId(denom.getId());
            denomRecord.setName(denom.getName());
            return denomRecord;
        }).collect(Collectors.toList());

        return denomRecords;
    }

    /**
     * 查询清分券别明细
     * @return
     */
    public AtmClearDenomDTO queryDenomList() {

        //无版本券别
        List<AtmClearDenomRecord> denomRecords = this.listDenomByVersion(0, DenomAttrEnum.PAPER.getValue());
        //残缺券别
        List<AtmClearDenomRecord> badDenomRecords = this.listDenomByVersion(2,DenomAttrEnum.PAPER.getValue());
        AtmClearDenomDTO atmClearDenomDTO = new AtmClearDenomDTO();
        atmClearDenomDTO.setDenoms(denomRecords);
        atmClearDenomDTO.setBadDenoms(badDenomRecords);
        return atmClearDenomDTO;
    }

    /**
     * 查询清分银行列表
     * @return
     */
    public List<AtmClearBankDTO> listClearBank() {
        Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
        if (null == employee) {
            return new ArrayList<>();
        }
        List<Bank> bankList = bankService.listAllTopBank(employee.getDepartmentId());

        List<AtmClearBankDTO> atmClearBankDTOS = bankList.stream().map(bank -> {
            AtmClearBankDTO bankDTO = new AtmClearBankDTO();
            bankDTO.setBankName(bank.getFullName());
            bankDTO.setId(bank.getId());
            return bankDTO;
        }).collect(Collectors.toList());
        return atmClearBankDTOS;
    }

    /**
     * 按线路统计我的清分数据，当参数为空时，统计当天的清分数据
     */
    public AtmClearTotalDTO calcMyClearTotalByRoute(Long beginDt, Long endDt) {

        Long userId = UserContextHolder.getUserId();
//        String taskDate = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(),"yyyy-MM-dd");
        Long beginTime = beginDt;
        if (null == beginTime || 0L == beginTime ) {
            beginTime = DateTimeUtil.getDailyStartTimeMs(System.currentTimeMillis());
        }
        Long endTime = endDt;
        if (null == endTime || 0L == endTime ) {
            endTime = DateTimeUtil.getDailyEndTimeMs(System.currentTimeMillis());
        }
        List<AtmClearSumDTO> sumDTOList = atmClearTaskService.sumClearAmountByRoute(userId,beginTime,endTime);

        //统计用户清分绑定的钞盒数量（未入库）
        List<AtmClearBoxCountDTO> countDTOList = cashboxPackRecordService.countClearBoxByBank(beginTime,endTime,BoxStatusEnum.PACK.getValue(),userId);

        Set<Long> bankIds = sumDTOList.stream().map(AtmClearSumDTO::getBankId).collect(Collectors.toSet());
        Set<Long> tmpBankIds = countDTOList.stream().map(AtmClearBoxCountDTO::getBankId).collect(Collectors.toSet());
        bankIds.addAll(tmpBankIds);
        Map<Long,Bank> bankMap = new HashMap<>();
        if (bankIds.size() > 0) {
            List<Bank> bankList = bankService.listByIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2)->key2));
        }

        if (sumDTOList.size() > 0) {
            Set<Long> routeIds = sumDTOList.stream().map(AtmClearSumDTO::getRouteId).collect(Collectors.toSet());
            List<Route> routeList = routeService.listByIds(routeIds);
            Map<Long,Route> routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,Function.identity(),(key1,key2)->key2));
            for (AtmClearSumDTO atmClearSumDTO : sumDTOList) {
                Route route = routeMap.get(atmClearSumDTO.getRouteId());
                atmClearSumDTO.setRouteName(route == null ? "" : route.getRouteNo() + "号线");
                Bank bank = bankMap.get(atmClearSumDTO.getBankId());
                atmClearSumDTO.setBankName(bank == null ? "" : bank.getFullName());
            }
        }
        if (countDTOList.size() > 0) {
            for (AtmClearBoxCountDTO atmClearBoxCountDTO : countDTOList) {
                Bank bank = bankMap.get(atmClearBoxCountDTO.getBankId());
                atmClearBoxCountDTO.setBankName(bank == null ? "" : bank.getFullName());
            }
        }

        AtmClearTotalDTO atmClearTotalDTO = new AtmClearTotalDTO();
        atmClearTotalDTO.setClearAmountSums(sumDTOList);
        atmClearTotalDTO.setClearBoxCounts(countDTOList);

        return atmClearTotalDTO;
    }

//    /**
//     * 查询当日绑定钞盒列表
//     * @param taskDate
//     * @return
//     */
//    public List<String> queryMyCashboxList(Long taskDate) throws BusinessException {
//        try {
//            List<CashboxPackRecord> records = cashboxPackRecordService.listByUser(UserContextHolder.getUserId(),taskDate);
//            List<String> boxList = records.stream().map(CashboxPackRecord::getBoxNo).collect(Collectors.toList());
//            return boxList;
//        } catch (Exception e) {
//            throw new BusinessException("处理异常");
//        }
//    }
    /**
     * 查询绑定钞盒列表
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<String> queryBindCashbox(Long beginTime, Long endTime,Long bankId) throws BusinessException {
        try {
            Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
            if (null == employee) {
                log.error("查询错误：null == employee");
                return new ArrayList<>();
            }
            List<String> boxNos = new ArrayList<>();
            List<CashboxPackRecord> packRecords = cashboxPackRecordService.listBindsByTime(employee.getDepartmentId(),beginTime,endTime,bankId);
            if (0 < packRecords.size()) {
                for (CashboxPackRecord packRecord : packRecords) {
                    if (BoxStatusEnum.PACK.getValue() == packRecord.getStatusT()) {
                        boxNos.add(packRecord.getBoxNo());
                    }
                }
            }
           return boxNos;
        } catch (Exception e) {
            log.error("查询失败",e);
            throw new BusinessException("查询失败");
        }
    }
}
