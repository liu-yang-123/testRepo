package com.zcxd.base.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.zcxd.base.dto.*;
import com.zcxd.base.vo.*;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ExcelImportUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.AtmAdditionCashMapper;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.mapper.VaultOrderTaskMapper;
import com.zcxd.db.model.AtmAdditionCash;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import com.zcxd.db.model.AtmTaskImportRecord;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.VaultOrderTask;
import com.zcxd.db.model.result.AtmDenomAmountResult;
import com.zcxd.db.model.result.BankAtmTaskResult;

import javax.annotation.Resource;

/**
 * 
 * @ClassName ATMTaskService
 * @Description 清机任务服务层
 * @author 秦江南
 * @Date 2021年6月2日下午6:40:44
 */
@Slf4j
@Service
public class ATMTaskService extends ServiceImpl<AtmTaskMapper, AtmTask>{
	
    @Autowired
    private ATMService atmService;
    
    @Autowired
    private CommonService commonService;
    
	@Autowired
	private BankService bankService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private AtmTaskMapper taskMapper;

    @Autowired
    private DenomMapper denomMapper;

    @Autowired
    private VaultOrderTaskMapper orderTaskMapper;
    
    @Autowired
    private ATMTaskImportRecordService atmTaskImportRecordService;

    @Autowired
    private AtmAdditionCashMapper additionCashMapper;

    @Resource
	private RedisUtil redisUtil;

	@Value("${wechat.push}")
	private int wxPushNotice;
    @Value("${importType.hzicbc}")
    private int hzicbc;
    @Value("${importType.rcb}")
    private int rcb;
    @Value("${importType.bob}")
    private int bob;
    @Value("${importType.jdicbc}")
    private int jdicbc;
    
	

    private static final int ADD_TASK = 1;
    private static final int CANCEL_TASK = 2;

    private boolean isWxPushEnable() {
    	return wxPushNotice == 1;
	}
	/**
	 * 
	 * @Title savemultiClean
	 * @Description 批量添加ATM维修任务
	 * @param atmTaskRepairBatchVO
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result savemultiRepair(ATMTaskRepairBatchVO atmTaskRepairBatchVO) {
		
		if(atmTaskRepairBatchVO.getAtmTashRepairList() == null || atmTaskRepairBatchVO.getAtmTashRepairList().size()==0){
			throw new BusinessException(-1, "维修任务列表为空");
		}
		
    	//得到设备集合
    	Set<Long> atmIds = atmTaskRepairBatchVO.getAtmTashRepairList().stream().map(ATMTaskRepairVO::getAtmId).collect(Collectors.toSet());
    	List<AtmDevice> atmDevices = atmService.listByIds(atmIds);
    	Map<Long, AtmDevice> atmMap = atmDevices.stream().collect(Collectors.toMap(AtmDevice::getId,atmDevice -> atmDevice));
    	
    	//得到设备网点集合
    	Set<Long> babkIds = atmDevices.stream().map(AtmDevice::getSubBankId).collect(Collectors.toSet());
    	List<Bank> banks = bankService.listByIds(babkIds);
    	Map<Long, Bank> bankMap = banks.stream().collect(Collectors.toMap(Bank::getId,bank -> bank));
    	
    	//获取当天线路集合
    	List<Map<String, Object>> routeList = routeService.listMaps((QueryWrapper)Wrappers.query()
    			.select("id,route_no as routeNo,department_id as departmentId,status_t as statusT").eq("route_date", atmTaskRepairBatchVO.getTaskDate()).eq("deleted", 0));
    	Map<Object, Map> routeMap = routeList.stream().collect(Collectors.toMap(map->map.get("routeNo"),map->map));
		List<WxTaskNotice> wxTaskNoticeList = new ArrayList<>(); //推送通知
		for (ATMTaskRepairVO atmTaskRepairVO : atmTaskRepairBatchVO.getAtmTashRepairList()) {
			//得到ATM设备信息
			AtmDevice atmDevice = atmMap.get(atmTaskRepairVO.getAtmId());
			Bank bank = bankMap.get(atmDevice.getSubBankId());
			
			
			if(!routeMap.containsKey(bank.getRouteNo())){
				return Result.fail(DateTimeUtil.getDateTimeDisplayString(atmTaskRepairBatchVO.getTaskDate()) + bank.getRouteNo() + "线路不存在,请先创建线路");
			}
		
//			if(routeMap.get(bank.getRouteNo()).get("statusT").equals(RouteStatusEnum.FINISH.getValue())){
//				return Result.fail(DateTimeUtil.getDateTimeDisplayString(atmTaskRepairBatchVO.getTaskDate()) + bank.getRouteNo() + "线路已经交接完成，不能追加任务");
//			}
			
			Map route = routeMap.get(bank.getRouteNo());
			
			AtmTask atmTask = new AtmTask();
			atmTask.setAtmId(atmTaskRepairVO.getAtmId());
			atmTask.setBankId(atmDevice.getBankId());
			atmTask.setSubBankId(atmDevice.getSubBankId());
			atmTask.setTaskType(AtmTaskTypeEnum.REPAIR.getValue());
			atmTask.setTaskDate(atmTaskRepairBatchVO.getTaskDate());
			atmTask.setRouteId(Long.parseLong(route.get("id").toString()));
			atmTask.setDepartmentId(Long.parseLong(route.get("departmentId").toString()));
			atmTask.setCarryRouteId(Long.parseLong(route.get("id").toString()));
			atmTask.setStatusT(AtmTaskStatusEnum.CONFRIM.getValue());
			atmTask.setRepairPlanTime(StringUtils.isBlank(atmTaskRepairVO.getPlanTime()) ? 
					null : DateTimeUtil.convertDateToLong(DateTimeUtil.timeStampMs2Date(atmTaskRepairBatchVO.getTaskDate(), "yyyy-MM-dd") 
							+ " " + atmTaskRepairVO.getPlanTime() + ":00"));
			atmTask.setRepairContent(atmTaskRepairVO.getContent());
			atmTask.setComments(atmTaskRepairVO.getContent());
			atmTask.setRepairCompany(atmTaskRepairVO.getRepairCompany());
			boolean save = this.save(atmTask);
			
			if(!save){
				throw new BusinessException(-1, "ATM任务添加出错");
			}
			//微信推送通知
			if (isWxPushEnable()) {
				WxTaskNotice taskNotice = new WxTaskNotice();
				taskNotice.setOpType(ADD_TASK);
				taskNotice.setRouteId(atmTask.getCarryRouteId());
				taskNotice.setTaskId(atmTask.getId());
				taskNotice.setTaskDate(DateTimeUtil.timeStampMs2Date(atmTask.getTaskDate(),"yyyy-MM-dd"));
				wxTaskNoticeList.add(taskNotice);
			}
		}
		if (isWxPushEnable()) {
			taskNoticesInQueue(wxTaskNoticeList);
		}
		return Result.success();
		
	}
	
	/**
	 * 
	 * @Title savemultiClean
	 * @Description 批量添加ATM加钞任务
	 * @param atmTaskCleanBatchVO
	 * @param status 
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result savemultiClean(ATMTaskCleanBatchVO atmTaskCleanBatchVO, int status) {
		Map<String,Long> denomMap = commonService.getDenomMap();
		
		//得到设备集合
    	Set<Long> atmIds = atmTaskCleanBatchVO.getAtmTashCleanList().stream().map(ATMTaskCleanVO::getAtmId).collect(Collectors.toSet());
    	List<AtmDevice> atmDevices = atmService.listByIds(atmIds);
    	Map<Long, AtmDevice> atmMap = atmDevices.stream().collect(Collectors.toMap(AtmDevice::getId,atmDevice -> atmDevice));
    	
    	//得到设备网点集合
    	Set<Long> bankIds = atmDevices.stream().map(AtmDevice::getSubBankId).collect(Collectors.toSet());
    	List<Bank> banks = bankService.listByIds(bankIds);
    	Map<Long, Bank> bankMap = banks.stream().collect(Collectors.toMap(Bank::getId,bank -> bank));
    	
    	//获取当天线路集合
    	List<Map<String, Object>> routeList = routeService.listMaps((QueryWrapper)Wrappers.query()
    			.select("id,route_no as routeNo,department_id as departmentId,status_t as statusT").eq("route_date", atmTaskCleanBatchVO.getTaskDate()).eq("deleted", 0));
    	Map<Object, Map> routeMap = routeList.stream().collect(Collectors.toMap(map->map.get("routeNo"),map->map));

    	List<WxTaskNotice> wxTaskNoticeList = new ArrayList<>(); //推送通知
		for (ATMTaskCleanVO atmTaskCleanVO : atmTaskCleanBatchVO.getAtmTashCleanList()) {
			//得到ATM设备信息
			AtmDevice atmDevice = atmMap.get(atmTaskCleanVO.getAtmId());
			Bank bank = bankMap.get(atmDevice.getSubBankId());
			
			
			if(!routeMap.containsKey(bank.getRouteNo())){
				return Result.fail(DateTimeUtil.getDateTimeDisplayString(atmTaskCleanBatchVO.getTaskDate()) + bank.getRouteNo() + "线路不存在,请先创建线路");
			}
		
//			if(routeMap.get(bank.getRouteNo()).get("statusT").equals(RouteStatusEnum.FINISH.getValue())){
//				return Result.fail(DateTimeUtil.getDateTimeDisplayString(atmTaskCleanBatchVO.getTaskDate()) + bank.getRouteNo() + "线路已经交接完成，不能追加任务");
//			}
			
			Map route = routeMap.get(bank.getRouteNo());
			
			AtmTask atmTask = new AtmTask();
			atmTask.setAtmId(atmTaskCleanVO.getAtmId());
			atmTask.setBankId(atmDevice.getBankId());
			atmTask.setSubBankId(atmDevice.getSubBankId());
			atmTask.setTaskDate(atmTaskCleanBatchVO.getTaskDate());
				
			atmTask.setRouteId(Long.parseLong(route.get("id").toString()));
			atmTask.setDepartmentId(Long.parseLong(route.get("departmentId").toString()));
			atmTask.setCarryRouteId(Long.parseLong(route.get("id").toString()));
			atmTask.setCarryType(bank.getCarryType());
			atmTask.setAmount(atmTaskCleanVO.getAmount().movePointRight(5));
			atmTask.setTaskType(AtmTaskTypeEnum.CASHIN.getValue());
			if(atmTaskCleanVO.getAmount().compareTo(BigDecimal.ZERO)  == 0){
				atmTask.setTaskType(AtmTaskTypeEnum.CLEAN.getValue());
			}
			
			atmTask.setDenomId(denomMap.get(atmDevice.getDenom()+""));
			atmTask.setComments(atmTaskCleanVO.getComments());
			atmTask.setStatusT(status);
			boolean save = this.save(atmTask);
			if(!save){
				throw new BusinessException(-1, "ATM任务添加出错");
			}
			if (isWxPushEnable()) {
				WxTaskNotice taskNotice = new WxTaskNotice();
				taskNotice.setOpType(ADD_TASK);
				taskNotice.setRouteId(atmTask.getCarryRouteId());
				taskNotice.setTaskId(atmTask.getId());
				taskNotice.setTaskDate(DateTimeUtil.timeStampMs2Date(atmTask.getTaskDate(),"yyyy-MM-dd"));
				wxTaskNoticeList.add(taskNotice);
			}
		}
		if (isWxPushEnable()) {
			taskNoticesInQueue(wxTaskNoticeList);
			//TODO  入队列
		}
		return Result.success();
		
	}

	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询ATM任务信息
	 * @param page
	 * @param limit
	 * @param atmTask
	 * @return
	 * @return 返回类型 IPage<AtmTask>
	 */
	public IPage<AtmTask> findListByPage(Integer page, Integer limit, AtmTask atmTask) {
		Page<AtmTask> ipage=new Page<AtmTask>(page,limit);
    	QueryWrapper<AtmTask> queryWrapper=new QueryWrapper<AtmTask>();
    	if(atmTask != null){
	    	
    		if(atmTask.getDepartmentId() != null){
	    		queryWrapper.eq("department_id", atmTask.getDepartmentId());
	    	}

	    	if(atmTask.getBankId() != null){
	    		queryWrapper.eq("bank_id", atmTask.getBankId());
	    	}
    		
    		if(atmTask.getImportBatch() != null){
	    		if(atmTask.getImportBatch() == 0){
	    			queryWrapper.eq("import_batch", 0);
	    		}else{
	    			queryWrapper.ne("import_batch", 0);
	    		}
	    	}
    		
	    	if(atmTask.getTaskType() != null){
	    		queryWrapper.eq("task_type", atmTask.getTaskType());
	    	}
	    	
	    	if(atmTask.getStatusT() != null){
	    		queryWrapper.eq("status_t", atmTask.getStatusT());
	    	}
	    	
	    	if(atmTask.getTaskDate() != null){
	    		queryWrapper.eq("task_date", atmTask.getTaskDate());
	    	}
	    	
	    	if(atmTask.getAtmId() != null){
	    		queryWrapper.eq("atm_id", atmTask.getAtmId());
	    	}
    	}
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"task_date");
    	queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * 分页查询ATM任务信息
	 * @param page
	 * @param limit
	 * @param bankId
	 * @param beginDate
	 * @param endDate
	 * @param atmId
	 * @param taskType
	 * @param status
	 * @param errType
	 * @param importBatch
	 * @return
	 */
	public IPage<AtmTask> findListByPage(Integer page,
										 Integer limit,
										 Long bankId,
										 Long beginDate,
										 Long endDate,
										 Long atmId,
										 Integer taskType,
										 Integer status,
										 Integer errType,
										 Integer importBatch
										 ) {
		Page<AtmTask> ipage=new Page<AtmTask>(page,limit);
		QueryWrapper<AtmTask> queryWrapper=new QueryWrapper<AtmTask>();

		queryWrapper.eq("bank_id", bankId);
		if(beginDate != null){
			queryWrapper.ge("task_date", beginDate);
		}
		if(endDate != null){
			queryWrapper.le("task_date", endDate);
		}
		if(atmId != null){
			queryWrapper.eq("atm_id", atmId);
		}
		if(taskType != null){
			queryWrapper.eq("task_type", taskType);
		}
		if(status!= null){
			queryWrapper.eq("status_t", status);
		}
		if (null != errType) {
			if (1 == errType) {
				queryWrapper.gt("stuck_amount", 0.00);
			} else if (2 == errType) {
				queryWrapper.eq("check_result", 2);
			}
		}
		if(importBatch != null){
			if(importBatch == 0){
				queryWrapper.eq("import_batch", 0);
			}else{
				queryWrapper.ne("import_batch", 0);
			}
		}
		queryWrapper.eq("deleted", 0);
//		queryWrapper.orderBy(true,false,"task_date");
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectPage(ipage, queryWrapper);
	}

	public List<AtmTask> findListAll(	 Long bankId,
										 Long beginDate,
										 Long endDate,
										 Long atmId,
										 Integer taskType,
										 Integer status,
										 Integer errType,
										 Integer importBatch) {
		QueryWrapper<AtmTask> queryWrapper=new QueryWrapper<AtmTask>();

		queryWrapper.eq("bank_id", bankId);
		if(beginDate != null){
			queryWrapper.ge("task_date", beginDate);
		}
		if(endDate != null){
			queryWrapper.le("task_date", endDate);
		}
		if(atmId != null){
			queryWrapper.eq("atm_id", atmId);
		}
		if(taskType != null){
			queryWrapper.eq("task_type", taskType);
		}
		if(status!= null){
			queryWrapper.eq("status_t", status);
		}
		if (null != errType) {
			if (1 == errType) {
				queryWrapper.gt("stuck_amount", 0.00);
			} else if (2 == errType) {
				queryWrapper.eq("check_result", 2);
			}
		}
		if(importBatch != null){
			if(importBatch == 0){
				queryWrapper.eq("import_batch", 0);
			}else{
				queryWrapper.ne("import_batch", 0);
			}
		}
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectList(queryWrapper);
	}


	/**
	 * 
	 * @Title findTrimListByPage
	 * @Description 分页查询ATM任务调整记录
	 * @param page
	 * @param limit
	 * @param atmTask
	 * @return
	 * @return 返回类型 IPage<AtmTask>
	 */
	public IPage<AtmTask> findTrimListByPage(Integer page, Integer limit, AtmTask atmTask) {
		Page<AtmTask> ipage=new Page<AtmTask>(page,limit);
    	QueryWrapper<AtmTask> queryWrapper=new QueryWrapper<AtmTask>();
    	queryWrapper.eq("task_date", atmTask.getTaskDate()).eq("department_id", atmTask.getDepartmentId()).eq("bank_id", atmTask.getBankId())
    	.and(wrapper -> wrapper.eq("import_batch", 0).or().eq("status_t",atmTask.getStatusT())).eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"task_date");
    	queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}
	
	
	/**
	 * 
	 * @Title getATMTaskByCondition
	 * @Description 根据条件搜索ATM任务集合
	 * @param atmTask
	 * @return
	 * @return 返回类型 List<AtmTask>
	 */
	public List<AtmTask> getATMTaskByCondition(AtmTask atmTask) {
		QueryWrapper<AtmTask> queryWrapper = new QueryWrapper<>();
		if(atmTask != null){
			
			if(atmTask.getDepartmentId() != null){
				queryWrapper.eq("department_id", atmTask.getDepartmentId());
			}			
			
			if(atmTask.getAtmId() != null){
				queryWrapper.eq("atm_id", atmTask.getAtmId());
			}
			
			if(atmTask.getBankId() != null){
				queryWrapper.eq("bank_id", atmTask.getBankId());
			}
			
			if(atmTask.getRouteId() != null){
				queryWrapper.eq("route_id", atmTask.getRouteId());
			}
			
			if(atmTask.getStatusT() != null){
				queryWrapper.eq("status_t", atmTask.getStatusT());
			}
			
			if(atmTask.getTaskDate() != null){
				queryWrapper.eq("task_date", atmTask.getTaskDate());
			}
			
			if(atmTask.getTaskType() != null){
				queryWrapper.eq("task_type", atmTask.getTaskType());
			}
			
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findList
	 * @Description ATM任务列表
	 * @param routeId
	 * @param bankId 
	 * @param routeType 
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String,Object>> findList(Long routeId, Long bankId, Integer routeType){
		return baseMapper.findList(routeId,bankId,routeType,0);
	}

	/**
	 * 
	 * @Title batchConfirm
	 * @Description 批量确认任务
	 * @param idStr
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result batchConfirm(String idStr) {
		String[] ids = idStr.split(",");
		
		for (String id : ids) {
			AtmTask atmTask = baseMapper.selectById(id);
	    	if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CREATE.getValue())){
				Bank subBank = bankService.getById(atmTask.getSubBankId());
				AtmDevice atmDevice = atmService.getById(atmTask.getAtmId());
				throw new BusinessException(-1, "网点：" + subBank.getShortName() + "的" + atmDevice.getTerNo() 
					+ "设备" + "任务不是新建状态，无法确认");
	    	}
	    	atmTask.setId(Long.parseLong(id));
	    	atmTask.setStatusT(AtmTaskStatusEnum.CONFRIM.getValue());
	    	boolean update = this.updateById(atmTask);
	    	
	    	if(!update)
	    		throw new BusinessException(-1,"批量确认任务失败");
		}
		
		return Result.success();
	}
	
	
	/**
     * 获取银行 ATM加钞任务数据
     * @param taskDate
     * @return
     */
    public List<BankAtmTaskDto> getBankAtmTask(long taskDate, long departmentId){
        //获取清机任务
        List<AtmTask> resultList = taskMapper.getAtmCleanTask(taskDate, departmentId,0L);

        List<Denom> denomList = denomMapper.selectList((QueryWrapper)Wrappers.query().eq("deleted", 0));
        Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        //根据银行分组
        Map<Long,List<AtmTask>> bankCleanMap = resultList.stream().collect(Collectors.groupingBy(AtmTask::getBankId));
        //获取银行ID集合
        Set<Long> bankIdList = bankCleanMap.keySet();
        
    	List<Bank> bankList = bankIdList.size() > 0 ? bankService.listByIds(bankIdList) : new LinkedList<>();
    	Map<Long,String> bankCleanMapTmp = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));

        return bankIdList.stream().map(s -> {
            BankAtmTaskDto bankAtmTaskDto = new BankAtmTaskDto();
            bankAtmTaskDto.setBankId(s);
            String bankName = Optional.ofNullable(bankCleanMapTmp.get(s)).orElse("");
            bankAtmTaskDto.setBankName(bankName);
            List<AtmTask> bankTaskList = Optional.ofNullable(bankCleanMap.get(s)).orElseGet(LinkedList::new);
            //
            List<AtmTask> confirmTaskList = bankTaskList.stream().filter(m -> m.getStatusT() == 1).collect(Collectors.toList());
            List<AtmTask> pendingTaskList = bankTaskList.stream().filter(m -> m.getStatusT() == 0).collect(Collectors.toList());
            List<AtmTask> noOutTaskList = bankTaskList.stream().filter(m -> m.getStatusT() == 1 && m.getIsOut() == 0).collect(Collectors.toList());

            //计算任务数量
            Integer confirmTaskCount = confirmTaskList.size();
            Integer pendingTaskCount = pendingTaskList.size();
            Integer outTaskCount = noOutTaskList.size();
            bankAtmTaskDto.setConfirmTaskCount(confirmTaskCount);
            bankAtmTaskDto.setPendingTaskCount(pendingTaskCount);
            bankAtmTaskDto.setOutTaskCount(outTaskCount);

            List<Long> confirmTaskIdList = confirmTaskList.stream().map(AtmTask::getId).collect(Collectors.toList());
            List<Long> pendingTaskIdList = pendingTaskList.stream().map(AtmTask::getId).collect(Collectors.toList());
            List<Long> noOutTaskIdList = noOutTaskList.stream().map(AtmTask::getId).collect(Collectors.toList());

            List<AtmDenomAmountResult> confirmDenomList = confirmTaskIdList.size() > 0 ?  baseMapper.getDenomAmountList(confirmTaskIdList) : new LinkedList<>();
            List<AtmDenomAmountResult> pendingDenomList = pendingTaskIdList.size() > 0 ? baseMapper.getDenomAmountList(pendingTaskIdList) : new LinkedList<>();
            List<AtmDenomAmountResult> noOutDenomList = noOutTaskIdList.size() > 0 ? baseMapper.getDenomAmountList(noOutTaskIdList) : new LinkedList<>();
            //计算数据
            BigDecimal taskAmount = confirmDenomList.stream().map(AtmDenomAmountResult::getTaskAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            bankAtmTaskDto.setConfirmAmount(taskAmount);
            BigDecimal taskAmount2 = pendingDenomList.stream().map(AtmDenomAmountResult::getTaskAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            bankAtmTaskDto.setPendingAmount(taskAmount2);
			BigDecimal taskAmount3 = noOutDenomList.stream().map(AtmDenomAmountResult::getTaskAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            //任务金+备用金
			bankAtmTaskDto.setOutAmount(taskAmount3);

            LinkedList<BankAtmDenomTaskDto> demoList = new LinkedList<>();
            confirmDenomList.forEach(t-> {
                BankAtmDenomTaskDto denomTaskDto = new BankAtmDenomTaskDto();
                denomTaskDto.setDenomId(t.getDenomId());
                denomTaskDto.setTaskAmount(t.getTaskAmount());
                denomTaskDto.setStatusT(1);
                denomTaskDto.setDenomName(Optional.ofNullable(denomMap.get(t.getDenomId())).orElse(""));
                demoList.add(denomTaskDto);
            });
            pendingDenomList.forEach(t -> {
                BankAtmDenomTaskDto denomTaskDto = new BankAtmDenomTaskDto();
                denomTaskDto.setDenomId(t.getDenomId());
                denomTaskDto.setTaskAmount(t.getTaskAmount());
                denomTaskDto.setStatusT(0);
                denomTaskDto.setDenomName(Optional.ofNullable(denomMap.get(t.getDenomId())).orElse(""));
                demoList.add(denomTaskDto);
            });
			noOutDenomList.forEach(t -> {
				BankAtmDenomTaskDto denomTaskDto = new BankAtmDenomTaskDto();
				denomTaskDto.setDenomId(t.getDenomId());
				denomTaskDto.setTaskAmount(t.getTaskAmount());
				denomTaskDto.setStatusT(2);
				denomTaskDto.setDenomName(Optional.ofNullable(denomMap.get(t.getDenomId())).orElse(""));
				demoList.add(denomTaskDto);
			});

            bankAtmTaskDto.setDenomList(demoList);
            return bankAtmTaskDto;
        }).collect(Collectors.toList());
    }

    /**
     * 
     * @Title CleanExcelImport
     * @Description 银行计划表导入
     * @param bankType
     * @param taskDate 
     * @param excelImportVO
     * @return
     * @return 返回类型 Result
     */
    public Result cleanExcelImport(Integer bankType, Long taskDate, MultipartFile file) {
//    	//判断是否重复导入
//    	AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getOne((QueryWrapper)Wrappers.query().eq("task_date", taskDate)
//    			.eq("bank_type", bankType).eq("import_type", 0).eq("deleted", 0));
//    	if(atmTaskImportRecord != null){
//    		return Result.fail("不允许重复导入");
//    	}
    	
    	
    	//定义各银行有效数据列
    	ExcelImportAtmVO excelImportVO = null;
//    	switch (bankType) {
//			case Constant.BOBID:
//				excelImportVO = new ExcelImportAtmVO(1,0,"北京银行",0,"第","组",7,0,2,3,8);
//				break;
//			case Constant.HZICBCID:
//				excelImportVO = new ExcelImportAtmVO(1,0,"工商银行",0,"##","号线",3,0,2,3,6);
//				break;
//			case Constant.RCBID:
//				excelImportVO = new ExcelImportAtmVO(2,0,"余杭农商行",0,"第","组",7,0,2,3,8);
//				break;
//			case Constant.JDICBCID:
//				excelImportVO = new ExcelImportAtmVO(2,1,"工商银行",0,"第","组",2,0,2,3,5);
//				break;
//			default:
//				return Result.fail("银行类型参数有误");
//		}
    	if(bankType.equals(bob)){
    		excelImportVO = new ExcelImportAtmVO(1,0,0,"第","组",7,0,2,3,8);
    	}else if(bankType.equals(hzicbc)){
    		excelImportVO = new ExcelImportAtmVO(1,0,0,"##","号线",3,0,2,3,6);
    	}else if(bankType.equals(rcb)){
    		excelImportVO = new ExcelImportAtmVO(2,0,0,"第","组",7,0,2,3,8);
    	}else if(bankType.equals(jdicbc)){
    		excelImportVO = new ExcelImportAtmVO(2,1,0,"第","组",2,0,2,3,5);
    	}else{
    		return Result.fail("银行类型参数有误");
    	}
    	
    	
    	//提取银行有效数据列开始=========
    	
    	//Sheet数量
    	int sheetNum = excelImportVO.getSheetNum();
    	//数据Sheet开始位置
    	int sheetStartNum = excelImportVO.getSheetStartNum();
    	//银行名称
//    	String bankName = excelImportVO.getBankName();
    	//线路编号位置
    	int routeNoCell = excelImportVO.getRouteNoCell();
    	//线路编号前字符
    	String routeNoTop = excelImportVO.getRouteNoTop();
    	//线路编号后字符
    	String routeNoBack = excelImportVO.getRouteNoBack();
    	//线路日期位置
//    	int routeDateCell = excelImportVO.getRouteDateCell();
    	//序号位置
    	int serialCell = excelImportVO.getSerialCell();
    	//设备编号位置
    	int terNoCell = excelImportVO.getTerNoCell();
    	//加款金额位置
    	int amountCell = excelImportVO.getAmountCell();
    	//备注位置
    	int commonCell = excelImportVO.getCommonCell();
    	
    	//提取银行有效数据列结束=========
    	
    	//获取atm编号集合
    	List<Map<String, Object>> atmTerNoList = atmService.listMaps((QueryWrapper)Wrappers.query().select("id,bank_id as bankId,ter_no as terNo,denom").eq("deleted", 0));
    	Map<Object, Map> atmTerNoMap = atmTerNoList.stream().collect(Collectors.toMap(map->map.get("terNo"),map->map));
    	
    	//任务集合
    	List<ATMTaskCleanImportBatchVO> taskBatchs = new ArrayList<>();
    	
    	try {
    		String extString = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        	Workbook wb = ExcelImportUtil.readExcel(file.getInputStream(),extString);
        	if(wb == null){
        		return Result.fail("读取excel文件失败");
        	}
    		
        	//循环sheet
	    	for(int x = sheetStartNum; x <= sheetNum-1; x++){
	    		Sheet sheet = wb.getSheetAt(x);
	        	List<Integer> number = new ArrayList<Integer>();
	        	
	        	//获取最大行数
	        	int rownum = sheet.getPhysicalNumberOfRows();
	        	String terNo = "";
	        	
	        	//提取各线路表格开始行数开始=================
	        	for (int i = 0; i < rownum; i++) {
	        		if(sheet.getRow(i) != null){
		        		String str = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(serialCell)).toString();
		        		if(str.replace(" ", "").equals("序号")){
		        			number.add(i);
		        			i+=1;
		        		}
		        		
		        		//提取第一个有效数据设备编号开始============
	        			/*if(number.size() > 0 && StringUtils.isBlank(terNo)){
	        				if(bankType == hzicbc){
		        				String beforehandAmount = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString();
		        				String backupAmount = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell+1)).toString();
		        				String common = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString();
		        				//判断设备和金额是否都有数据
			        			if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString()) && 
			        					(!StringUtils.isBlank(beforehandAmount) || !StringUtils.isBlank(backupAmount) || !StringUtils.isBlank(common))){
			        				sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
			        				terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
			        			}
	        				}else if(bankType == jdicbc){
	        					if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())
		        						&& (!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())
		        						|| !StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString()))){
		        					sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
			        				terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		        				}
	        				}else{
		        				if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())
		        						&& !StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
		        					sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
			        				terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		        				}
	        				}
	        			}*/
	        			//提取第一个有效数据设备编号结束============
	        		}
	        	}
	        	//提取各线路表格开始行数结束=================
	        	
	        	
	        	//判断excel是否是有效excel开始=============
/*	        	if(StringUtils.isBlank(terNo)){
	        		continue;
	        	}
	        	
	        	if(!atmTerNoMap.containsKey(terNo)){
	        		return Result.fail("第" + (number.get(0) + 2) + "行设备【" + terNo + "】不存在");
	        	}*/
	        	
	        	//判断导入模板与所选银行类型是否相同
/*	        	if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
	        		Bank bank = bankService.getById(bankType);
	        		return Result.fail("所选excel不是" + bank.getShortName() + "计划表");
	        	}*/
	        	//判断excel是否是有效excel结束=============
	        	
	        	
	        	//获取当天线路集合
	        	List<Map<String, Object>> routeList = routeService.listMaps((QueryWrapper)Wrappers.query().select("id,route_no as routeNo,status_t as statusT").eq("route_date", taskDate).eq("deleted", 0));
	        	Map<Object, Map> routeMap = routeList.stream().collect(Collectors.toMap(map->map.get("routeNo"),map->map));
	        	
	        	//提取数据开始===================
	        	for (Integer num : number) {
	            	
	        		//任务列表
	        		List<ATMTaskCleanImportVO> atmTashList = new ArrayList<>();
	        		
	        		//获取每条数据开始===================
	        		for (int i = num+1; ; i++) {
	        			
	        			if(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(serialCell)).toString().replace(" ", "").equals("合计")){
	        				break;
	        			}
	        			
	        			if(StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())){
	        				continue;
	        			}
	        			
	        			
	        			//提取北京银行和余杭农商行、建德工行数据
	        			if(!bankType.equals(hzicbc)){
		        			//判断设备和金额是否都有数据
		        			if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
		        				sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
		        				terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		        				if(!atmTerNoMap.containsKey(terNo)){
		        					return Result.fail("第" + ( i +1 ) + "行设备【" + terNo + "】不存在");
		        				}
		        				
		        				if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
		        	        		Bank bank = bankService.getById(bankType);
		        	        		return Result.fail("所选excel不是" + bank.getShortName() + "计划表");
		        	        	}
		        				
		        				String amountStr = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString();
		        				if(!ExcelImportUtil.checkNum(amountStr)){
		        					return Result.fail("第" + ( i +1 ) + "行任务金额数据有误,不是数字或小数");
		        				}
		        				Double amount = Double.valueOf(amountStr);
		        				if(amount != 0 && ((amount >= 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("10")) 
		        						|| (amount < 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("100")))){
		        					return Result.fail("第" + ( i +1 ) + "行数据有误,设备【" + terNo + "】加钞券别为" + atmTerNoMap.get(terNo).get("denom").toString() 
		        							+ ",加钞金额却为" + amountStr + "(十万)");
		        				}
		        				
		        				//清机任务信息
		        				ATMTaskCleanImportVO atmTaskCleanVO = new ATMTaskCleanImportVO();
		                    	atmTaskCleanVO.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
		                    	atmTaskCleanVO.setTerNo(terNo);
		            			atmTaskCleanVO.setAmount(new BigDecimal(amountStr));
		            			atmTaskCleanVO.setComments(bankType == rcb ? "" : ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString());
		            			atmTashList.add(atmTaskCleanVO);
		        			}else{
			        			if(bankType.equals(jdicbc)){
				        			//提取只有备注列数据维修任务
				        			if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString())){
				            			sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
				            			terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
				                    	if(!atmTerNoMap.containsKey(terNo)){
				                    		return Result.fail("第" + ( i +1 ) + "行设备【" + terNo + "】不存在");
				                    	}
				                    	
				        				if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
				        	        		Bank bank = bankService.getById(bankType);
				        	        		return Result.fail("所选excel不是" + bank.getShortName() + "计划表");
				        	        	}
				                    	
				        				//维修任务信息
				                    	ATMTaskCleanImportVO atmTaskCleanVO = new ATMTaskCleanImportVO();
				            			atmTaskCleanVO.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
				            			atmTaskCleanVO.setTerNo(terNo);
				            			atmTaskCleanVO.setComments(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString());
				            			atmTashList.add(atmTaskCleanVO);
				        			}
			        			}
		        			}
		        			
	        			}else{
	        				//提取工商银行数据
	        				String beforehandAmount = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString();
	        				String backupAmount = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell+1)).toString();
	        				
	        				//判断设备和金额是否都有数据
		        			if(!StringUtils.isBlank(beforehandAmount) || !StringUtils.isBlank(backupAmount)){
		            			sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
		            			terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		                    	if(!atmTerNoMap.containsKey(terNo)){
		                    		return Result.fail("第" + ( i +1 ) + "行设备【" + terNo + "】不存在");
		                    	}
		                    	
		        				if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
		        	        		Bank bank = bankService.getById(bankType);
		        	        		return Result.fail("所选excel不是" + bank.getShortName() + "计划表");
		        	        	}
		                    	
		                    	//提取任务金额列任务
		                    	if(!StringUtils.isBlank(beforehandAmount)){
			                    	if(!ExcelImportUtil.checkNum(beforehandAmount)){
			                    		return Result.fail("第" + ( i +1 ) + "行任务金额数据有误,不是数字或小数");
			                    	}
		                    		Double amount = Double.valueOf(beforehandAmount);
			                    	if(amount != 0 && ((amount >= 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("10")) 
			                    			|| (amount < 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("100")))){
			                    		return Result.fail("第" + ( i +1 ) + "行数据有误,设备【" + terNo + "】加钞券别为" + atmTerNoMap.get(terNo).get("denom").toString() 
			                    				+ ",加钞金额却为" + beforehandAmount + "(十万)");
			                    	}
		                    		
		                    		//清机任务信息
			                    	ATMTaskCleanImportVO atmTaskCleanVO = new ATMTaskCleanImportVO();
			            			atmTaskCleanVO.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
			            			atmTaskCleanVO.setTerNo(terNo);
			            			atmTaskCleanVO.setAmount((new BigDecimal(beforehandAmount)));
			            			atmTaskCleanVO.setComments(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString());
			            			atmTashList.add(atmTaskCleanVO);
		                    	}
		                    	
		                    	//提取备用金额列任务
		                    	if(!StringUtils.isBlank(backupAmount)){
			                    	if(!ExcelImportUtil.checkNum(backupAmount)){
			                    		return Result.fail("第" + ( i +1 ) + "行任务金额数据有误,不是数字或小数");
			                    	}
		                    		Double amount = Double.valueOf(backupAmount);
			                    	if(amount != 0 && ((amount >= 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("10")) 
			                    			|| (amount < 0.5 && atmTerNoMap.get(terNo).get("denom").toString().equals("100")))){
			                    		return Result.fail("第" + ( i +1 ) + "行数据有误,设备【" + terNo + "】加钞券别为" + atmTerNoMap.get(terNo).get("denom").toString() 
			                    				+ ",加钞金额却为" + backupAmount + "(十万)");
			                    	}
			                    	
			                    	//清机任务信息
			                    	ATMTaskCleanImportVO atmTaskCleanVO = new ATMTaskCleanImportVO();
			            			atmTaskCleanVO.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
			            			atmTaskCleanVO.setTerNo(terNo);
			            			atmTaskCleanVO.setAmount((new BigDecimal(backupAmount)));
			            			atmTaskCleanVO.setComments(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString());
			            			atmTashList.add(atmTaskCleanVO);
		                    	}
		        			}
		        			
		        			
		        			//提取只有备注列数据维修任务
		        			if(StringUtils.isBlank(beforehandAmount) && StringUtils.isBlank(backupAmount)
		        					&& !StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString())){
		            			sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
		            			terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		                    	if(!atmTerNoMap.containsKey(terNo)){
		                    		return Result.fail("第" + ( i +1 ) + "行设备【" + terNo + "】不存在");
		                    	}
		                    	
		        				if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
		        	        		Bank bank = bankService.getById(bankType);
		        	        		return Result.fail("所选excel不是" + bank.getShortName() + "计划表");
		        	        	}
		        				
		        				//维修任务信息
		                    	ATMTaskCleanImportVO atmTaskCleanVO = new ATMTaskCleanImportVO();
		            			atmTaskCleanVO.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
		            			atmTaskCleanVO.setTerNo(terNo);
		            			atmTaskCleanVO.setComments(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commonCell)).toString());
		            			atmTashList.add(atmTaskCleanVO);
		        			}
	        			}
	
	        		}
	        		//获取每条数据结束===================
	        		
	        		if(atmTashList.size() == 0){
	        			continue;
	        		}
	        		
	            	//判断线路是否存在
	        		String routeNoStr = ExcelImportUtil.getCellFormatValue(sheet.getRow(num-1).getCell(routeNoCell)).toString().replace(" ", "");
	            	String routeNo = routeNoStr.substring(routeNoStr.indexOf(routeNoTop)+1,routeNoStr.indexOf(routeNoBack));

	        		if(!routeMap.containsKey(routeNo)){
	            		return Result.fail(DateTimeUtil.getDateTimeDisplayString(taskDate) + "线路【" + routeNo + "】不存在,请先创建线路");
	        		}
	        		
//	    			if(routeMap.get(routeNo).get("statusT").equals(RouteStatusEnum.FINISH.getValue())){
//	    				return Result.fail(DateTimeUtil.getDateTimeDisplayString(taskDate) + routeNo + "线路已经交接完成，不能追加任务");
//	    			}
	        		
	               	//线路任务信息
	            	ATMTaskCleanImportBatchVO atmTaskBatchVO = new ATMTaskCleanImportBatchVO();
	        		atmTaskBatchVO.setRouteId((Long) routeMap.get(routeNo).get("id"));
	        		atmTaskBatchVO.setRouteNo(routeNo);
	        		atmTaskBatchVO.setTaskDate(taskDate);
	        		atmTaskBatchVO.setAtmTashCleanList(atmTashList);
	        		taskBatchs.add(atmTaskBatchVO);
	            		
	            	
				}
	        	//提取数据结束===================
	    	}
		} catch (Exception e) {
			return Result.fail("文件解析出错，请检查所选文件是否正确");
		}
    	
    	if(taskBatchs.size() == 0){
    		return Result.fail("所选excel中没有有效数据，请检查所选文件是否正确");
    	}
    	
    	return Result.success(taskBatchs);
	}
    

    /**
     * 
     * @Title saveCleanImport
     * @Description 清机任务导入保存
     * @param atmTaskCleanImportSaveVO
     * @return
     * @return 返回类型 Result
     */
	@Transactional(rollbackFor=Exception.class)
	public Result saveImportClean(ATMTaskCleanImportSaveVO atmTaskCleanImportSaveVO) {
//		AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getOne((QueryWrapper)Wrappers.query().eq("task_date", atmTaskCleanImportSaveVO.getTaskDate())
//    			.eq("bank_type", atmTaskCleanImportSaveVO.getBankType()).eq("import_type", 0).eq("deleted", 0));
//    	if(atmTaskImportRecord != null){
//    		return Result.fail("不允许重复导入");
//    	}
		
		AtmTaskImportRecord atmTaskImportRecord = new AtmTaskImportRecord();
		atmTaskImportRecord.setDepartmentId(atmTaskCleanImportSaveVO.getDepartmentId());
		atmTaskImportRecord.setImportType(0);
		atmTaskImportRecord.setTaskDate(atmTaskCleanImportSaveVO.getTaskDate());
		atmTaskImportRecord.setBankType(atmTaskCleanImportSaveVO.getBankType());
		atmTaskImportRecord.setFileName(atmTaskCleanImportSaveVO.getFileName());
		boolean atmTaskImportRecordSave = atmTaskImportRecordService.save(atmTaskImportRecord);
		if(!atmTaskImportRecordSave){
			throw new BusinessException(-1, "添加导入记录出错");
		}
		
    	
    	//获取当天线路集合
    	List<Route> routeList = routeService.list((QueryWrapper)Wrappers.query().eq("route_date", atmTaskCleanImportSaveVO.getTaskDate()).eq("deleted", 0));
    	Map<Long, Route> routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,route->route));
		
		//获取券别信息
		Map<String,Long> denomMap = commonService.getDenomMap();
		List<ATMTaskCleanImportBatchVO> atmTaskCleanBatchVOs = atmTaskCleanImportSaveVO.getAtmTaskCleanImportBatchVOs();
		
		for (ATMTaskCleanImportBatchVO atmTaskCleanBatchVO : atmTaskCleanBatchVOs) {
			Route route = routeMap.get(atmTaskCleanBatchVO.getRouteId());
//			if(route.getStatusT().equals(RouteStatusEnum.FINISH.getValue())){
//				throw new BusinessException(-1, "线路已经交接完成，不能追加任务");
//			}
			
			if(atmTaskCleanBatchVO.getAtmTashCleanList() == null || atmTaskCleanBatchVO.getAtmTashCleanList().size()==0){
				throw new BusinessException(-1, "任务列表为空");
			}
			
			//得到设备集合
	    	Set<Long> atmIds = atmTaskCleanBatchVO.getAtmTashCleanList().stream().map(ATMTaskCleanImportVO::getAtmId).collect(Collectors.toSet());
	    	List<AtmDevice> atmDevices = atmService.listByIds(atmIds);
	    	Map<Long, AtmDevice> atmMap = atmDevices.stream().collect(Collectors.toMap(AtmDevice::getId,atmDevice -> atmDevice));
	    	
	    	//得到设备网点集合
	    	Set<Long> babkIds = atmDevices.stream().map(AtmDevice::getSubBankId).collect(Collectors.toSet());
	    	List<Bank> banks = bankService.listByIds(babkIds);
	    	Map<Long, Bank> bankMap = banks.stream().collect(Collectors.toMap(Bank::getId,bank -> bank));
			
			for (ATMTaskCleanImportVO atmTaskCleanVO : atmTaskCleanBatchVO.getAtmTashCleanList()) {
				//得到ATM设备信息
//				AtmDevice atmDevice = atmService.getById(atmTaskCleanVO.getAtmId());
				AtmDevice atmDevice = atmMap.get(atmTaskCleanVO.getAtmId());

				AtmTask atmTask = new AtmTask();
				atmTask.setAtmId(atmTaskCleanVO.getAtmId());
				atmTask.setBankId(atmDevice.getBankId());
				atmTask.setSubBankId(atmDevice.getSubBankId());
				atmTask.setTaskDate(atmTaskCleanBatchVO.getTaskDate());
				atmTask.setRouteId(atmTaskCleanBatchVO.getRouteId());
				atmTask.setImportBatch(atmTaskImportRecord.getId());
				atmTask.setDepartmentId(route.getDepartmentId());
				atmTask.setCarryRouteId(atmTaskCleanBatchVO.getRouteId());
				if(atmTaskCleanVO.getAmount() != null){
					//得到所属行信息
//					Bank bank = bankService.getById(atmDevice.getBankId());
					Bank bank = bankMap.get(atmDevice.getSubBankId());
					atmTask.setCarryType(bank.getCarryType());
					atmTask.setAmount(atmTaskCleanVO.getAmount().movePointRight(5));
					atmTask.setTaskType(AtmTaskTypeEnum.CASHIN.getValue());
					if(atmTaskCleanVO.getAmount().compareTo(BigDecimal.ZERO)  == 0){
						atmTask.setTaskType(AtmTaskTypeEnum.CLEAN.getValue());
					}
					atmTask.setDenomId(denomMap.get(atmDevice.getDenom()+""));
					atmTask.setBackupFlag(atmTaskCleanVO.getBackupFlag() == null ? 0 : atmTaskCleanVO.getBackupFlag());
					atmTask.setComments(atmTaskCleanVO.getComments());
				}else{
					atmTask.setComments(atmTaskCleanVO.getComments());
					atmTask.setRepairContent(atmTaskCleanVO.getComments());
					atmTask.setTaskType(AtmTaskTypeEnum.REPAIR.getValue());
				}
				

				

				atmTask.setStatusT(AtmTaskStatusEnum.CREATE.getValue());
				boolean save = this.save(atmTask);
				if(!save){
					throw new BusinessException(-1, "ATM任务添加出错");
				}
			}
			
//			route.setTaskTotal(route.getTaskTotal()+atmTaskCleanBatchVO.getAtmTashCleanList().size());
//			boolean routeUpdate = routeService.updateById(route);
//			if(!routeUpdate){
//				throw new BusinessException(-1, "修改线路任务数出错");
//			}
		}
		
		return Result.success();
		
	}

	/**
	 * 查询设备任务数据
	 * @param taskDate   任务日期
	 * @param departmentId  区域ID
	 * @param bankId 银行ID
	 * @param orderId  订单ID
	 * @return
	 */
	public List<AtmRouteTaskDTO> getTaskList(Long taskDate, Long departmentId, Long bankId, Long orderId){
		//ATM任务
		List<BankAtmTaskResult> resultList = baseMapper.getBankTaskList(taskDate,departmentId,bankId);

		Set<Long> taskIds = new HashSet<>();
		Set<Long> cashIds = new HashSet<>();
		//查询订单相关的ATM任务数据
		if (orderId != null && orderId >0){
			QueryWrapper wrapper = Wrappers.query().eq("order_id",orderId);
			List<VaultOrderTask> orderTaskList = orderTaskMapper.selectList(wrapper);
			taskIds = orderTaskList.stream().filter(r -> r.getCategory() == 0).map(VaultOrderTask::getTaskId).collect(Collectors.toSet());
			cashIds = orderTaskList.stream().filter(r -> r.getCategory() != 0).map(VaultOrderTask::getTaskId).collect(Collectors.toSet());
		}

		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(taskDate/1000, 0, ZoneOffset.of("+8"));
		String taskDateStr =  localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


		//计算已确认的备用金
		QueryWrapper cashWrapper = Wrappers.query().eq("bank_id",bankId).eq("task_date",taskDateStr)
				.eq("status_t",1).eq("deleted",0);
		List<AtmAdditionCash> cashList = additionCashMapper.selectList(cashWrapper);
		Set<Long> cashRouteIds = cashList.stream().map(AtmAdditionCash::getRouteId).collect(Collectors.toSet());

		//数据分组
		Set<Long> routeIds = resultList.stream().map(BankAtmTaskResult::getRouteId).collect(Collectors.toSet());
		routeIds.addAll(cashRouteIds);
		Map<Long,String> routeMap = new HashMap<>();
		if (routeIds.size() > 0){
			List<Route> routeList = routeService.getBaseMapper().selectBatchIds(routeIds);
			routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteName));
		}

		Set<Long> finalCashIds = cashIds;
		Map<Long, String> finalRouteMap = routeMap;
        List<AtmRouteTaskDTO> cashDTOList =cashList.stream().map(r -> {
        	    String categoryName = r.getCashType() == 0 ? "备用金" : "其他金额";
				AtmRouteTaskDTO taskDTO = new AtmRouteTaskDTO();
				BeanUtils.copyProperties(r, taskDTO);
				String idStr = "cash-"+r.getId();
				taskDTO.setId(idStr);
				taskDTO.setAmount(r.getAmount());
				taskDTO.setDenomId(r.getDenomId());
				taskDTO.setTerNo(categoryName);
				boolean check = finalCashIds.contains(r.getId());
				taskDTO.setIsSelected(check ? 1: 0);
				taskDTO.setRouteName(Optional.ofNullable(finalRouteMap.get(r.getRouteId())).orElse(""));
				return taskDTO;
		}).collect(Collectors.toList());

		Set<Long> finalTaskIds = taskIds;
		List<AtmRouteTaskDTO> routeTaskDTOList = resultList.stream().map(r -> {
			AtmRouteTaskDTO taskDTO = new AtmRouteTaskDTO();
			BeanUtils.copyProperties(r, taskDTO);
			String idStr = "task-"+r.getId();
			taskDTO.setId(idStr);
			boolean check = finalTaskIds.contains(r.getId());
			taskDTO.setIsSelected(check ? 1: 0);
			taskDTO.setRouteName(Optional.ofNullable(finalRouteMap.get(r.getRouteId())).orElse(""));
			return taskDTO;
		}).collect(Collectors.toList());

		//返回数据
		if (cashDTOList.size() > 0){
			routeTaskDTOList.addAll(cashDTOList);
		}
		return routeTaskDTOList;
	}

	public Result cancelTask(Long taskId) {
		AtmTask atmTask = baseMapper.selectById(taskId);
		if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CONFRIM.getValue())){
			return Result.fail("当前任务不是确认状态，无法撤销");
		}
		atmTask.setId(taskId);
		atmTask.setStatusT(AtmTaskStatusEnum.CANCEL.getValue());
		baseMapper.updateById(atmTask);

		//发送任务通知
		if (isWxPushEnable()) {
			List<WxTaskNotice> taskNotices = new ArrayList<>();
			WxTaskNotice notice = new WxTaskNotice();
			notice.setOpType(CANCEL_TASK);
			notice.setTaskId(taskId);
			notice.setRouteId(atmTask.getCarryRouteId());
			notice.setTaskDate(DateTimeUtil.timeStampMs2Date(atmTask.getTaskDate(),"yyyy-MM-dd"));
			taskNotices.add(notice);
			taskNoticesInQueue(taskNotices);
		}
		return Result.success();
	}

	/**
	 * 
	 * @Title batchCancel
	 * @Description 批量撤销任务
	 * @param batchIdsVO
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result batchCancel(BatchIdsVO batchIdsVO) {
		Set<Long> ids = new HashSet<>(batchIdsVO.getIds());
		List<WxTaskNotice> wxTaskNoticeList = new ArrayList<>(); //推送通知
		for (Long id : ids) {
			AtmTask atmTask = baseMapper.selectById(id);
			if(atmTask == null){
				throw new BusinessException(-1, "任务不存在，请检查数据是否正常");
			}
	    	if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CONFRIM.getValue())){
				Bank subBank = bankService.getById(atmTask.getSubBankId());
				AtmDevice atmDevice = atmService.getById(atmTask.getAtmId());
				
				throw new BusinessException(-1, "网点：" + subBank.getShortName() + "的" + atmDevice.getTerNo() 
					+ "设备" + "任务不是确认状态，无法撤销");
	    	}
	    	atmTask.setId(id);
	    	atmTask.setStatusT(AtmTaskStatusEnum.CANCEL.getValue());
	    	boolean update = this.updateById(atmTask);
	    	if(!update) {
				throw new BusinessException(-1, "批量撤销任务失败");
			}
			if (isWxPushEnable()) {
				WxTaskNotice taskNotice = new WxTaskNotice();
				taskNotice.setOpType(CANCEL_TASK);
				taskNotice.setRouteId(atmTask.getCarryRouteId());
				taskNotice.setTaskId(atmTask.getId());
				taskNotice.setTaskDate(DateTimeUtil.timeStampMs2Date(atmTask.getTaskDate(),"yyyy-MM-dd"));
				wxTaskNoticeList.add(taskNotice);
			}
		}
		if (isWxPushEnable()) {
			taskNoticesInQueue(wxTaskNoticeList);
		}
		
		return Result.success();
	}

	/**
	 * 
	 * @Title move
	 * @Description 任务分配
	 * @param moveVO
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result move(ATMTaskMoveVO moveVO) {
		Set<Long> ids = new HashSet<>(moveVO.getIds());
		for (Long id : ids) {
			AtmTask atmTask = baseMapper.selectById(id);
			if(atmTask == null){
				throw new BusinessException(-1, "任务不存在，请检查数据是否正常");
			}
	    	if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CREATE.getValue())
	    			&&!atmTask.getStatusT().equals(AtmTaskStatusEnum.CONFRIM.getValue())){
				Bank subBank = bankService.getById(atmTask.getSubBankId());
				AtmDevice atmDevice = atmService.getById(atmTask.getAtmId());
				
				throw new BusinessException(-1, "网点：" + subBank.getShortName() + "的" + atmDevice.getTerNo() 
					+ "设备" + "任务不是新建和确认状态，无法分配");
	    	}
	    	atmTask.setId(id);
	    	atmTask.setCarryRouteId(moveVO.getRouteId());
	    	boolean update = this.updateById(atmTask);
	    	
	    	if(!update)
	    		throw new BusinessException(-1,"任务分配失败");
		}
		
		return Result.success();
	}

	//通知入队列
	private void taskNoticesInQueue(List<WxTaskNotice> wxTaskNotices) {
		if (wxTaskNotices != null && wxTaskNotices.size() > 0) {
			final String redisKey = CacheKeysDef.TaskChangeQueue;
			for (WxTaskNotice notice : wxTaskNotices) {
				String data = JSON.toJSONString(notice);
				redisUtil.lLeftPush(redisKey,data);
			}
		}
	}
}
