package com.zcxd.pda.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.JacksonUtil;
import com.zcxd.db.model.*;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.*;
import com.zcxd.pda.exception.BaseException;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.exception.SystemErrorType;
import com.zcxd.pda.service.impl.*;
import com.zcxd.pda.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName RouteManagerService
 * @Description 线路管理服务
 * @author 秦江南
 * @Date 2021年5月7日上午10:50:05
 */
@Service
@Slf4j
public class RouteManagerService {

    public static final int HALF_BOX = 1; //半盒
    public static final int FULL_BOX = 2; //整合
    public static final int extraDenomValue = 10;
    @Resource
    private RouteService routeService;
    @Resource
    private AtmTaskService atmTaskService;
    @Resource
    private AtmTaskRepairRecordService atmTaskRepairRecordService;
    @Resource
    private AtmTaskCheckRecordService atmTaskCheckRecordService;
    @Resource
    private AtmBankCheckRecordService atmBankCheckRecordService;
    @Resource
    private AtmTaskReturnService atmTaskReturnService;
    @Resource
    private AtmTaskCardService atmTaskCardService;
    @Resource
    private AtmDeviceService atmDeviceService;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private CashboxService cashboxService;
    @Resource
    private CashboxPackRecordService cashboxPackRecordService;
    @Resource
    private CashboxScanRecordService cashboxScanRecordService;
    @Resource
    private BankService bankService;
    @Resource
    private DenomService denomService;
    @Resource
    private AtmAdditionCashService atmAdditionCashService;
    @Resource
    private RouteLogService routeLogService;
    @Resource
    private SysDictionaryService sysDictionaryService;
    @Resource
    private RedisUtil redisUtil;

    /**
     * 查询钞线路列表
     * @param dateTime
     * @param routeStatusEnum
     * @return
     */
    public List<RouteDTO> listRoute(Long dateTime, RouteStatusEnum routeStatusEnum) {
        Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
        if (null == employee) {
            return new ArrayList<>();
        }
        List<Route> routeList = routeService.listRoute(employee.getDepartmentId(),dateTime,routeStatusEnum);
        List<RouteDTO> routeDTOList = routeList.stream().map(route -> {
            RouteDTO routeDTO = new RouteDTO();
            BeanUtils.copyProperties(route,routeDTO);
            return  routeDTO;
        }).collect(Collectors.toList());
        return routeDTOList;
    }

    /**
     * 查询业务员关联线路列表
     * @param dateTime
     * @param userId
     * @return
     */
    public List<RouteDTO> listRouteByMan(Long dateTime, Long userId) {

        List<Route> routeList = routeService.listRouteByMan(dateTime,userId);
        List<RouteDTO> routeDTOList = routeList.stream().map(route -> {
            RouteDTO routeDTO = new RouteDTO();
            BeanUtils.copyProperties(route,routeDTO);
            Vehicle vehicle = vehicleService.getById(route.getVehicleId());
            if (null != vehicle) {
                routeDTO.setVehicleNo(vehicle.getLpno());
            }
            //统计任务数
            List<AtmTask> atmTaskList = atmTaskService.listByCarryRouteId(route.getId(),null);
            routeDTO.setTaskTotal(countValideTask(atmTaskList));
            routeDTO.setUnfinishTotal(countUnfinishTask(atmTaskList));
            routeDTO.setAllowHandover(routeDTO.getUnfinishTotal() == 0 ? 1 : 0);
            Set<Long> empIds = new HashSet<>();
            empIds.add(route.getDriver());
            empIds.add(route.getSecurityA());
            empIds.add(route.getSecurityB());
            empIds.add(route.getRouteKeyMan());
            empIds.add(route.getRouteOperMan());
            List<Employee> employeeList = employeeService.listByIds(empIds);
            Map<Long,String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName,(key1,key2)->key2));

            routeDTO.setDriverName(employeeMap.getOrDefault(route.getDriver(),""));
            routeDTO.setSecurityAName(employeeMap.getOrDefault(route.getSecurityA(),""));
            routeDTO.setSecurityBName(employeeMap.getOrDefault(route.getSecurityB(),""));
            routeDTO.setKeyManName(employeeMap.getOrDefault(route.getRouteKeyMan(),""));
            routeDTO.setOperManName(employeeMap.getOrDefault(route.getRouteOperMan(),""));

            return  routeDTO;
        }).collect(Collectors.toList());
        return routeDTOList;
    }

    /**
     * 统计线路分配金额
     * @param cashboxPackRecords
     * @return
     */
    private Map<String,BankDenomSumDTO> sumDispatchAmount(List<CashboxPackRecord> cashboxPackRecords) {
        Map<String,BankDenomSumDTO> bankDenomSumDTOMap = new HashMap<>();
        for (CashboxPackRecord cashboxPackRecord : cashboxPackRecords) {
            String key = cashboxPackRecord.getBankId().toString() + "_" + cashboxPackRecord.getDenomId();

            BankDenomSumDTO sumDTO = bankDenomSumDTOMap.get(key);
            if (null == sumDTO) {
                sumDTO = new BankDenomSumDTO();
                sumDTO.setTotalAmount(cashboxPackRecord.getAmount());
                sumDTO.setBoxCount(1.0F);
                sumDTO.setTaskCount(0);
                sumDTO.setBankId(cashboxPackRecord.getBankId());
                sumDTO.setDenomId(cashboxPackRecord.getDenomId());
                bankDenomSumDTOMap.put(key,sumDTO);
            } else {
                sumDTO.setTotalAmount(sumDTO.getTotalAmount().add(cashboxPackRecord.getAmount()));
                sumDTO.setBoxCount(sumDTO.getBoxCount()+1);
            }
        }
        return bankDenomSumDTOMap;
    }

    /**
     * 统计线路已确认，已完成的任务数量及金额
     * @param atmTaskList
     * @return
     */
    private Map<String,BankDenomSumDTO> sumAtmTaskAmount(List<AtmTask> atmTaskList,boolean firstTime) {
        Map<Long, Denom> denomMap = getDenomIdMap();
        Map<Integer, Denom> denomMap1 = getDenomMap();
        Denom hundredDenom = denomMap1.get(100);
        Long hundredDenomId = hundredDenom.getId();
        Denom tenDenom = denomMap1.get(extraDenomValue);
        Long tenDenomId = tenDenom.getId();

        Map<String,BankDenomSumDTO> bankDenomSumDTOMap = new HashMap<>();
        for (AtmTask atmTask : atmTaskList) {

            boolean bisCleanTask = (atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue() || atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue());
            if (!bisCleanTask) { //不是加钞任务
                continue;
            }
            if (firstTime) { //首次按导入任务单统计，不管是否撤销
                if (atmTask.getImportBatch() == 0L || atmTask.getStatusT() == AtmTaskStatusEnum.CREATE.getValue()) { //不是导入任务不统计
                    continue;
                }
            } else { //非首次导入，按已确认及完成的任务统计
                if (atmTask.getStatusT() < AtmTaskStatusEnum.CONFRIM.getValue()) {
                    continue;
                }
            }
            Float hundredBoxNum = countHundredBoxNum(atmTask.getAmount(), hundredDenom);

            // 百元
            String hundredKey = atmTask.getBankId() + "_" + hundredDenomId;
            BankDenomSumDTO hundredSumDTO = bankDenomSumDTOMap.get(hundredKey);
            float hundredAmount = hundredBoxNum * hundredDenom.getBundleSize() *
                    hundredDenom.getWadSize() * hundredDenom.getValue().intValue();
            BigDecimal hundredBigDecimal = BigDecimal.valueOf(hundredAmount).setScale(0);
            if (null == hundredSumDTO) {
                hundredSumDTO = new BankDenomSumDTO();
                hundredSumDTO.setTotalAmount(hundredBigDecimal);
                hundredSumDTO.setBoxCount(hundredBoxNum);
                hundredSumDTO.setTaskCount(1);
                hundredSumDTO.setBankId(atmTask.getBankId());
                hundredSumDTO.setDenomId(hundredDenomId);
                bankDenomSumDTOMap.put(hundredKey, hundredSumDTO);
            } else {
                hundredSumDTO.setTotalAmount(hundredSumDTO.getTotalAmount().add(hundredBigDecimal));
                hundredSumDTO.setTaskCount(hundredSumDTO.getTaskCount()+1);
                hundredSumDTO.setBoxCount(hundredSumDTO.getBoxCount() + hundredBoxNum);
            }

            // 10
            int tenValue = tenDenom.getValue().intValue() * tenDenom.getWadSize() * tenDenom.getBundleSize();
            String tenKey = atmTask.getBankId() + "_" + tenDenomId;
            BankDenomSumDTO tenSumDTO = bankDenomSumDTOMap.get(tenKey);
            float tenAmount = atmTask.getAmount().subtract(BigDecimal.valueOf(hundredAmount)).floatValue();
            BigDecimal tenBigDecimal = BigDecimal.valueOf(tenAmount).setScale(0);
            float tenBoxNum = tenAmount / tenValue;

            if (null == tenSumDTO) {
                tenSumDTO = new BankDenomSumDTO();
                tenSumDTO.setTotalAmount(tenBigDecimal);
                tenSumDTO.setBoxCount(tenBoxNum);
                tenSumDTO.setTaskCount(1);
                tenSumDTO.setBankId(atmTask.getBankId());
                tenSumDTO.setDenomId(tenDenomId);
                bankDenomSumDTOMap.put(tenKey,tenSumDTO);
            } else {
                tenSumDTO.setTotalAmount(tenSumDTO.getTotalAmount().add(tenBigDecimal));
                tenSumDTO.setTaskCount(tenSumDTO.getTaskCount()+1);
                tenSumDTO.setBoxCount(tenSumDTO.getBoxCount() + tenBoxNum);
            }
        }
        return bankDenomSumDTOMap;
    }

    /**
     * 统计已确认的备用金
     * @param atmAdditionCashes
     * @return
     */
    private Map<String,BankDenomSumDTO> sumAdditionAmount(List<AtmAdditionCash> atmAdditionCashes) {
        Map<Integer, Denom> denomMap = getDenomMap();
        Map<String,BankDenomSumDTO> bankDenomSumDTOMap = new HashMap<>();
        for (AtmAdditionCash additionCash : atmAdditionCashes) {
            //撤销未确认的任务不统计
            if (additionCash.getStatusT() < AtmTaskStatusEnum.CONFRIM.getValue()) {
                continue;
            }
            String key = additionCash.getBankId()+"_"+additionCash.getDenomId();
            Denom denom = denomMap.get(additionCash.getDenomValue());
            long oneBoxAmount = (long) denom.getValue().intValue() * denom.getBundleSize() * denom.getWadSize();
            BankDenomSumDTO sumDTO = bankDenomSumDTOMap.get(key);
            if (null == sumDTO) {

                sumDTO = new BankDenomSumDTO();
                sumDTO.setTotalAmount(additionCash.getAmount());
                sumDTO.setBoxCount(additionCash.getAmount().divide(
                        BigDecimal.valueOf(oneBoxAmount)).floatValue());
                sumDTO.setTaskCount(0);
                sumDTO.setBankId(additionCash.getBankId());
                sumDTO.setDenomId(additionCash.getDenomId());
                bankDenomSumDTOMap.put(key,sumDTO);
            } else {
                sumDTO.setTotalAmount(sumDTO.getTotalAmount().add(additionCash.getAmount()));
                sumDTO.setBoxCount(sumDTO.getBoxCount() + additionCash.getAmount().divide(
                        BigDecimal.valueOf(oneBoxAmount)).intValue());
            }
        }
        return bankDenomSumDTOMap;
    }

    class MyComparator implements Comparator<String>{
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * 统计有效任务
     * @param atmTaskList
     * @return
     */
    public int countValideTask(List<AtmTask> atmTaskList) {
        int count = 0;
        if (null != atmTaskList) {
            for(AtmTask atmTask : atmTaskList) {
                if (atmTask.getStatusT() >= AtmTaskStatusEnum.CONFRIM.getValue()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countUnfinishTask(List<AtmTask> atmTaskList) {
        int count = 0;
        if (null != atmTaskList) {
            for(AtmTask atmTask : atmTaskList) {
                if (atmTask.getStatusT() == AtmTaskStatusEnum.CONFRIM.getValue()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean existImportTask(List<AtmTask> atmTaskList) {
        for (AtmTask task : atmTaskList) {
            if (task.getImportBatch() != 0L) {
                return true;
            }
        }
        return false;
    }
    /**
     * 查询线路详情
     * @param routeId
     * @return
     */
    public RouteDetailDTO getRouteDiapatchDetail(Long routeId) throws BusinessException {
        Route route = routeService.getById(routeId);
        if (null == route) {
            log.info("无效线路id：" + routeId);
            throw new BusinessException("无效线路id");
        }
        RouteDetailDTO detailDTO = new RouteDetailDTO();
        BeanUtils.copyProperties(route,detailDTO);
        //车辆信息
        Vehicle vehicle = vehicleService.getById(route.getVehicleId());
        if (null != vehicle) {
            detailDTO.setVehicleLpNo(vehicle.getLpno());
        }

        Set<Long> bankIds = new HashSet<>();
        Set<Long> denomIds = new HashSet<>();
        Map<String,BankDenomSumDTO> taskBankDenomSumDTOMap = new HashMap<>();
        //任务统计
        List<AtmTask> atmTaskList = atmTaskService.listByCarryRouteId(routeId,null);
        if (atmTaskList.size() > 0) {
//            throw new BusinessException("找不到记录");
            detailDTO.setTaskTotal(countValideTask(atmTaskList));
            boolean firstTime = false; //首次任务单配钞
            if (route.getStatusT() < RouteStatusEnum.DISPATCH_CHECK.getValue()) {
                firstTime = existImportTask(atmTaskList);
            }
            taskBankDenomSumDTOMap = this.sumAtmTaskAmount(atmTaskList,firstTime);
            bankIds = atmTaskList.stream().map(AtmTask::getBankId).collect(Collectors.toSet());
            denomIds = atmTaskList.stream().map(AtmTask::getDenomId).collect(Collectors.toSet());
        }

        //备用金统计
        List<AtmAdditionCash> atmAdditionCashList = atmAdditionCashService.listByRoute(routeId);
        Set<Long> additionBankIds = atmAdditionCashList.stream().map(AtmAdditionCash::getBankId).collect(Collectors.toSet());
        Set<Long> addDenomIds = atmAdditionCashList.stream().map(AtmAdditionCash::getDenomId).collect(Collectors.toSet());
        Map<String,BankDenomSumDTO> additionBankDenomSumDTOMap = this.sumAdditionAmount(atmAdditionCashList);

        //备用金与任务金额合并
        if (additionBankDenomSumDTOMap.size() > 0) {
            for (String key : additionBankDenomSumDTOMap.keySet()) {
                BankDenomSumDTO bankDenomSumDTO = taskBankDenomSumDTOMap.get(key);
                if (null == bankDenomSumDTO) { //不存在，将备用金加入总金额
                    taskBankDenomSumDTOMap.put(key,additionBankDenomSumDTOMap.get(key));
                } else { //金额、盒数累加
                    bankDenomSumDTO.setTotalAmount(bankDenomSumDTO.getTotalAmount().add(additionBankDenomSumDTOMap.get(key).getTotalAmount()));
                    bankDenomSumDTO.setBoxCount(bankDenomSumDTO.getBoxCount() + additionBankDenomSumDTOMap.get(key).getBoxCount());
                }
            }
        }

        //配钞统计
        List<CashboxPackRecord> cashboxPackRecords = cashboxPackRecordService.listByRoute(routeId);
        Map<String,BankDenomSumDTO> dispBankDenomSumDTOMap = this.sumDispatchAmount(cashboxPackRecords);

        detailDTO.setDispBoxCount(cashboxPackRecords.size());
        //加载银行及券别信息
        bankIds.addAll(additionBankIds);
        List<Bank> bankList = new ArrayList<Bank>();
        if(bankIds.size()>0){
        	bankList = bankService.listByIds(bankIds);
        }

        denomIds.addAll(addDenomIds);
        List<Denom> denomList = new ArrayList<Denom>();
        if(denomIds.size()>0){
        	denomList = denomService.listByIds(denomIds);
        }
        
        Map<Long,String> bankNameMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName,(key1,key2)->key2));
//        Map<Long,Denom> deomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Function.identity(),(key1,key2)->key2));
        Map<Long,Denom> deomMap = getDenomIdMap();
        //计算还需要配钞金额
        Map<String,SubRouteBankDenomDTO> subRouteBankDenomDTOMap = new HashMap<>();
        for (String key : taskBankDenomSumDTOMap.keySet()) {
            BankDenomSumDTO target = taskBankDenomSumDTOMap.get(key);
//            String bank_id = key.split("_")[0];
            BankDenomSumDTO dispatch = dispBankDenomSumDTOMap.get(key);
            BigDecimal needAmount = null;
            if (null != dispatch) { //已配钞，计算差额
                if (target.getTotalAmount().compareTo(dispatch.getTotalAmount()) > 0) { //需要配钞
                    needAmount = target.getTotalAmount().subtract(dispatch.getTotalAmount());
                }
            } else { //未配钞,需要配钞
                needAmount = target.getTotalAmount();
            }
            if (needAmount != null) {
                SubRouteBankDenomDTO subRouteBankDenomDTO = new SubRouteBankDenomDTO();
                subRouteBankDenomDTO.setTotalAmount(needAmount);
                Denom denom = deomMap.get(target.getDenomId());
                if (null != denom) {
                    subRouteBankDenomDTO.setDenomName(denom.getName());
//                    subRouteBankDenomDTO.setBundleCount(denom.toBundleFloat(needAmount));
                    float dispatchBoxNum = 0;
                    if (dispatch != null) {
                        dispatchBoxNum = dispatch.getBoxCount();
                    }
                    subRouteBankDenomDTO.setBundleCount((float) Math.ceil(needAmount.divide(denom.getValue())
                            .divide(BigDecimal.valueOf(denom.getBundleSize()))
                            .divide(BigDecimal.valueOf(denom.getWadSize())).floatValue()));
                }
                subRouteBankDenomDTO.setBankName(bankNameMap.getOrDefault(target.getBankId(),""));
                subRouteBankDenomDTO.setTotalTask(target.getTaskCount());
                subRouteBankDenomDTO.setDenomId(denom.getId());
                subRouteBankDenomDTOMap.put(key,subRouteBankDenomDTO);
            }
        }
        //map转换有序list输出
        Map<String,SubRouteBankDenomDTO> stu=new TreeMap<>(new MyComparator());
        stu.putAll(subRouteBankDenomDTOMap);
        Set<String> keySet=stu.keySet();
        Iterator it=keySet.iterator();
        List<SubRouteBankDenomDTO> subRouteBankDenomDTOS = new ArrayList<>();
        while (it.hasNext()) {
            String next = (String)it.next();
            subRouteBankDenomDTOS.add(stu.get(next));
        }
        detailDTO.setSubRouteBankDenomDTOS(subRouteBankDenomDTOS);

        //随车人员
        Set<Long> empIds = new HashSet<>();
        if (route.getDriver() != 0L) {
            empIds.add(route.getDriver());
        }
        if (route.getSecurityA() != 0L) {
            empIds.add(route.getSecurityA());
        }
        if (route.getSecurityB() != 0L) {
            empIds.add(route.getSecurityB());
        }
        if (route.getRouteKeyMan() != 0L) {
            empIds.add(route.getRouteKeyMan());
        }
        if (route.getRouteOperMan() != 0L) {
            empIds.add(route.getRouteOperMan());
        }
        if (empIds.size() > 0) {
            List<Employee> employeeList = employeeService.listByIds(empIds);
            Map<Long, String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee::getEmpName, (key1, key2) -> key2));
            detailDTO.setDriverName(employeeMap.getOrDefault(route.getDriver(), ""));
            detailDTO.setSecurityAName(employeeMap.getOrDefault(route.getSecurityA(), ""));
            detailDTO.setSecurityBName(employeeMap.getOrDefault(route.getSecurityB(), ""));
            detailDTO.setRouteKeyManName(employeeMap.getOrDefault(route.getRouteKeyMan(), ""));
            detailDTO.setRouteOperManName(employeeMap.getOrDefault(route.getRouteOperMan(), ""));
        }
        return detailDTO;
    }
    /**
     * 查询线路详情
     * @param routeId
     * @return
     */
    public RouteDetailDTO getRouteInfo(Long routeId) throws BusinessException {
        Route route = routeService.getById(routeId);
        if (null == route) {
            log.info("无效线路id：" + routeId);
            throw new BusinessException("无效线路id");
        }
        RouteDetailDTO detailDTO = new RouteDetailDTO();
        BeanUtils.copyProperties(route,detailDTO);
        //车辆信息
        Vehicle vehicle = vehicleService.getById(route.getVehicleId());
        if (null != vehicle) {
            detailDTO.setVehicleLpNo(vehicle.getLpno());
        }

        List<AtmTask> atmTaskList = atmTaskService.listByCarryRouteId(routeId,null);
        if (atmTaskList.size() > 0) {
            detailDTO.setTaskTotal(countValideTask(atmTaskList));
        }

        //配钞统计
        List<CashboxPackRecord> cashboxPackRecords = cashboxPackRecordService.listByRoute(routeId);
        detailDTO.setDispBoxCount(cashboxPackRecords.size());
        
        //未使用钞盒数
        List<String> notUsedBoxNos = new ArrayList<String>();
        for (CashboxPackRecord cashboxPackRecord : cashboxPackRecords) {
        	if(cashboxPackRecord.getStatusT().equals(BoxStatusEnum.STOREOUT.getValue())){
        		notUsedBoxNos.add(cashboxPackRecord.getBoxNo());
        	}
		}
        detailDTO.setNotUsedBoxNos(notUsedBoxNos);
        detailDTO.setNotUsedBoxCount(notUsedBoxNos.size());
        
        
        //随车人员
        Set<Long> empIds = new HashSet<>();
        if (route.getDriver() != 0L) {
            empIds.add(route.getDriver());
        }
        if (route.getSecurityA() != 0L) {
            empIds.add(route.getSecurityA());
        }
        if (route.getSecurityB() != 0L) {
            empIds.add(route.getSecurityB());
        }
        if (route.getRouteKeyMan() != 0L) {
            empIds.add(route.getRouteKeyMan());
        }
        if (route.getRouteOperMan() != 0L) {
            empIds.add(route.getRouteOperMan());
        }
        if (empIds.size() > 0) {
            List<Employee> employeeList = employeeService.listByIds(empIds);
            Map<Long, String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee::getEmpName, (key1, key2) -> key2));
            detailDTO.setDriverName(employeeMap.getOrDefault(route.getDriver(), ""));
            detailDTO.setSecurityAName(employeeMap.getOrDefault(route.getSecurityA(), ""));
            detailDTO.setSecurityBName(employeeMap.getOrDefault(route.getSecurityB(), ""));
            detailDTO.setRouteKeyManName(employeeMap.getOrDefault(route.getRouteKeyMan(), ""));
            detailDTO.setRouteOperManName(employeeMap.getOrDefault(route.getRouteOperMan(), ""));
        }
        return detailDTO;
    }

    /**
     * 线路配钞
     * @param routeDispatchCashVO
     */
    public void dispatchRouteCash(RouteDispCashVO routeDispatchCashVO) throws BusinessException {
        Route route = routeService.getById(routeDispatchCashVO.getRouteId());
        if (null == route) {
            log.warn("无效线路id: " +routeDispatchCashVO.getRouteId());
            throw new BusinessException("无效线路");
        }
        if (RouteStatusEnum.CHECKED.getValue() != route.getStatusT()) {
            log.warn("无效线路id: " +routeDispatchCashVO.getRouteId());
            throw new BusinessException("线路未确认");
        }
        Route routeTmp = new Route();
        routeTmp.setId(route.getId());
        routeTmp.setDispOperMan(routeDispatchCashVO.getDispOperMan());
        routeTmp.setDispCheckMan(routeDispatchCashVO.getDispCheckMan());
        if (routeDispatchCashVO.getDispBagCount() > 0) {
            routeTmp.setDispBagCount(routeDispatchCashVO.getDispBagCount() + route.getDispBagCount());
        }
        routeTmp.setStatusT(RouteStatusEnum.DISPATCH.getValue());
        routeTmp.setDispTime(System.currentTimeMillis());
        routeTmp.setActBeginTime(System.currentTimeMillis());
        routeService.updateById(routeTmp);
    }

    /**
     * 批量保存钞盒扫描记录
     * @param packIds  - 扫描钞盒列表
     * @param boxScanNodeEnum - 扫描节点
     */
    private void saveCashboxScanRecordBatch(List<Long> packIds,BoxScanNodeEnum boxScanNodeEnum,Long scanTime) {
        if (packIds == null || packIds.size() == 0) {
            return;
        }
        List<CashboxScanRecord> scanRecords = packIds.stream().map(id -> {
            CashboxScanRecord scanRecord = new CashboxScanRecord();
            scanRecord.setPackId(id);
            scanRecord.setScanNode(boxScanNodeEnum.getValue());
            scanRecord.setScanTime(scanTime);
            scanRecord.setScanUser(UserContextHolder.getUserId());
            return scanRecord;
        }).collect(Collectors.toList());
        cashboxScanRecordService.saveBatch(scanRecords);
    }

    /**
     * 批量更新钞盒封装记录数据
     * @param packIds  - 钞盒封装id列表
     * @param routeId  - 分配线路id
     * @param atmId    - 加钞atmid
     * @param boxStatusEnum - 状态
     */
    private void updateCashboxPackRecordBatch(List<Long> packIds,Long routeId,Long atmId,BoxStatusEnum boxStatusEnum) {
        if (packIds == null || packIds.size() == 0) {
            return;
        }
        List<CashboxPackRecord> packRecords = packIds.stream().map(id -> {
            CashboxPackRecord packRecord = new CashboxPackRecord();
            packRecord.setId(id);
            packRecord.setStatusT(boxStatusEnum.getValue());
            if (routeId != null) {
                packRecord.setRouteId(routeId);
            }
            if (atmId != null) {
                packRecord.setAtmId(atmId);
            }
            return packRecord;
        }).collect(Collectors.toList());
        cashboxPackRecordService.updateBatchById(packRecords,packRecords.size());
    }

    /**
     * 加钞更新钞盒状态
     * @param oldPackRecords
     * @param atmId
     * @param boxStatusEnum
     * @param usedBoxVOList
     */
    private void updateCashboxPackRecordAtmIdBatch(List<CashboxPackRecord> oldPackRecords,Long routeId,Long atmId,BoxStatusEnum boxStatusEnum,List<UsedBoxVO> usedBoxVOList) {
        if (oldPackRecords == null || oldPackRecords.size() == 0) {
            return;
        }
        Map<String,UsedBoxVO> usedBoxVOMap = new HashMap<>();
        if (usedBoxVOList != null) {
            usedBoxVOMap = usedBoxVOList.stream().collect(Collectors.toMap(UsedBoxVO::getBoxNo,Function.identity()));
        }
        List<CashboxPackRecord> packRecords = new ArrayList<>();
        for (CashboxPackRecord oldPackRecord : oldPackRecords) {
            CashboxPackRecord packRecord = new CashboxPackRecord();
            packRecord.setId(oldPackRecord.getId());
            packRecord.setStatusT(boxStatusEnum.getValue());
            UsedBoxVO usedBoxVO = usedBoxVOMap.get(oldPackRecord.getBoxNo());
            if (null != usedBoxVO) {
                packRecord.setUseCount(oldPackRecord.getUseCount() + usedBoxVO.getUseCount());
            }
            if (null != routeId) { //如果配钞不扫描，则不知道是哪条线路
                packRecord.setRouteId(routeId);
            }
            if (oldPackRecord.getAtmId() == 0L) {
                packRecord.setAtmId(atmId);
            } else if (oldPackRecord.getSecondAtmId() == 0L) {
                packRecord.setSecondAtmId(atmId);
            } else {
                packRecord.setAtmId(atmId);
            }
            packRecords.add(packRecord);
        };
        cashboxPackRecordService.updateBatchById(packRecords,packRecords.size());
    }

    /**
     * 线路配钞结果复核
     * @param routeDispCashConfirmVO
     */
    @Transactional
    public void dispatchRouteCashConfirm(RouteDispCashConfirmVO routeDispCashConfirmVO) throws BusinessException {
        Route route = routeService.getById(routeDispCashConfirmVO.getRouteId());
        if (null == route) {
            log.error("无效线路: "+ routeDispCashConfirmVO.getRouteId());
            throw new BusinessException("无效线路");
        }
        //线路未确认或已完成，无法操作
        if (route.getStatusT() < RouteStatusEnum.DISPATCH.getValue() || route.getStatusT() > RouteStatusEnum.DISPATCH_CHECK.getValue()) {
            BusinessException exception = new BusinessException("线路未配钞，无法复核");
            log.warn(exception.getMessage());
            throw exception;
        }
        List<String> boxNoList = Arrays.asList(routeDispCashConfirmVO.getBoxRfids());
        if (boxNoList.size() == 0) {
            log.warn("缺少钞盒参数");
            throw new BusinessException("缺少钞盒");
        }
        List<Cashbox> cashboxList = cashboxService.listByBatchBoxNo(boxNoList);
        Map<String,Cashbox> cashboxMap = cashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
        for (String boxNo : boxNoList) {
            if (!cashboxMap.containsKey(boxNo)) {
                String errMsg = "无效钞盒: " + boxNo;
                log.info(errMsg);
                throw new BusinessException(errMsg);
            }
        }
        List<String> unPackList = new ArrayList<>(); //未绑定列表
        List<Long> packIds = new ArrayList<>(); //已绑定列表
        for (Cashbox cashbox : cashboxList) {
            if (cashbox.getPackId() == 0L) { //钞盒未入库
                unPackList.add(cashbox.getBoxNo());
            } else {
                packIds.add(cashbox.getPackId());
            }
        }
        if (unPackList.size() > 0) {
            String errMsg = "钞盒未绑定:"+unPackList.toString();
            log.warn(errMsg);
            throw new BusinessException(errMsg);
        }

        List<CashboxPackRecord> cashboxPackRecords = cashboxPackRecordService.listByIds(packIds);
        //处理配钞
        for (CashboxPackRecord cashboxPackRecord : cashboxPackRecords) {
            if (cashboxPackRecord.getStatusT() == BoxStatusEnum.OPEN.getValue()) {
                String errMsg = "钞盒已使用: "+cashboxPackRecord.getBoxNo();
                log.warn(errMsg);
                throw new BusinessException(errMsg);
            }
        }

        // 所需的各denom数量
        Map<Long, Float> denomNumMap = new HashMap<>();
        RouteDetailDTO routeDiapatchDetail = getRouteDiapatchDetail(route.getId());
        List<SubRouteBankDenomDTO> subRouteBankDenomDTOS = routeDiapatchDetail.getSubRouteBankDenomDTOS();
        for (SubRouteBankDenomDTO dto: subRouteBankDenomDTOS) {
            Long denomId = dto.getDenomId();
            if (denomNumMap.containsKey(denomId)) {
                Float onum = denomNumMap.get(denomId);
                denomNumMap.put(denomId, onum + dto.getBundleCount());
            } else {
                denomNumMap.put(denomId, dto.getBundleCount());
            }
        }

        for (CashboxPackRecord cashboxPackRecord: cashboxPackRecords) {
            Long denomId = cashboxPackRecord.getDenomId();
            Float num = denomNumMap.get(denomId);
            if (num <= 0) {
                BigDecimal value = getDenomIdMap().get(denomId).getValue();
                String errMsg = value + "元钞盒配钞数量超过阈值！";
                log.warn(errMsg);
                throw new BusinessException(errMsg);
            }
            denomNumMap.put(denomId, num - 1);
        }

        //修改钞盒封装记录状态
        this.updateCashboxPackRecordBatch(packIds,route.getId(),null,BoxStatusEnum.STOREOUT);
        //保存钞盒扫描记录
        //TODO 考虑异步处理
        this.saveCashboxScanRecordBatch(packIds,BoxScanNodeEnum.DISPATCH_CONFIRM,System.currentTimeMillis());

        if (route.getStatusT() < RouteStatusEnum.DISPATCH_CHECK.getValue()) {
	        /**
	         * 更新线路配钞状态
	         */
	        Route newRoute = new Route();
	        newRoute.setId(route.getId());
	        newRoute.setStatusT(RouteStatusEnum.DISPATCH_CHECK.getValue());
	        newRoute.setDispCfmTime(System.currentTimeMillis());
	        newRoute.setActBeginTime(System.currentTimeMillis());
	        routeService.updateById(newRoute);
        }
    }

    /**
     * 校验用户密码
     * @param userName
     * @param password
     * @return
     */
    public boolean validateEmployeePassword(String userName,String password) {

        Employee employee = employeeService.getByEmployeeNo(userName);
        if (employee == null) {
            return false;
        }
        return 0 == employee.getPassword().compareToIgnoreCase(password);
    }

    /**
     * 查询线路对应ATM未完成任务列表
     * @param routeId
     * @return
     */
    public List<AtmTaskGroupDTO> listAtmTaskByRoute(Long routeId){
        List<AtmTask> atmTaskList = atmTaskService.listByCarryRouteId(routeId,AtmTaskStatusEnum.CONFRIM);
        if (atmTaskList.size() == 0) {
            return new ArrayList<>();
        }
        /**
         * 将atm任务按银行网点分组
         */
        Map<Long,List<AtmTask>> subBankTaskMap = new HashMap<>();
        atmTaskList.forEach(atmTask -> {
            boolean bBankListExist = subBankTaskMap.get(atmTask.getSubBankId()) != null;
            if (!bBankListExist) {
                List<AtmTask> atmTasks = new ArrayList<>();
                subBankTaskMap.put(atmTask.getSubBankId(),atmTasks);
            }
            subBankTaskMap.get(atmTask.getSubBankId()).add(atmTask);
        });
        /**
         * 查询ATM设备信息
         */
        List<Long> atmIdList = atmTaskList.stream().map(AtmTask::getAtmId).collect(Collectors.toList());
        List<AtmDevice> atmDeviceList = atmDeviceService.listByIds(atmIdList);
        Map<Long,AtmDevice> atmDeviceMap = atmDeviceList.stream().collect(Collectors.toMap(AtmDevice::getId, Function.identity(),(key1,key2) -> key2));
        /**
         * 查询银行信息
         */
        Set<Long> bankIdSet = atmTaskList.stream().map(AtmTask::getBankId).collect(Collectors.toSet());
        Set<Long> suBankIdSet = atmTaskList.stream().map(AtmTask::getSubBankId).collect(Collectors.toSet());
        bankIdSet.addAll(suBankIdSet);
        List<Bank> bankList = bankService.listByIds(bankIdSet);
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));

        //TODO 排序 返回
        List<AtmTaskGroupDTO> atmTaskGroupDTOS = new ArrayList<>();
        suBankIdSet.forEach(subBankId -> {
            List<AtmTask> subBankAtmTaskList = subBankTaskMap.get(subBankId);
            if (subBankAtmTaskList != null) {
                AtmTaskGroupDTO atmTaskGroupDTO = new AtmTaskGroupDTO();
                Bank bank = bankMap.get(subBankId); //网点
                if (bank != null) {
                    atmTaskGroupDTO.setSubBankName(bank.getFullName());
                    atmTaskGroupDTO.setSubBankNo(bank.getBankNo());
                    atmTaskGroupDTO.setSubBankId(bank.getId());
                }
                AtmTask tmpTask = subBankAtmTaskList.get(0);
                bank = bankMap.get(tmpTask.getBankId()); //银行机构
                if (bank != null) {
                    atmTaskGroupDTO.setBankName(bank.getFullName());
                }
                List<AtmTaskGroupItem> atmTaskGroupItems = new ArrayList<>();
                for (AtmTask atmTask : subBankAtmTaskList) {
                    AtmTaskGroupItem atmTaskGroupItem = new AtmTaskGroupItem();
                    atmTaskGroupItem.setTaskId(atmTask.getId());
                    atmTaskGroupItem.setStatusT(atmTask.getStatusT());
                    atmTaskGroupItem.setTaskName(AtmTaskTypeEnum.getText(atmTask.getTaskType()));
                    AtmDevice atmDevice = atmDeviceMap.get(atmTask.getAtmId());
                    if (atmDevice != null) {
                        atmTaskGroupItem.setAtmNo(atmDevice.getTerNo());
                        atmTaskGroupItem.setAtmLocateType(AtmLocateTypeEnum.INDEPENT.getValue());
                    }
                    if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
                        atmTaskGroupItem.setTaskContent(atmTask.getRepairContent());
                    } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                    || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
                        float tenThounsand = atmTask.getAmount().setScale(0,RoundingMode.HALF_DOWN).divide(BigDecimal.valueOf(10000),BigDecimal.ROUND_DOWN).floatValue();
                        atmTaskGroupItem.setTaskContent(tenThounsand+"万元");
                    }
                    atmTaskGroupItem.setComments(atmTask.getComments());
                    atmTaskGroupItem.setBackupFlagText(AtmBackupFlagEnum.getText(atmTask.getBackupFlag()));
                    atmTaskGroupItems.add(atmTaskGroupItem);
                }
//                atmTaskGroupDTO.setAtmTaskGroupItems(atmTaskGroupItems);
                atmTaskGroupDTOS.add(atmTaskGroupDTO);
            }
        });
        return atmTaskGroupDTOS;
    }

    private Map<Integer, Denom> getDenomMap() {
        List<Denom> denoms = denomService.listAtmDenom();
        return denoms.stream().collect(Collectors.toMap(denom -> denom.getValue().intValue(), denom -> denom));
    }

    private Map<Long, Denom> getDenomIdMap() {
        List<Denom> denoms = denomService.listAtmDenom();
        return denoms.stream().collect(Collectors.toMap(denom -> denom.getId(), denom -> denom));
    }
    
    /**
     * 查询线路对应ATM未完成任务列表
     * @param routeId
     * @return
     */
    public List<AtmTaskGroupDTO> listAtmTaskByRouteNew(Long routeId){
        List<AtmTask> atmTaskList = atmTaskService.listByCarryRouteId(routeId,AtmTaskStatusEnum.CONFRIM);
        if (atmTaskList.size() == 0) {
            return new ArrayList<>();
        }
        /**
         * 将atm任务按银行网点分组
         */
        Map<Long,List<AtmTask>> subBankTaskMap = new HashMap<>();
        atmTaskList.forEach(atmTask -> {
            boolean bBankListExist = subBankTaskMap.get(atmTask.getSubBankId()) != null;
            if (!bBankListExist) {
                List<AtmTask> atmTasks = new ArrayList<>();
                subBankTaskMap.put(atmTask.getSubBankId(),atmTasks);
            }
            subBankTaskMap.get(atmTask.getSubBankId()).add(atmTask);
        });
        /**
         * 查询ATM设备信息
         */
        List<Long> atmIdList = atmTaskList.stream().map(AtmTask::getAtmId).collect(Collectors.toList());
        List<AtmDevice> atmDeviceList = atmDeviceService.listByIds(atmIdList);
        Map<Long,AtmDevice> atmDeviceMap = atmDeviceList.stream().collect(Collectors.toMap(AtmDevice::getId, Function.identity(),(key1,key2) -> key2));
        /**
         * 查询银行信息
         */
        Set<Long> bankIdSet = atmTaskList.stream().map(AtmTask::getBankId).collect(Collectors.toSet());
        Set<Long> suBankIdSet = atmTaskList.stream().map(AtmTask::getSubBankId).collect(Collectors.toSet());
        bankIdSet.addAll(suBankIdSet);
        List<Bank> bankList = bankService.listByIds(bankIdSet);
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));

        /**
         * 券别信息
         */
        Map<Integer, Denom> denomMap = getDenomMap();

        //TODO 排序 返回
        List<AtmTaskGroupDTO> atmTaskGroupDTOS = new ArrayList<>();
        suBankIdSet.forEach(subBankId -> {
            List<AtmTask> subBankAtmTaskList = subBankTaskMap.get(subBankId);
            if (subBankAtmTaskList != null) {
                AtmTaskGroupDTO atmTaskGroupDTO = new AtmTaskGroupDTO();
                Bank subBank = bankMap.get(subBankId); //网点
                if (subBank != null) {
                    atmTaskGroupDTO.setSubBankName(subBank.getFullName());
                    atmTaskGroupDTO.setSubBankNo(subBank.getBankNo());
                    atmTaskGroupDTO.setSubBankId(subBank.getId());
                }
                //顶级机构名称
                AtmTask tmpTask = subBankAtmTaskList.get(0);
                Bank bank = bankMap.get(tmpTask.getBankId()); //银行机构
                if (bank != null) {
                    atmTaskGroupDTO.setBankName(bank.getFullName());
                }
                //返回任务信息
                List<AtmTaskGroupItemNew> atmTaskGroupItems = new ArrayList<>();
                for (AtmTask atmTask : subBankAtmTaskList) {
                    AtmTaskGroupItemNew atmTaskGroupItem = new AtmTaskGroupItemNew();
                    atmTaskGroupItem.setTaskId(atmTask.getId());
                    atmTaskGroupItem.setStatusT(atmTask.getStatusT());
                    atmTaskGroupItem.setTaskType(atmTask.getTaskType());
                    atmTaskGroupItem.setTaskName(AtmTaskTypeEnum.getText(atmTask.getTaskType()));
                    atmTaskGroupItem.setAmount(atmTask.getAmount());
                    //维修字段
                    atmTaskGroupItem.setRepairCompany(atmTask.getRepairCompany());
                    atmTaskGroupItem.setRepairContent(atmTask.getRepairContent());
                    atmTaskGroupItem.setRepairPlanTime(atmTask.getRepairPlanTime());

                    AtmDevice atmDevice = atmDeviceMap.get(atmTask.getAtmId());
                    if (atmDevice != null) {
                        atmTaskGroupItem.setAtmNo(atmDevice.getTerNo());
                        atmTaskGroupItem.setDenomValue(atmDevice.getDenom());
                        //计算盒数
                        int boxCount = countBoxNum(atmTask.getAmount(),
                                denomMap.get(atmDevice.getDenom()), denomMap.get(extraDenomValue));
//                        int boxCount = atmTask.getAmount().setScale(0, RoundingMode.HALF_DOWN).
//                                divide(BigDecimal.valueOf(Constant.ATM_DEF_WAD_SIZE*Constant.ATM_DEF_BUNDLE_SIZE*atmDevice.getDenom()),BigDecimal.ROUND_HALF_UP).intValue();
                        atmTaskGroupItem.setCashBoxCnt(boxCount);
                    }
                    atmTaskGroupItem.setComments(atmTask.getComments());
                    atmTaskGroupItem.setBackupFlagText(AtmBackupFlagEnum.getText(atmTask.getBackupFlag()));
                    atmTaskGroupItems.add(atmTaskGroupItem);
                }
                atmTaskGroupDTO.setAtmTaskGroupItems(atmTaskGroupItems);
                atmTaskGroupDTOS.add(atmTaskGroupDTO);
            }
        });
        return atmTaskGroupDTOS;
    }

    /**
     * 计算所需钞盒的数量
     * @param amount 总金额
     * @param denom atm指定券别
     * @param extraDenom 额外券别
     * @return 钞盒数量
     */
    private Integer countBoxNum(BigDecimal amount, Denom denom, Denom extraDenom) {
        // 单盒数量
        long oneBoxAmount = (long) denom.getValue().intValue() * denom.getBundleSize() * denom.getWadSize();
        int boxCount = amount.divide(BigDecimal.valueOf(oneBoxAmount), BigDecimal.ROUND_DOWN).intValue();
        float decimalBoxCount = amount.divide(BigDecimal.valueOf(oneBoxAmount)).floatValue();
        // ATM指定券额的加钞额
        BigDecimal originalDenomAmount = BigDecimal.valueOf(oneBoxAmount).multiply(BigDecimal.valueOf(boxCount));
        // 需要加10元
        if (originalDenomAmount.compareTo(amount) != 0) {
            // 有半盒
            if (decimalBoxCount - boxCount >= 0.5) {
                originalDenomAmount = originalDenomAmount.add(BigDecimal.valueOf(oneBoxAmount / 2));
                boxCount += 1;
            }
            BigDecimal extraAmount = amount.subtract(originalDenomAmount);
            int extraBoxCount = extraAmount.divide(BigDecimal.valueOf((long) extraDenom.getValue().intValue() * extraDenom.getBundleSize() * extraDenom.getWadSize()), BigDecimal.ROUND_DOWN).intValue();
            boxCount += extraBoxCount;
        }
        return boxCount;
    }

    private float countHundredBoxNum(BigDecimal amount, Denom denom) {
        // 单盒数量
        long oneBoxAmount = (long) denom.getValue().intValue() * denom.getBundleSize() * denom.getWadSize();
        float boxCount = amount.divide(BigDecimal.valueOf(oneBoxAmount), BigDecimal.ROUND_DOWN).intValue();
        float decimalBoxCount = amount.divide(BigDecimal.valueOf(oneBoxAmount)).floatValue();
        // ATM指定券额的加钞额
        BigDecimal originalDenomAmount = BigDecimal.valueOf(oneBoxAmount).multiply(BigDecimal.valueOf(boxCount));
        // 需要加10元
        if (originalDenomAmount.compareTo(amount) != 0) {
            // 有半盒
            if (decimalBoxCount - boxCount >= 0.5) {
                originalDenomAmount = originalDenomAmount.add(BigDecimal.valueOf(oneBoxAmount / 2));
                boxCount += 0.5;
            }
        }
        return boxCount;
    }


    /**
     * 查询一台设备的所有任务
     * @param routeId
     * @param atmNo
     * @return
     */
    public List<AtmTaskGroupItem> listByAtmId(Long routeId, String atmNo) throws BusinessException{

        AtmDevice atmDevice = atmDeviceService.getByAtmNo(atmNo);
        if (null == atmDevice) {
            String errMsg = "无效终端";
            log.warn(errMsg);
            throw new BusinessException(errMsg);
        }
        List<AtmTask> atmTaskList = atmTaskService.listByAtmId(routeId,atmDevice.getId(),AtmTaskStatusEnum.CONFRIM);
        if (atmTaskList.size() == 0) {
            return new ArrayList<>();
        }

        List<AtmTaskGroupItem> atmTaskGroupItems = new ArrayList<>();
        for (AtmTask atmTask : atmTaskList) {
            AtmTaskGroupItem atmTaskGroupItem = new AtmTaskGroupItem();
            atmTaskGroupItem.setTaskId(atmTask.getId());
            atmTaskGroupItem.setStatusT(atmTask.getStatusT());
            atmTaskGroupItem.setTaskName(AtmTaskTypeEnum.getText(atmTask.getTaskType()));
            atmTaskGroupItem.setAtmNo(atmDevice.getTerNo());
            atmTaskGroupItem.setAtmLocateType(AtmLocateTypeEnum.INDEPENT.getValue());
            if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
                atmTaskGroupItem.setTaskContent(atmTask.getRepairContent());
            } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                    || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
                float tenThounsand = atmTask.getAmount().setScale(0,RoundingMode.HALF_DOWN).divide(BigDecimal.valueOf(10000),BigDecimal.ROUND_DOWN).floatValue();
                atmTaskGroupItem.setTaskContent(tenThounsand+"万元");
            }
            atmTaskGroupItem.setComments(atmTask.getComments());
            atmTaskGroupItem.setBackupFlagText(AtmBackupFlagEnum.getText(atmTask.getBackupFlag()));
            atmTaskGroupItems.add(atmTaskGroupItem);
        }

        return atmTaskGroupItems;
    }

    /**
     * 查询任务详情
     * @param taskId
     */
    public AtmTaskDetailDTO getAtmTaskDetail(Long taskId) throws BusinessException {
        AtmTask atmTask = atmTaskService.getById(taskId);
        if (atmTask == null) {
            throw new BusinessException("无效任务id");
        }
        /**
         * 券别信息
         */
        Map<Integer, Denom> denomMap = getDenomMap();

        AtmTaskDetailDTO atmTaskDetailDTO = new AtmTaskDetailDTO();
        atmTaskDetailDTO.setTaskId(taskId);
        atmTaskDetailDTO.setStatusT(atmTask.getStatusT());
        atmTaskDetailDTO.setComments(atmTask.getComments());
        Bank bank = bankService.getById(atmTask.getBankId());
        if (null != bank) {
            atmTaskDetailDTO.setBankName(bank.getFullName());
            atmTaskDetailDTO.setCarryType(bank.getCarryType());
        }
        bank = bankService.getById(atmTask.getSubBankId());
        if (null != bank) {
            atmTaskDetailDTO.setSubBankName(bank.getFullName());
        }
        AtmDevice atmDevice = atmDeviceService.getById(atmTask.getAtmId());
        if (null != atmDevice) {
            atmTaskDetailDTO.setAtmNo(atmDevice.getTerNo());
            atmTaskDetailDTO.setAtmLocateType(atmDevice.getLocationType());
        }
        atmTaskDetailDTO.setTaskType(atmTask.getTaskType());
        atmTaskDetailDTO.setTaskTypeText(AtmTaskTypeEnum.getText(atmTask.getTaskType()));
        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
            atmTaskDetailDTO.setRepairCompany(atmTask.getRepairCompany());
            atmTaskDetailDTO.setRepairContent(atmTask.getRepairContent());
            atmTaskDetailDTO.setRepairPlanTime(atmTask.getRepairPlanTime());

        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()
        || atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()) {
            atmTaskDetailDTO.setAmount(atmTask.getAmount());
            atmTaskDetailDTO.setDenomValue(atmDevice.getDenom());
            if (atmTask.getBackupFlag() == 0) {
                atmTaskDetailDTO.setBackupFlagText("预排");
            } else {
                atmTaskDetailDTO.setBackupFlagText("备用金");
            }
            //计算盒数
            int boxCount = countBoxNum(atmTask.getAmount(),
                    denomMap.get(atmDevice.getDenom()), denomMap.get(extraDenomValue));
//            int boxCount = atmTask.getAmount().setScale(0, RoundingMode.HALF_DOWN)
//                    .divide(BigDecimal.valueOf(Constant.ATM_DEF_WAD_SIZE*Constant.ATM_DEF_BUNDLE_SIZE*atmDevice.getDenom()),BigDecimal.ROUND_HALF_UP).intValue();
            atmTaskDetailDTO.setCashBoxCnt(boxCount);
        }

        return atmTaskDetailDTO;
    }

    /**
     * 钞袋领取
     * @param routeDispBagVO
     */
    public void saveDispatchBagCount(RouteDispBagVO routeDispBagVO) {
        Route route = routeService.getById(routeDispBagVO.getRouteId());
        if (null == route) {
            log.warn("无效线路id = " + routeDispBagVO.toString());
            throw new BusinessException("无效线路id");
        }

        Route routeTmp = new Route();
        routeTmp.setId(route.getId());
        routeTmp.setDispBagCount(routeDispBagVO.getDispBagCount()+route.getDispBagCount());
        routeService.updateById(routeTmp);
    }

    /**
     * 返回线路交接处理
     * @param routeReturnBackVO
     */
    public void handover(RouteReturnBackVO routeReturnBackVO) throws BusinessException{
        Route route = routeService.getById(routeReturnBackVO.getRouteId());
        if (null == route) {
            log.warn("无效线路id = " + routeReturnBackVO.toString());
            throw new BusinessException("无效线路id");
        }
        if (RouteStatusEnum.DISPATCH.getValue() > route.getStatusT()) {
            String errMsg = "线路未开始，不能交接";
            log.warn(errMsg);
            throw new BusinessException(errMsg);
        }
        List<AtmTask> taskList = atmTaskService.listByCarryRouteId(route.getId(),AtmTaskStatusEnum.CONFRIM);
        if (taskList.size() > 0) {
            log.warn("还有任务未完成: "+ taskList.size());
            throw new BusinessException("还有任务未完成");
        }
        Route routeTmp = new Route();
        routeTmp.setId(route.getId());
        BeanUtils.copyProperties(routeReturnBackVO,routeTmp);
        routeTmp.setHdoverOperMan(routeReturnBackVO.getHoverOperMan());
        routeTmp.setHdoverCheckMan(routeReturnBackVO.getHoverCheckMan());
        routeTmp.setStatusT(RouteStatusEnum.FINISH.getValue());
        routeTmp.setHdoverTime(System.currentTimeMillis());
        routeTmp.setActFinishTime(System.currentTimeMillis());
        routeService.updateById(routeTmp);
    }

    private void saveBankCard(Route route,AtmTask atmTask,List<BankCardInfo> bankCardInfos) {
        //吞卡保存
        if (bankCardInfos != null) {
            List<AtmTaskCard> atmTaskCards = bankCardInfos.stream().map(bankCardInfo -> {
                AtmTaskCard atmTaskCard = new AtmTaskCard();
                atmTaskCard.setCardBank(bankCardInfo.getCardBank());
                atmTaskCard.setCardNo(bankCardInfo.getCardNo());
                atmTaskCard.setTaskId(atmTask.getId());
                atmTaskCard.setRouteId(atmTask.getCarryRouteId());
                atmTaskCard.setAtmId(atmTask.getAtmId());
                atmTaskCard.setBankId(atmTask.getBankId());
                atmTaskCard.setDepartmentId(atmTask.getDepartmentId());
                atmTaskCard.setStatusT(BankCardStatusEnum.RETRIVE.getValue());
                atmTaskCard.setRouteNo((null != route)? route.getRouteNo():"");
                AtmDevice atmDevice = atmDeviceService.getById(atmTask.getAtmId());
                if (null != atmDevice) {
                    atmTaskCard.setDeliverBankId(atmDevice.getGulpBankId());
                    Bank bank  = bankService.getById(atmDevice.getGulpBankId());
                    if (null != bank) {
                        if (!StringUtils.isEmpty(bank.getRouteNo())) {
                            atmTaskCard.setDeliverRouteNo(bank.getRouteNo());
                        }
                    }
                }

                atmTaskCard.setDeliverDay(DateTimeUtil.getNextdayString());
                return atmTaskCard;
            }).collect(Collectors.toList());
            atmTaskCardService.saveBatch(atmTaskCards);
        }
    }

    /**
     * 保存维修记录
     * @param atmTask
     * @param repairResultVO
     */
    private void saveRepairRecord(AtmTask atmTask,AtmSubTaskRepairResultVO repairResultVO) {

        AtmTaskRepairRecord newAtmTaskRepairRecord = new AtmTaskRepairRecord();

        BeanUtils.copyProperties(repairResultVO,newAtmTaskRepairRecord);
        newAtmTaskRepairRecord.setAtmTaskId(atmTask.getId());
        newAtmTaskRepairRecord.setAtmId(atmTask.getAtmId());
        newAtmTaskRepairRecord.setRouteId(atmTask.getCarryRouteId());
        newAtmTaskRepairRecord.setDealResult(repairResultVO.getDealResult());
        AtmTaskRepairRecord atmTaskRepairRecord = atmTaskRepairRecordService.getByAtmTaskId(atmTask.getId());
        if (null != atmTaskRepairRecord) {
            newAtmTaskRepairRecord.setId(atmTaskRepairRecord.getId());
            atmTaskRepairRecordService.updateById(newAtmTaskRepairRecord);
        } else {
            atmTaskRepairRecordService.save(newAtmTaskRepairRecord);
        }
    }

    /**
     * 保存巡检记录
     * @param atmTask
     * @param checkResultVO
     */
    private void saveAtmCheckRecord(AtmTask atmTask, AtmSubTaskCheckResultVO checkResultVO) {
        if (null != checkResultVO && checkResultVO.getCheckResult() != null) {
            atmTask.setCheckItemResult(JSONObject.toJSONString(checkResultVO.getCheckResult()));
            int checkResult = 1;
            for (String key : checkResultVO.getCheckResult().keySet()) {
                int value = checkResultVO.getCheckResult().getOrDefault(key,0);
                if ("thingInstall".compareToIgnoreCase(key) == 0
                        || "thingStick".compareToIgnoreCase(key) == 0) {   //两项特殊处理
                    if (value == 1) {
                        checkResult = 2;
                    }
                } else {
                    if (value == 0) {
                        checkResult = 2;
                    }
                }
            }
            atmTask.setCheckResult(checkResult);
        }
    }
//    private void saveAtmCheckRecord(AtmTask atmTask, AtmSubTaskCheckResultVO checkResultVO) {
//        if (null != checkResultVO && checkResultVO.getCheckResult() != null) {
//            AtmTaskCheckRecord newCheckRecord = new AtmTaskCheckRecord();
//            newCheckRecord.setAtmTaskId(atmTask.getId());
//            newCheckRecord.setAtmId(atmTask.getAtmId());
//            newCheckRecord.setRouteId(atmTask.getCarryRouteId());
//            newCheckRecord.setComments(checkResultVO.getComments());
//            newCheckRecord.setCheckItemResult(JSONObject.toJSONString(checkResultVO.getCheckResult()));
//
//            AtmTaskCheckRecord checkRecord = atmTaskCheckRecordService.getByAtmTaskId(atmTask.getId());
//            if (null != checkRecord) {
//                newCheckRecord.setId(checkRecord.getId());
//                atmTaskCheckRecordService.updateById(newCheckRecord);
//            } else {
//                atmTaskCheckRecordService.save(newCheckRecord);
//            }
//        }
//    }

    /**
     * 保存回笼钞盒/钞袋
     * @param atmTask
     * @param returnCashboxs
     */
    private void saveReturnCashbox(AtmTask atmTask, List<String> returnCashboxs) {
        if (returnCashboxs != null && returnCashboxs.size() > 0) {
            List<AtmTaskReturn> atmTaskReturnList =  returnCashboxs.stream().map(s -> {
                AtmTaskReturn atmTaskReturn = new AtmTaskReturn();
                atmTaskReturn.setTaskId(atmTask.getId());
                atmTaskReturn.setRouteId(atmTask.getCarryRouteId());
                atmTaskReturn.setBankId(atmTask.getBankId());
                atmTaskReturn.setAtmId(atmTask.getAtmId());
                String taskDay = DateTimeUtil.timeStampMs2Date(atmTask.getTaskDate(),"yyyy-MM-dd");
                atmTaskReturn.setTaskDate(taskDay);
                atmTaskReturn.setBoxBarCode(s);
                atmTaskReturn.setCarryType(atmTask.getCarryType());
                return atmTaskReturn;
            }).collect(Collectors.toList());
            atmTaskReturnService.saveBatch(atmTaskReturnList);
        }
    }



    /**
     * 2秒 任务重复请求，使用ATMNO作为key
     * @param atmNo
     * @return
     */
    private synchronized boolean checkFinishTaskRepeat(String atmNo, Long uniqId) {

        if (!StringUtils.isEmpty(atmNo)) {
            String key = atmNo+"_"+uniqId;
            boolean bExist = redisUtil.hasKey(key);
            if (bExist) {
                return true;
            } else {
                redisUtil.setEx(key,"0",2, TimeUnit.SECONDS);
            }
        }
        return false;
    }

    /**
     * 离线上送缓存，防止多次上送
     * @param atmNo
     * @param uniqId
     */
    private void addNewTaskToUploadMapCache(String atmNo,Long uniqId) {
        String key = CacheKeysDef.AtmTaskUploadMap;
        String feild = atmNo+"_"+uniqId;
        boolean bExist = redisUtil.hasKey(key);
        if (!bExist) {
            redisUtil.hPut(key,feild,"0");
            redisUtil.setEx(key,null,12,TimeUnit.HOURS);
        } else {
            redisUtil.hPutIfAbsent(key,feild,"0");
        }
    }

    private boolean checkAtmTaskUploadExist(String atmNo,Long uniqId) {
        String key = CacheKeysDef.AtmTaskUploadMap;
        String feild = atmNo+"_"+uniqId;
//        System.out.println();
        return redisUtil.hExists(key,feild);
    }

    /**
     * 检查任务状态
     * @param atmTask
     * @return
     */
    private String checkAtmTaskStatus(AtmTask atmTask) {
        String errorMsg = "";
        if (atmTask.getStatusT() == AtmTaskStatusEnum.CONFRIM.getValue()) {
            return null;
        } else if (atmTask.getStatusT() == AtmTaskStatusEnum.CANCEL.getValue()) {
            errorMsg = "任务已撤销";
        } else if (atmTask.getStatusT() == AtmTaskStatusEnum.FINISH.getValue()) {
            errorMsg = "任务已完成";
        } else if (atmTask.getStatusT() == AtmTaskStatusEnum.CREATE.getValue()) {
            errorMsg = "任务无效";
        }
        return errorMsg;
    }



//    private List<CashboxPackRecord> checkAddCashbox(List<String> boxNos, AtmTask atmTask) throws BusinessException{
//        List<CashboxPackRecord> cashboxPackRecords = new ArrayList<>();
//        List<CashboxPackRecord> routeCashboxs = cashboxPackRecordService.listByRoute(atmTask.getCarryRouteId());
//        if (routeCashboxs.size() == 0) {
//            log.warn("无效加钞盒：" + boxNos);
//            throw new BusinessException("无效加钞盒");
//        }
//        for (String boxNo : boxNos) {
//            CashboxPackRecord curPackRecord = null;
//            for (CashboxPackRecord packRecord : routeCashboxs) {
//                if (packRecord.getBoxNo().equals(boxNo)) {
//                    curPackRecord = packRecord;
//                    if (!packRecord.getDenomId().equals(atmTask.getDenomId())) {
//                        String errMsg = "钞盒券别不正确: "+packRecord.getBoxNo();
//                        log.warn(errMsg);
//                        throw new BusinessException(errMsg);
//                    }
//                    //一个钞盒最多扫两次
//                    if (packRecord.getAtmId() != 0L && packRecord.getSecondAtmId() != 0L) {
//                        String errMsg = "加钞钞盒已使用: "+packRecord.getBoxNo();
//                        log.warn(errMsg);
//                        throw new BusinessException(errMsg);
//                    }
//                }
//            }
//            if (null == curPackRecord) {
//                log.warn("无效加钞盒：" + boxNo);
//                throw new BusinessException("无效加钞盒:"+boxNo);
//            } else {
//                cashboxPackRecords.add(curPackRecord);
//            }
//        }
//        return cashboxPackRecords;
//    }

    private List<CashboxPackRecord> checkAddCashbox(List<UsedBoxVO> usedBoxVOList, AtmTask atmTask) throws BusinessException{
        List<CashboxPackRecord> cashboxPackRecords = new ArrayList<>();
        List<CashboxPackRecord> routeCashboxs = cashboxPackRecordService.listByRoute(atmTask.getCarryRouteId());
        if (routeCashboxs.size() == 0) {
            List<String> tmpBoxNos = usedBoxVOList.stream().map(UsedBoxVO::getBoxNo).collect(Collectors.toList());
            log.warn("无效加钞盒：" + tmpBoxNos);
            throw new BusinessException("无效加钞盒");
        }
        for (UsedBoxVO usedBoxVO : usedBoxVOList) {
            CashboxPackRecord curPackRecord = null;
            for (CashboxPackRecord packRecord : routeCashboxs) {
                if (packRecord.getBoxNo().equals(usedBoxVO.getBoxNo())) {
                    curPackRecord = packRecord;
                    //判断钞盒是否已经使用
                    int useCount = packRecord.getUseCount()+usedBoxVO.getUseCount();
                    if (useCount > FULL_BOX) {
                        String errMsg = "";
                        if (packRecord.getUseCount() == FULL_BOX) {
                            errMsg = "加钞钞盒已使用: ";
                        } else if (packRecord.getUseCount() == HALF_BOX){
                            errMsg = "加钞金额剩余半盒：";
                        }
                        errMsg += packRecord.getBoxNo();
                        log.warn(errMsg);
                        throw new BusinessException(errMsg);
                    }
                }
            }
            if (null == curPackRecord) {
                String errMsg = "无效加钞盒：" + usedBoxVO.getBoxNo();
                log.warn(errMsg);
                throw new BusinessException(errMsg);
            } else {
                cashboxPackRecords.add(curPackRecord);
            }
        }
        return cashboxPackRecords;
    }


    @Transactional
    public void saveAtmTaskResult(AtmTaskResultVO atmTaskResultVO) throws BusinessException{
        if (checkFinishTaskRepeat(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId())) {
            log.warn("重复提交：" + atmTaskResultVO.toString());
            throw new BusinessException("重复提交");
        }
        AtmTask atmTask = atmTaskService.getById(atmTaskResultVO.getTaskId());
        if (null == atmTask) {
            log.warn("无效任务:" + atmTaskResultVO.toString());
            throw new BusinessException("无效任务");
        }
        String error = checkAtmTaskStatus(atmTask);
        if (null != error) {
            log.info(error+":" + atmTaskResultVO.toString());
            throw new BusinessException(error);
        }
        Route route = routeService.getById(atmTask.getCarryRouteId());
        if (atmTaskResultVO.getBankCardInfos() != null && atmTaskResultVO.getBankCardInfos().size() > 0) {
            this.saveBankCard(route,atmTask, atmTaskResultVO.getBankCardInfos());
        }

        AtmTask atmTaskNew = new AtmTask();
        atmTaskNew.setId(atmTask.getId());
        atmTaskNew.setBeginTime(atmTaskResultVO.getBeginTime());
        atmTaskNew.setEndTime(atmTaskResultVO.getEndTime());
        atmTaskNew.setOffline(atmTaskResultVO.getOffline());
        atmTaskNew.setChannel(ChannelEnum.FROM_PC.getValue());
        atmTaskNew.setAtmRunStatus(atmTaskResultVO.getAtmRunStatus());
        atmTaskNew.setStuckAmount(new BigDecimal(atmTaskResultVO.getStuckAmount()));
        atmTaskNew.setCleanOpManId(null != route? route.getRouteOperMan() : null);
        atmTaskNew.setCleanKeyManId(null != route? route.getRouteKeyMan() : null);
        atmTaskNew.setStatusT(AtmTaskStatusEnum.FINISH.getValue());
        /**
         * 巡检记录保存
         */
        if (null != atmTaskResultVO.getCheckResultVO()) {
            this.saveAtmCheckRecord(atmTaskNew,atmTaskResultVO.getCheckResultVO());
        }

        //维修记录保存
        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
            if (null == atmTaskResultVO.getRepairResultVO()) {
                log.warn("无效数据:" + atmTaskResultVO.toString());
                throw new BusinessException("无效数据");
            }
            this.saveRepairRecord(atmTask,atmTaskResultVO.getRepairResultVO());
        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
            //加钞记录保存
            AtmSubTaskCleanResultVO cleanResultVO = atmTaskResultVO.getCleanResultVO();
            if (null == cleanResultVO || null == cleanResultVO.getReturnBoxList()) {
                log.warn("无效数据:" + atmTaskResultVO.toString());
                throw new BusinessException("无效数据");
            }
            atmTaskNew.setClearSite(cleanResultVO.getClearSite());

            /**
             * 处理加钞钞盒记录
             */
            List<CashboxPackRecord> cashboxPackRecords = null;

            if(atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
                if (cleanResultVO.getUsedBoxList() == null || cleanResultVO.getUsedBoxList().size() == 0) {
                    log.warn("无效数据:" + atmTaskResultVO.toString());
                    throw new BusinessException("无效数据");
                }
                cashboxPackRecords = checkAddCashbox(cleanResultVO.getUsedBoxList(),atmTask);
            }
            /**
             * 回笼钞盒处理
             */
            List<Cashbox> returnCashboxList = cashboxService.listByBatchBoxNo(cleanResultVO.getReturnBoxList());
            Map<String,Cashbox> cashboxMap = returnCashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
            for (String boxNo : cleanResultVO.getReturnBoxList()) {
                if (!cashboxMap.containsKey(boxNo)) {
                    String errMsg = "无效回笼钞袋: " + boxNo;
                    log.warn(errMsg);
                    throw new BusinessException(errMsg);
                }
            }
            //查询钞袋当前是否已使用
            String taskDate = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(),"yyyy-MM-dd");
            for (String boxNo : cleanResultVO.getReturnBoxList()) {
                List<AtmTaskReturn> atmTaskReturnList = atmTaskReturnService.listByBoxNo(boxNo,taskDate);
                for (AtmTaskReturn atmTaskReturn : atmTaskReturnList) {
                    if (atmTaskReturn.getClearFlag() != 1) {
                        String errMsg = "回笼钞袋不能重复使用: " + atmTaskReturn.getBoxBarCode();
                        log.warn(errMsg);
                        throw new BusinessException(errMsg);
                    }
                }
            }
            //更新加钞钞盒状态
            if (cashboxPackRecords != null) {
                Set<Long> packIds = cashboxPackRecords.stream().map(CashboxPackRecord::getId).collect(Collectors.toSet());
                Long routeId = route != null ? route.getId() : null;
                this.updateCashboxPackRecordAtmIdBatch(cashboxPackRecords, routeId, atmTask.getAtmId(), BoxStatusEnum.OPEN,cleanResultVO.getUsedBoxList());
                atmTaskNew.setCashboxList(StringUtils.join(packIds, ','));
                this.saveCashboxScanRecordBatch(new ArrayList<>(packIds),BoxScanNodeEnum.CASHIN,System.currentTimeMillis());
            }
            //保存回笼钞袋,放在最后统一保存，避免中途异常返回导致无效处理
            this.saveReturnCashbox(atmTask,cleanResultVO.getReturnBoxList());
        }

        atmTaskService.updateById(atmTaskNew);
    }

    /**
     * 离线任务处理
     * @param atmTaskResultVO
     * @throws BusinessException
     */
//    @Transactional
//    public void saveOfflineAtmTaskResult(AtmTaskResultVO atmTaskResultVO) throws BaseException {
//        if (checkFinishTaskRepeat(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId())) {
//            log.warn("重复提交：" + atmTaskResultVO.toString());
//            throw new BusinessException("重复提交");
//        }
//        AtmTask atmTask = atmTaskService.getById(atmTaskResultVO.getTaskId());
//        if (null == atmTask) {
//            log.warn("无效任务:" + atmTaskResultVO.toString());
//            throw new BaseException(SystemErrorType.UPLOAD_TASK_NOTEXSIT,SystemErrorType.UPLOAD_TASK_NOTEXSIT.getMesg());
//        }
//        boolean dataError = false;
//        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
//            if (null == atmTaskResultVO.getRepairResultVO()) {
//                dataError = true;
//            }
//        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
//                || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
//            //加钞记录保存
//            AtmSubTaskCleanResultVO cleanResultVO = atmTaskResultVO.getCleanResultVO();
//            if (null == cleanResultVO || null == cleanResultVO.getReturnBoxList()) {
//                dataError = true;
//            }
//        }
//        if (dataError) {
//            log.info(SystemErrorType.ARGUMENT_NOT_VALID.getMesg() + ":" + atmTaskResultVO.toString());
//            throw new BaseException(SystemErrorType.ARGUMENT_NOT_VALID,SystemErrorType.ARGUMENT_NOT_VALID.getMesg());
//        }
//
//        if (checkAtmTaskUploadExist(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId())) {
//            log.info(SystemErrorType.UPLOAD_TASK_REPEAT.getMesg() + ":" + atmTaskResultVO.toString());
//            throw new BaseException(SystemErrorType.UPLOAD_TASK_REPEAT,SystemErrorType.UPLOAD_TASK_REPEAT.getMesg());
//        }
//        //TODO 保存离线数据，以备异常时查询数据
//        //
//        addNewTaskToUploadMapCache(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId());
//
//        Route route = routeService.getById(atmTask.getCarryRouteId());
//        if (atmTaskResultVO.getBankCardInfos() != null && atmTaskResultVO.getBankCardInfos().size() > 0) {
//            this.saveBankCard(route,atmTask, atmTaskResultVO.getBankCardInfos());
//        }
//
//        AtmTask atmTaskNew = new AtmTask();
//        atmTaskNew.setId(atmTask.getId());
//        atmTaskNew.setBeginTime(atmTaskResultVO.getBeginTime());
//        atmTaskNew.setEndTime(atmTaskResultVO.getEndTime());
//        atmTaskNew.setOffline(1);
//        atmTaskNew.setChannel(ChannelEnum.FROM_PC.getValue());
//        atmTaskNew.setAtmRunStatus(atmTaskResultVO.getAtmRunStatus());
//        atmTaskNew.setStuckAmount(new BigDecimal(atmTaskResultVO.getStuckAmount()));
//        atmTaskNew.setCleanOpManId(null != route? route.getRouteOperMan() : null);
//        atmTaskNew.setCleanKeyManId(null != route? route.getRouteKeyMan() : null);
//        atmTaskNew.setStatusT(AtmTaskStatusEnum.FINISH.getValue());
//
//        //维修记录保存
//        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
//            this.saveRepairRecord(atmTask,atmTaskResultVO.getRepairResultVO());
//        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
//                || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
//            //加钞记录保存
//            AtmSubTaskCleanResultVO cleanResultVO = atmTaskResultVO.getCleanResultVO();
//            atmTaskNew.setClearSite(cleanResultVO.getClearSite());
//            //处理加钞钞盒记录
//            List<CashboxPackRecord> cashboxPackRecords = null;
//            Set<Long> packIds = null;
//            String errorMsg = "";
//            if(atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) { //加钞任务
//                //判断是否钞袋/钞盒
//                List<Cashbox> cashboxList = cashboxService.listByBatchBoxNo(cleanResultVO.getUsedBoxList());
//                Map<String,Cashbox> cashboxMap = cashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
//                for (String boxNo : cleanResultVO.getUsedBoxList()) {
//                    if (!cashboxMap.containsKey(boxNo)) {
//                        errorMsg = "无效加钞盒: " + boxNo;
//                        log.warn(errorMsg);
//                    }
//                }
//                packIds = new HashSet<>();
//                for (Cashbox cashbox : cashboxList) {
//                    if (0L == cashbox.getPackId()) {
//                        errorMsg = "加钞盒未绑定: " + cashbox.getBoxNo();
//                        log.warn(errorMsg);
//                    } else {
//                        packIds.add(cashbox.getPackId());
//                    }
//                }
//                //判断钞盒是否已使用
//                if (packIds.size() > 0) {
//                    cashboxPackRecords = new ArrayList<>();
//                    List<CashboxPackRecord> packRecords = cashboxPackRecordService.listByIds(packIds);
//                    for (CashboxPackRecord packRecord : packRecords) {
//                        if (!packRecord.getDenomId().equals(atmTask.getDenomId())) {
//                            errorMsg = "钞盒券别不正确: " + packRecord.getBoxNo();
//                            log.warn(errorMsg);
//                        } else if (packRecord.getAtmId() != 0L && packRecord.getSecondAtmId() != 0L) {
//                            //一个钞盒最多扫两次
//                            errorMsg = "加钞钞盒已使用: " + packRecord.getBoxNo();
//                            log.warn(errorMsg);
//                        } else {
//                            cashboxPackRecords.add(packRecord);
//                        }
//                    }
//                }
//            }
//            /**
//             * 回笼钞盒处理
//             */
//            boolean returnBoxError = false;
//            List<Cashbox> returnCashboxList = cashboxService.listByBatchBoxNo(cleanResultVO.getReturnBoxList());
//            Map<String,Cashbox> cashboxMap = returnCashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
//            for (String boxNo : cleanResultVO.getReturnBoxList()) {
//                if (!cashboxMap.containsKey(boxNo)) {
//                    errorMsg = "无效回笼钞袋: " + boxNo;
//                    returnBoxError = true;
//                    log.warn(errorMsg);
//                    break;
//                }
//            }
//            //查询钞袋当前是否已使用
//            if (!returnBoxError) {
//                String taskDate = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(), "yyyy-MM-dd");
//                for (String boxNo : cleanResultVO.getReturnBoxList()) {
//                    List<AtmTaskReturn> atmTaskReturnList = atmTaskReturnService.listByBoxNo(boxNo, taskDate);
//                    for (AtmTaskReturn atmTaskReturn : atmTaskReturnList) {
//                        if (atmTaskReturn.getClearFlag() != 1) {
//                            errorMsg = "回笼钞袋不能重复使用: " + atmTaskReturn.getBoxBarCode();
//                            log.warn(errorMsg);
//                            returnBoxError = true;
//                            break;
//                        }
//                    }
//                    if (returnBoxError) {
//                        break;
//                    }
//                }
//            }
//            //更新加钞钞盒状态
//            if (cashboxPackRecords != null) {
//                Long routeId = route != null ? route.getId() : null;
//                this.updateCashboxPackRecordAtmIdBatch(cashboxPackRecords, routeId, atmTask.getAtmId(), BoxStatusEnum.OPEN);
//                atmTaskNew.setCashboxList(StringUtils.join(packIds, ','));
//                this.saveCashboxScanRecordBatch(new ArrayList<>(packIds),BoxScanNodeEnum.CASHIN,System.currentTimeMillis());
//            }
//            //保存回笼钞袋,放在最后统一保存，避免中途异常返回导致无效处理
//            if (!returnBoxError) { //钞盒有效才保存
//                this.saveReturnCashbox(atmTask, cleanResultVO.getReturnBoxList());
//            }
//        }
//        /**
//         * 巡检记录保存
//         */
//        if (null != atmTaskResultVO.getCheckResultVO()) {
//            this.saveAtmCheckRecord(atmTask,atmTaskResultVO.getCheckResultVO());
//        }
//        atmTaskService.updateById(atmTaskNew);
//    }

    @Transactional
    public void saveOfflineAtmTaskResult(AtmTaskResultVO atmTaskResultVO) throws BaseException {
        if (checkFinishTaskRepeat(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId())) {
            log.warn("重复提交：" + atmTaskResultVO.toString());
            throw new BusinessException("重复提交");
        }
        AtmTask atmTask = atmTaskService.getById(atmTaskResultVO.getTaskId());
        if (null == atmTask) {
            log.warn("无效任务:" + atmTaskResultVO.toString());
            throw new BaseException(SystemErrorType.UPLOAD_TASK_NOTEXSIT,SystemErrorType.UPLOAD_TASK_NOTEXSIT.getMesg());
        }
        boolean dataError = false;
        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
            if (null == atmTaskResultVO.getRepairResultVO()) {
                dataError = true;
            }
        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
            //加钞记录保存
            AtmSubTaskCleanResultVO cleanResultVO = atmTaskResultVO.getCleanResultVO();
            if (null == cleanResultVO || null == cleanResultVO.getReturnBoxList()) {
                dataError = true;
            }
        }
        if (dataError) {
            log.info(SystemErrorType.ARGUMENT_NOT_VALID.getMesg() + ":" + atmTaskResultVO.toString());
            throw new BaseException(SystemErrorType.ARGUMENT_NOT_VALID,SystemErrorType.ARGUMENT_NOT_VALID.getMesg());
        }

        if (checkAtmTaskUploadExist(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId())) {
            log.info(SystemErrorType.UPLOAD_TASK_REPEAT.getMesg() + ":" + atmTaskResultVO.toString());
            throw new BaseException(SystemErrorType.UPLOAD_TASK_REPEAT,SystemErrorType.UPLOAD_TASK_REPEAT.getMesg());
        }
        //TODO 保存离线数据，以备异常时查询数据
        //
        addNewTaskToUploadMapCache(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getTaskId());

        Route route = routeService.getById(atmTask.getCarryRouteId());
        if (atmTaskResultVO.getBankCardInfos() != null && atmTaskResultVO.getBankCardInfos().size() > 0) {
            this.saveBankCard(route,atmTask, atmTaskResultVO.getBankCardInfos());
        }

        AtmTask atmTaskNew = new AtmTask();
        atmTaskNew.setId(atmTask.getId());
        atmTaskNew.setBeginTime(atmTaskResultVO.getBeginTime());
        atmTaskNew.setEndTime(atmTaskResultVO.getEndTime());
        atmTaskNew.setOffline(1);
        atmTaskNew.setChannel(ChannelEnum.FROM_PC.getValue());
        atmTaskNew.setAtmRunStatus(atmTaskResultVO.getAtmRunStatus());
        atmTaskNew.setStuckAmount(new BigDecimal(atmTaskResultVO.getStuckAmount()));
        atmTaskNew.setCleanOpManId(null != route? route.getRouteOperMan() : null);
        atmTaskNew.setCleanKeyManId(null != route? route.getRouteKeyMan() : null);
        atmTaskNew.setStatusT(AtmTaskStatusEnum.FINISH.getValue());
        /**
         * 巡检记录保存
         */
        if (null != atmTaskResultVO.getCheckResultVO()) {
            this.saveAtmCheckRecord(atmTaskNew,atmTaskResultVO.getCheckResultVO());
        }
        //维修记录保存
        if (atmTask.getTaskType() == AtmTaskTypeEnum.REPAIR.getValue()) {
            this.saveRepairRecord(atmTask,atmTaskResultVO.getRepairResultVO());
        } else if (atmTask.getTaskType() == AtmTaskTypeEnum.CLEAN.getValue()
                || atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) {
            //加钞记录保存
            AtmSubTaskCleanResultVO cleanResultVO = atmTaskResultVO.getCleanResultVO();
            atmTaskNew.setClearSite(cleanResultVO.getClearSite());
            //处理加钞钞盒记录
            List<CashboxPackRecord> cashboxPackRecords = null;

            String errorMsg = "";
            if(atmTask.getTaskType() == AtmTaskTypeEnum.CASHIN.getValue()) { //加钞任务
                //判断是否钞袋/钞盒
                if (cleanResultVO.getUsedBoxList() == null || cleanResultVO.getUsedBoxList().size() == 0) {
                    log.warn("无效数据:" + atmTaskResultVO.toString());
                }
                try {
                    cashboxPackRecords = checkAddCashbox(cleanResultVO.getUsedBoxList(),atmTask);
                } catch (Exception e) {
                    //离线交易出现异常，也要继续
                    log.error(e.getMessage(),e);
                }
            }
            /**
             * 回笼钞盒处理
             */
            boolean returnBoxError = false;
            List<Cashbox> returnCashboxList = cashboxService.listByBatchBoxNo(cleanResultVO.getReturnBoxList());
            Map<String,Cashbox> cashboxMap = returnCashboxList.stream().collect(Collectors.toMap(Cashbox::getBoxNo,Function.identity(),(key1,key2)->key2));
            for (String boxNo : cleanResultVO.getReturnBoxList()) {
                if (!cashboxMap.containsKey(boxNo)) {
                    errorMsg = "无效回笼钞袋: " + boxNo;
                    returnBoxError = true;
                    log.warn(errorMsg);
                    break;
                }
            }
            //查询钞袋当前是否已使用
            if (!returnBoxError) {
                String taskDate = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(), "yyyy-MM-dd");
                for (String boxNo : cleanResultVO.getReturnBoxList()) {
                    List<AtmTaskReturn> atmTaskReturnList = atmTaskReturnService.listByBoxNo(boxNo, taskDate);
                    for (AtmTaskReturn atmTaskReturn : atmTaskReturnList) {
                        if (atmTaskReturn.getClearFlag() != 1) {
                            errorMsg = "回笼钞袋不能重复使用: " + atmTaskReturn.getBoxBarCode();
                            log.warn(errorMsg);
                            returnBoxError = true;
                            break;
                        }
                    }
                    if (returnBoxError) {
                        break;
                    }
                }
            }
            //更新加钞钞盒状态
            if (cashboxPackRecords != null) {
                Long routeId = route != null ? route.getId() : null;
                Set<Long> packIds = cashboxPackRecords.stream().map(CashboxPackRecord::getId).collect(Collectors.toSet());
                this.updateCashboxPackRecordAtmIdBatch(cashboxPackRecords, routeId, atmTask.getAtmId(), BoxStatusEnum.OPEN,cleanResultVO.getUsedBoxList());
                atmTaskNew.setCashboxList(StringUtils.join(packIds, ','));
                this.saveCashboxScanRecordBatch(new ArrayList<>(packIds),BoxScanNodeEnum.CASHIN,System.currentTimeMillis());
            }
            //保存回笼钞袋,放在最后统一保存，避免中途异常返回导致无效处理
            if (!returnBoxError) { //钞盒有效才保存
                this.saveReturnCashbox(atmTask, cleanResultVO.getReturnBoxList());
            }
        }

        atmTaskService.updateById(atmTaskNew);
    }

    /**
     * 创建维修任务+结果保存
     * @param atmTaskResultVO
     */
    @Transactional
    public void createAtmTaskResult(AtmTaskResultVO atmTaskResultVO) throws BaseException {
        if (checkFinishTaskRepeat(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getEndTime())) {
            log.warn("重复提交: " + atmTaskResultVO.toString());
            throw new BusinessException("重复提交");
        }
        if (null == atmTaskResultVO.getRepairResultVO()) {
            log.info(SystemErrorType.ARGUMENT_NOT_VALID.getMesg() + ":" + atmTaskResultVO.toString());
            throw new BaseException(SystemErrorType.ARGUMENT_NOT_VALID,SystemErrorType.ARGUMENT_NOT_VALID.getMesg());
        }

        Route route = routeService.getById(atmTaskResultVO.getRouteId());
        if (null == route) {
            log.warn("无效线路: " + atmTaskResultVO.getRouteId());
            throw new BaseException(SystemErrorType.UPLOAD_TASK_NOTEXSIT,"无效线路");
        }
        AtmDevice atmDevice = atmDeviceService.getByAtmNo(atmTaskResultVO.getAtmNo());
        if (null == atmDevice) {
            log.warn("无效ATM: " + atmTaskResultVO.getRouteId());
            throw new BaseException(SystemErrorType.UPLOAD_TASK_NOTEXSIT,"无效ATM");
        }

        if (atmTaskResultVO.getOffline() == 1) {
            if (checkAtmTaskUploadExist(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getEndTime())) {
                log.warn("重复上送: " + atmTaskResultVO.toString());
                throw new BaseException(SystemErrorType.UPLOAD_TASK_REPEAT,"重复上送");
            }
            //TODO 保存离线数据，以备异常时查询数据
            //
            addNewTaskToUploadMapCache(atmTaskResultVO.getAtmNo(),atmTaskResultVO.getEndTime());
        }

        Bank subBank = bankService.getById(atmDevice.getSubBankId());
        Long routeId = 0L;
        if (null != subBank) {
            Route fixRoute = routeService.getByRouteNo(System.currentTimeMillis(),subBank.getRouteNo());
            if (null != fixRoute) {
                routeId = fixRoute.getId();
            }
        }
        //保存维护任务
        AtmTask atmTaskNew = new AtmTask();
        atmTaskNew.setRouteId(routeId);
        atmTaskNew.setCarryRouteId(route.getId());
        atmTaskNew.setTaskDate(DateTimeUtil.getDailyStartTimeMs(System.currentTimeMillis()));
        atmTaskNew.setOffline(atmTaskResultVO.getOffline());
        atmTaskNew.setChannel(ChannelEnum.FROM_MOBILE.getValue());
        atmTaskNew.setBankId(atmDevice.getBankId());
        atmTaskNew.setSubBankId(atmDevice.getSubBankId());
        atmTaskNew.setAtmId(atmDevice.getId());
        atmTaskNew.setDepartmentId(route.getDepartmentId());
        atmTaskNew.setBeginTime(atmTaskResultVO.getBeginTime());
        atmTaskNew.setEndTime(atmTaskResultVO.getEndTime());
        atmTaskNew.setAtmRunStatus(atmTaskResultVO.getAtmRunStatus());
        atmTaskNew.setStuckAmount(new BigDecimal(atmTaskResultVO.getStuckAmount()));
        atmTaskNew.setCleanOpManId(route.getRouteOperMan());
        atmTaskNew.setCleanKeyManId(route.getRouteKeyMan());
        atmTaskNew.setStatusT(AtmTaskStatusEnum.FINISH.getValue());
        atmTaskNew.setTaskType(AtmTaskTypeEnum.REPAIR.getValue());
        /**
         * 巡检记录保存
         */
        if (null != atmTaskResultVO.getCheckResultVO()) {
            this.saveAtmCheckRecord(atmTaskNew,atmTaskResultVO.getCheckResultVO());
        }

        String faultType = atmTaskResultVO.getRepairResultVO().getFaultType();
        String faultTypeText = AtmFaultType.getFaultTypeText(faultType);
        if (!StringUtils.isEmpty(faultTypeText)) {
            atmTaskNew.setComments(faultTypeText);
            atmTaskNew.setRepairContent(faultTypeText);
        } else { //其他故障，直接取
            atmTaskNew.setRepairContent(atmTaskResultVO.getRepairResultVO().getDescription());
            atmTaskNew.setComments(atmTaskResultVO.getRepairResultVO().getDescription());
        }
        atmTaskService.save(atmTaskNew);

        if (atmTaskResultVO.getBankCardInfos() != null
                && atmTaskResultVO.getBankCardInfos().size() > 0) {
            this.saveBankCard(route,atmTaskNew, atmTaskResultVO.getBankCardInfos());
        }
        //维修记录保存
        this.saveRepairRecord(atmTaskNew,atmTaskResultVO.getRepairResultVO());
    }

    /**
     * 保存离行式网点巡检结果
     * @param atmBankCheckRecordVO
     */
    public void saveBankCheckResult(AtmBankCheckRecordVO atmBankCheckRecordVO) throws BusinessException {
        Route route = routeService.getById(atmBankCheckRecordVO.getRouteId());
        if (null == route) {
            log.warn("无效线路id: "+ atmBankCheckRecordVO.getRouteId());
            throw new BusinessException("无效线路");
        }
        long bankId = 0;
        Bank bank = bankService.getById(atmBankCheckRecordVO.getSubBankId());
        if (null != bank) {
            String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length() - 1).split("/");
            if (parentsIds.length > 1) {
                bankId = Long.parseLong(parentsIds[parentsIds.length - 1]);
            }
        }
        AtmBankCheckRecord atmBankCheckRecord = new AtmBankCheckRecord();
        atmBankCheckRecord.setRouteId(atmBankCheckRecordVO.getRouteId());
        atmBankCheckRecord.setBankId(bankId);
        atmBankCheckRecord.setDepartmentId(route.getDepartmentId());
        atmBankCheckRecord.setSubBankId(atmBankCheckRecordVO.getSubBankId());
        atmBankCheckRecord.setCheckTime(atmBankCheckRecordVO.getCheckTime());
        atmBankCheckRecord.setSetAlarmTime(atmBankCheckRecordVO.getSetAlarmTime());
        atmBankCheckRecord.setRevokeAlarmTime(atmBankCheckRecordVO.getRevokeAlarmTime());
        atmBankCheckRecord.setComments(atmBankCheckRecordVO.getComments());
        int checkNormal = 0;
        if (atmBankCheckRecordVO.getHallCheckResult() != null) {
            atmBankCheckRecord.setHallCheckResult(JSONObject.toJSONString(atmBankCheckRecordVO.getHallCheckResult()));
            for (String key: atmBankCheckRecordVO.getHallCheckResult().keySet()) {
                if (atmBankCheckRecordVO.getHallCheckResult().getOrDefault(key,0) == 0) {
                    checkNormal = 1;
                    break;
                }
            }
        }
        if (atmBankCheckRecordVO.getRoomCheckResult() != null) {
            atmBankCheckRecord.setRoomCheckResult(JSONObject.toJSONString(atmBankCheckRecordVO.getRoomCheckResult()));
            for (String key: atmBankCheckRecordVO.getRoomCheckResult().keySet()) {
                if (atmBankCheckRecordVO.getRoomCheckResult().getOrDefault(key,0) == 0) {
                    checkNormal = 1;
                    break;
                }
            }
        }
        atmBankCheckRecord.setCheckNormal(checkNormal);
        List<Long> checkManList = new ArrayList<>();
        if (route.getRouteKeyMan() != 0L) {
            checkManList.add(route.getRouteKeyMan());
        }
        if (route.getRouteOperMan() != 0L) {
            checkManList.add(route.getRouteOperMan());
        }
        atmBankCheckRecord.setCheckMans(StringUtils.join(checkManList,','));
        atmBankCheckRecordService.save(atmBankCheckRecord);
    }


    /**
     * 根据线路查询吞没卡任务列表
     * @param routeId
     * @param type  0 - 回笼线路，1 - 送卡线路
     * @return
     */
    public List<AtmTaskCardGroupDTO> listBankCardByRoute(Long routeId,int type){

        List<AtmTaskCard> cardList = null;
        if (type == 0) {
            AtmTaskCard where = new AtmTaskCard();
            where.setRouteId(routeId);
            cardList = atmTaskCardService.listByCondition(where);
        } else {
            cardList = atmTaskCardService.listDispatchCard(routeId);
        }

        if (cardList.size() == 0) {
            return new ArrayList<>();
        }
        /**
         * 将atm任务按银行网点分组
         */
        Map<Long,List<AtmTaskCardGroupItem>> subBankCardMap = new HashMap<>();
        cardList.forEach(taskCard -> {
            List<AtmTaskCardGroupItem> atmTaskCardGroupItemList = subBankCardMap.computeIfAbsent(taskCard.getDeliverBankId(), k -> new ArrayList<>());
            AtmTaskCardGroupItem atmTaskCardGroupItem = new AtmTaskCardGroupItem();
            atmTaskCardGroupItem.setCardBank(taskCard.getCardBank());
            atmTaskCardGroupItem.setCardNo(taskCard.getCardNo());
            atmTaskCardGroupItem.setStatusT(taskCard.getStatusT());
            atmTaskCardGroupItem.setId(taskCard.getId());
            atmTaskCardGroupItem.setCategory(taskCard.getCategory());
            atmTaskCardGroupItemList.add(atmTaskCardGroupItem);
        });
        /**
         * 查询银行信息
         */
        Set<Long> bankIdSet = cardList.stream().map(AtmTaskCard::getDeliverBankId).collect(Collectors.toSet());
        List<Bank> bankList = bankService.listByIds(bankIdSet);
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));

        //排序 返回
        List<AtmTaskCardGroupDTO> atmTaskCardGroupDTOS = new ArrayList<>();
        bankIdSet.forEach(subBankId -> {
            List<AtmTaskCardGroupItem> taskCardGroupItems = subBankCardMap.get(subBankId);
            if (taskCardGroupItems != null) {
                AtmTaskCardGroupDTO atmTaskCardGroupDTO = new AtmTaskCardGroupDTO();
                Bank bank = bankMap.get(subBankId); //网点
                if (bank != null) {
                    atmTaskCardGroupDTO.setBankName(bank.getFullName());
                    atmTaskCardGroupDTO.setBankId(bank.getId());
                }
                atmTaskCardGroupDTO.setGroupList(taskCardGroupItems);
                atmTaskCardGroupDTOS.add(atmTaskCardGroupDTO);
            }
        });
        return atmTaskCardGroupDTOS;
    }

    /**
     * 根据线路查询待交接吞没卡列表
     * @param routeId
     * @param type  0 - 回笼交接，1 - 送卡交接
     * @return
     */
    public HandOverBankCardDTO listHandOverBankCardByRoute(Long routeId,int type) throws BusinessException{
        Route route = routeService.getById(routeId);
        if (null == route) {
            log.warn("无效线路id: "+ routeId);
            throw new BusinessException("无效参数");
        }

        Employee routeKeyMan = employeeService.getById(route.getRouteKeyMan());
        Employee routeOpMan = employeeService.getById(route.getRouteOperMan());
        HandOverBankCardDTO handOverBankCardDTO = new HandOverBankCardDTO();
        handOverBankCardDTO.setRouteId(routeId);
        handOverBankCardDTO.setRouteNo(route.getRouteNo());
        handOverBankCardDTO.setRouteName(route.getRouteName());
        handOverBankCardDTO.setKeyManName((routeKeyMan != null)? routeKeyMan.getEmpName() : "");
        handOverBankCardDTO.setOpManName((routeOpMan != null)? routeOpMan.getEmpName() : "");
        /**
         * 查询吞没卡列表
         */
        AtmTaskCard where = new AtmTaskCard();
        if (type == 0) { // 上缴
            where.setRouteId(routeId);
            where.setStatusT(BankCardStatusEnum.RETRIVE.getValue());
        } else { //分配
            where.setDeliverRouteId(routeId);
            where.setStatusT(BankCardStatusEnum.ALLOCATION.getValue());
        }
        List<AtmTaskCard> cardList = atmTaskCardService.listByCondition(where);
        if (cardList.size() == 0) {
            handOverBankCardDTO.setCardList(new ArrayList<>());
        } else {
            List<AtmTaskCardGroupItem> groupItemList = cardList.stream().map(atmTaskCard -> {
                AtmTaskCardGroupItem atmTaskCardGroupItem = new AtmTaskCardGroupItem();
                atmTaskCardGroupItem.setCardBank(atmTaskCard.getCardBank());
                atmTaskCardGroupItem.setCardNo(atmTaskCard.getCardNo());
                atmTaskCardGroupItem.setStatusT(atmTaskCard.getStatusT());
                atmTaskCardGroupItem.setId(atmTaskCard.getId());
                atmTaskCardGroupItem.setCategory(atmTaskCard.getCategory());
                return atmTaskCardGroupItem;
            }).collect(Collectors.toList());
            handOverBankCardDTO.setCardList(groupItemList);
        }

        return handOverBankCardDTO;
    }

    /**
     * 吞没卡上交、取卡
     * @param atmTaskCardDealDTO
     * @return
     */
    @Transactional
    public void updateBankCardDeliver(AtmTaskCardDealDTO atmTaskCardDealDTO) throws BusinessException{
        validateTaskCardParameter(atmTaskCardDealDTO);
        try {
            for (Long id : atmTaskCardDealDTO.getCardIdList()) {
                AtmTaskCard atmTaskCard = atmTaskCardService.getById(id);
                if (atmTaskCard == null) {
                    throw new BusinessException("无效卡");
                }
                if (atmTaskCard.getStatusT() != BankCardStatusEnum.DISPATCH.getValue()
                        && atmTaskCard.getStatusT() != BankCardStatusEnum.RETRIVE.getValue()) {
                    throw new BusinessException("卡片状态不正确");
                }
                if (atmTaskCard.getCategory() == BankCardCategoryEnum.PAPER.getValue() && atmTaskCardDealDTO.getDeliverType() == 1) {
                    throw new BusinessException("单据不支持现场拿卡");
                }
                AtmTaskCard newAtmTaskCard = new AtmTaskCard();
                newAtmTaskCard.setId(id);
                newAtmTaskCard.setStatusT(BankCardStatusEnum.DELIVER.getValue());
                newAtmTaskCard.setReceiveTime(atmTaskCardDealDTO.getDealTime());
                if (atmTaskCardDealDTO.getDeliverType() == 0) {
                    newAtmTaskCard.setDeliverType(BankCardDeliverTypeEnum.TOBANK.getValue());
                } else if (atmTaskCardDealDTO.getDeliverType() == 1) {
                    newAtmTaskCard.setDeliverType(BankCardDeliverTypeEnum.TOCUST.getValue());
                }
                //当日现场取卡，派送线路未配置
                if (atmTaskCard.getDeliverRouteId() == 0L) {
                    newAtmTaskCard.setDeliverRouteId(atmTaskCardDealDTO.getRouteId());
                }
                newAtmTaskCard.setReceiverName(atmTaskCardDealDTO.getReceiverName());
                newAtmTaskCard.setReceiverIdno(atmTaskCardDealDTO.getReceiverIdno());
                atmTaskCardService.updateById(newAtmTaskCard);

                //现场拿卡，需要生成一个回执单据
                if (atmTaskCardDealDTO.getDeliverType() == 1) {
                    AtmTaskCard atmTaskCardPaper = new AtmTaskCard();
                    atmTaskCardPaper.setId(atmTaskCard.getId());
                    atmTaskCardPaper.setReceiveTime(System.currentTimeMillis());
                    atmTaskCardPaper.setRouteNo(atmTaskCard.getDeliverRouteNo());
                    atmTaskCardPaper.setRouteId(atmTaskCardDealDTO.getRouteId()); //使用当前执行线路
                    atmTaskCardPaper.setBankId(atmTaskCard.getBankId());
                    atmTaskCardPaper.setCardNo(atmTaskCard.getCardNo());
                    atmTaskCardPaper.setCardBank(atmTaskCard.getCardBank());
                    atmTaskCardPaper.setAtmId(atmTaskCard.getAtmId());
                    atmTaskCardPaper.setTaskId(atmTaskCard.getTaskId());
                    atmTaskCardPaper.setDepartmentId(atmTaskCard.getDepartmentId());
                    atmTaskCardPaper.setDeliverBankId(atmTaskCard.getDeliverBankId());
                    atmTaskCardPaper.setDeliverRouteNo(atmTaskCard.getDeliverRouteNo());
                    atmTaskCardPaper.setDeliverDay(DateTimeUtil.getNextdayString());
                    atmTaskCardPaper.setCategory(BankCardCategoryEnum.PAPER.getValue());
                    atmTaskCardPaper.setStatusT(BankCardStatusEnum.RETRIVE.getValue());
                    atmTaskCardService.save(atmTaskCardPaper);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
//
//    /**
//     * 吞没卡回笼交接
//     * @param atmTaskCardDealDTO
//     * @return
//     */
//    @Transactional
//    public void updateBankCardCollect(AtmTaskCardDealDTO atmTaskCardDealDTO) throws BusinessException{
//        validateTaskCardParameter(atmTaskCardDealDTO);
//        try {
//            for (Long id : atmTaskCardDealDTO.getCardIdList()) {
//                AtmTaskCard atmTaskCard = atmTaskCardService.getById(id);
//                if (atmTaskCard != null && atmTaskCard.getStatusT() == BankCardStatusEnum.RETRIVE.getValue()) {
//                    AtmTaskCard newAtmTaskCard = new AtmTaskCard();
//                    newAtmTaskCard.setId(id);
//                    newAtmTaskCard.setStatusT(BankCardStatusEnum.COLLECT.getValue());
//                    newAtmTaskCard.setCollectTime(atmTaskCardDealDTO.getDealTime());
//                    newAtmTaskCard.setCollectManA(UserContextHolder.getUserId());
//                    atmTaskCardService.updateById(newAtmTaskCard);
//                }
//            }
//        } catch (Exception e) {
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    /**
//     * 吞没卡派送分配
//     * @param atmTaskCardDealDTO
//     * @return
//     */
//    @Transactional
//    public void updateBankCardDispatch(AtmTaskCardDealDTO atmTaskCardDealDTO) throws BusinessException{
//        validateTaskCardParameter(atmTaskCardDealDTO);
//        try {
//            for (Long id : atmTaskCardDealDTO.getCardIdList()) {
//                AtmTaskCard atmTaskCard = atmTaskCardService.getById(id);
//                if (atmTaskCard != null && atmTaskCard.getStatusT() == BankCardStatusEnum.ALLOCATION.getValue()) {
//                    AtmTaskCard newAtmTaskCard = new AtmTaskCard();
//                    newAtmTaskCard.setId(id);
//                    newAtmTaskCard.setStatusT(BankCardStatusEnum.DISPATCH.getValue());
//                    newAtmTaskCard.setDispatchTime(atmTaskCardDealDTO.getDealTime());
//                    newAtmTaskCard.setDispatchManA(UserContextHolder.getUserId());
//                    atmTaskCardService.updateById(newAtmTaskCard);
//                }
//            }
//        } catch (Exception e) {
//            throw new BusinessException(e.getMessage());
//        }
//    }

    private void validateTaskCardParameter(AtmTaskCardDealDTO atmTaskCardDealDTO) throws BusinessException{
        if (atmTaskCardDealDTO.getCardIdList() == null || atmTaskCardDealDTO.getCardIdList().size() == 0) {
            throw new BusinessException("缺少吞没卡");
        }
    }


    /**
     * 车长日志上传
     * @param routeLogDTO
     */
    @Transactional
    public void saveRouteLog(RouteLogDTO routeLogDTO) throws BusinessException{
        Route route = routeService.getById(routeLogDTO.getRouteId());
        if (null == route) {
            log.warn("无效线路id = " + routeLogDTO.getRouteId());
            throw new BusinessException("无效线路id");
        }

        int result = 1; //默认都正常
        if (routeLogDTO.getDetails() != null && routeLogDTO.getDetails().size() > 0) {
            for (RouteLogItem item : routeLogDTO.getDetails()) {
                if (0 == item.getChk()) {
                    result = 0;
                    break;
                }
            }
        }

        RouteLog routeLog = new RouteLog();
        routeLog.setRouteId(route.getId());
        routeLog.setResult(result);
        routeLog.setDetail(JacksonUtil.toJson(routeLogDTO.getDetails()));
        routeLog.setComments(routeLogDTO.getComments());
        routeLogService.save(routeLog);

        Route routeTmp = new Route();
        routeTmp.setId(route.getId());
        routeTmp.setLeaderLog(result==1? 1:2);
        routeService.updateById(routeTmp);
    }

    public List<RouteLogTemplate> queryRouteLogTemplate() {

        SysDictionary dictionary = new SysDictionary();
        dictionary.setGroups("ROUTE_LOG_ITEM");
        List<SysDictionary> dictionaries = sysDictionaryService.getDictionaryByCondition(dictionary);
        List<RouteLogTemplate> list = new ArrayList<>();
        if (null != dictionaries) {
            for (SysDictionary dict : dictionaries) {
                RouteLogTemplate template = new RouteLogTemplate();
                template.setCode(dict.getCode());
                template.setName(dict.getContent());
                list.add(template);
            }
        }
        return list;
    }
//    public RouteLogDTO getRouteLog(Long id) {
//        RouteLog routeLog = routeLogService.getById(id);
//        if (null == log) {
//            log.warn("无效id = " + id);
//            throw new BusinessException("无效日志id");
//        }
//        RouteLogDTO routeLogDTO = new RouteLogDTO();
//        routeLogDTO.setComments(routeLog.getComments());
//        Map<String,RouteLogItem> itemMap = (Map<String,RouteLogItem>)JSONObject.parse(routeLog.getDetail());
//        routeLogDTO.setDetails(itemMap);
//        routeLogDTO.setRouteId(routeLog.getRouteId());
//        return routeLogDTO;
//    }

    public List<DispBoxDTO> listDispBox(Long routeId) {
        List<DispBoxDTO> dispBoxDTOList = new ArrayList<>();
        List<CashboxPackRecord> cashboxPackRecords = cashboxPackRecordService.listByRoute(routeId);
        Map<Long,Denom> denomMap = new HashMap<>();
        if (cashboxPackRecords.size() > 0) {
            Set<Long> ids = cashboxPackRecords.stream().map(CashboxPackRecord::getDenomId).collect(Collectors.toSet());
            List<Denom> denomList = denomService.listByIds(ids);
            if (denomList.size() > 0) {
                denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Function.identity()));
            }
        }
        for (CashboxPackRecord record : cashboxPackRecords) {
            DispBoxDTO dispBoxDTO = new DispBoxDTO();
            dispBoxDTO.setBoxNo(record.getBoxNo());
            dispBoxDTO.setUseCount(record.getUseCount());
            Denom denom = denomMap.get(record.getDenomId());
            if (null != denom) {
                dispBoxDTO.setDenomValue(denom.getValue().intValue());
            }
            dispBoxDTOList.add(dispBoxDTO);
        }
        return  dispBoxDTOList;
    }
}
