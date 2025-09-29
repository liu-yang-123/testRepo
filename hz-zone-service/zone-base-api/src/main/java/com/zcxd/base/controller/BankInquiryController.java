package com.zcxd.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.*;
import com.zcxd.base.dto.excel.BankAtmTaskRecordHead;
import com.zcxd.base.dto.excel.BankClearTaskRecordHead;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.service.*;
import com.zcxd.base.vo.ClearTaskVO;
import com.zcxd.base.vo.VaultOrderRecordVO;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.*;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName BankInquiryController
 * @Description 银行业务查询
 * @author 秦江南
 * @Date 2021年10月9日下午3:57:57
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bankInquiry")
@Api(tags = "银行业务查询")
public class BankInquiryController {
	private static int QUERY_GET = 0;
	private static int QUERY_SEND = 1;
	
    private RouteService routeService;
    
    private CommonService commonService;
	
	private EmployeeService employeeService;
	
	private BankService bankService;
	
	private ATMService atmService;
	
    private ATMTaskService atmTaskService;
    
	private RouteEmpChangeService routeEmpChangeService;
	
	private AtmAdditionCashService atmAdditionCashService;
	
    private CashboxPackRecordService cashboxPackRecordService;
    
    private ATMTaskReturnService atmTaskReturnService;
    
    private ATMTaskCheckRecordService atmTaskCheckRecordService;
    
    private ATMTaskRepairRecordService atmTaskRepairRecordService;
    
    private ATMTaskCardService atmTaskCardService;

    private SysUserService userService;
	
	private ATMService atmDeviceService;
	
    private ATMBankCheckRecordService atmBankCheckRecordService;
    
    private AtmClearTaskService clearTaskService;

    private VaultOrderService orderService;
    
    private VaultVolumeService volumeService;

    private DenomService denomService;
    
    private WorkflowRecordService recordService;
    
	@ApiOperation(value = "查询线路列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeName", value = "线路名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/routeList")
    public Result routeList(@RequestParam(required=false) Integer routeType,
                                   @RequestParam(required=false) @Size(max=32,message="routeNo最大长度为32") String routeNo,
                                   @RequestParam(required=false) @Size(max=32,message="routeName最大长度为32") String routeName,
                                   @RequestParam Long routeDate){
		
		List<RouteDTO> routeDTOList = new ArrayList<>();
		Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(routeDTOList);
		}
		
		AtmTask atmTask = new AtmTask();
		atmTask.setBankId(bankId);
		atmTask.setTaskDate(routeDate);
		List<AtmTask> atmTasks = atmTaskService.getATMTaskByCondition(atmTask);
		if(atmTasks.size() <= 0){
			return Result.success(routeDTOList);
		}
		
		Set<Long> routeIds = atmTasks.stream().map(AtmTask::getRouteId).collect(Collectors.toSet());
		
		Route route = new Route();
		route.setRouteNo(routeNo);
		route.setRouteName(routeName);
		route.setRouteType(routeType);
		
		List<Route> routeList = routeService.findBankInquiryList(route , routeIds);

		Set<Long> userIds = routeList.stream().map(t -> {
			HashSet<Long> set = new HashSet();
			set.add(t.getDriver());
			set.add(t.getSecurityA());
			set.add(t.getSecurityB());
			set.add(t.getRouteKeyMan());
			set.add(t.getRouteOperMan());
			set.add(t.getFollower());
			set.add(t.getDispOperMan());
			set.add(t.getDispCheckMan());
			set.add(t.getHdoverOperMan());
			set.add(t.getHdoverCheckMan());
			set.add(t.getCheckUser());
			return set;
		}).flatMap(Collection::stream).filter(r -> r != 0).collect(Collectors.toSet());

		Map<Long,String> employeeMap = new HashMap<>();
		if(userIds.size() != 0){
			List<Employee> employeeList = employeeService.listByIds(userIds);
			employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
		}

		Map<String, Object> vehicleMap = commonService.getVehicleMap();
		Map<Long, String> finalEmployeeMap = employeeMap;
		routeDTOList = routeList.stream().map(routeTmp -> {
			RouteDTO routeDTO = new RouteDTO();
			BeanUtils.copyProperties(routeTmp, routeDTO);
			Map a = (Map)vehicleMap.get(routeTmp.getVehicleId() + "");
			routeDTO.setSeqno(a == null ? "" : a.get("seqno").toString());
			routeDTO.setLpno(a == null ? "" : a.get("lpno").toString());
			routeDTO.setDriverName(finalEmployeeMap.get(routeTmp.getDriver()));
			routeDTO.setSecurityAName(routeTmp.getSecurityA() == 0 ? "" : finalEmployeeMap.get(routeTmp.getSecurityA()));
			routeDTO.setSecurityBName(routeTmp.getSecurityB() == 0 ? "" : finalEmployeeMap.get(routeTmp.getSecurityB()));
			routeDTO.setRouteKeyManName(routeTmp.getRouteKeyMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getRouteKeyMan()));
			routeDTO.setRouteOperManName(routeTmp.getRouteOperMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getRouteOperMan()));
			routeDTO.setFollowerName(routeTmp.getFollower() == 0 ? "" : finalEmployeeMap.get(finalEmployeeMap.get(routeTmp.getFollower())));
			routeDTO.setDispOperManName(routeTmp.getDispOperMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getDispOperMan()));
			routeDTO.setDispCheckManName(routeTmp.getDispCheckMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getDispCheckMan()));
			routeDTO.setHdoverOperManName(routeTmp.getHdoverOperMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getHdoverOperMan()));
			routeDTO.setHdoverCheckManName(routeTmp.getHdoverCheckMan() == 0 ? "" : finalEmployeeMap.get(routeTmp.getHdoverCheckMan()));
			routeDTO.setCheckUserName(routeTmp.getCheckUser() == 0 ? "" : finalEmployeeMap.get(routeTmp.getCheckUser()));
			return routeDTO;
		}).collect(Collectors.toList());

		return Result.success(routeDTOList);
    }
	
	
    @ApiOperation(value = "查询人员调整记录")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/routeEmpChange/{id}")
    public Result empChange(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<RouteEmpChange> empChanges = routeEmpChangeService.list((QueryWrapper)Wrappers.query().eq("route_id", id).orderByAsc("create_time"));
    	Set<Long> userIds = new HashSet<>();
    	empChanges.stream().forEach(empChange -> {
    		userIds.add(empChange.getOldManId());
    		userIds.add(empChange.getNewManId());
    	});
    	
		Map<Long,String> employeeMap = new HashMap<>();
		if(userIds.size() != 0){
			List<Employee> employeeList = employeeService.listByIds(userIds);
			employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
		}
		
		Map<Long, String> finalEmployeeMap = employeeMap;
		List<RouteEmpChangeDTO> empChangeDTOs = empChanges.stream().map(empChang -> {
			RouteEmpChangeDTO empChangeDTO = new RouteEmpChangeDTO();
			BeanUtils.copyProperties(empChang, empChangeDTO);
			empChangeDTO.setOldManName(finalEmployeeMap.get(empChang.getOldManId()));
			empChangeDTO.setNewManName(finalEmployeeMap.get(empChang.getNewManId()));
			return empChangeDTO;
		}).collect(Collectors.toList());
    	
    	return Result.success(empChangeDTOs);
    }
    
    @ApiOperation(value = "线路监控")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeName", value = "线路名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/routeMonitor")
    public Result routeMonitor(@RequestParam(required=false) Integer routeType,
                                   @RequestParam(required=false) @Size(max=32,message="routeNo最大长度为32") String routeNo,
                                   @RequestParam(required=false) @Size(max=32,message="routeName最大长度为32") String routeName,
                                   @RequestParam Long routeDate){
    	
    	List<RouteMonitorDTO> routeMonitorList = new ArrayList<>();
		Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(routeMonitorList);
		}
		
		AtmTask atmTask = new AtmTask();
		atmTask.setBankId(bankId);
		atmTask.setTaskDate(routeDate);
		List<AtmTask> atmTasks = atmTaskService.getATMTaskByCondition(atmTask);
		if(atmTasks.size() <= 0){
			return Result.success(routeMonitorList);
		}
		
		Set<Long> routeIds = atmTasks.stream().map(AtmTask::getRouteId).collect(Collectors.toSet());
		
		Route route = new Route();
		route.setRouteNo(routeNo);
		route.setRouteName(routeName);
		route.setRouteType(routeType);
		
		List<Route> routeList = routeService.findBankInquiryList(route , routeIds);
    	
    	Map<String, Object> vehicleMap = commonService.getVehicleMap();
    	
    	List<Map<String,Object>> atmTaskList = atmTaskService.listMaps((QueryWrapper)Wrappers.query().select("id,carry_route_id as carryRouteId,status_t as status")
    			.eq("task_date", routeDate).eq("bank_id",bankId).ne("status_t", AtmTaskStatusEnum.CANCEL.getValue()).eq("deleted", 0));
    	
    	Map<String,Integer> taskCountMap = new HashMap<>();
    	atmTaskList.stream().forEach(atmTaskTmp -> {
    		if(!taskCountMap.containsKey(atmTaskTmp.get("carryRouteId") + "A")){
    			taskCountMap.put(atmTaskTmp.get("carryRouteId") + "A",0);
    		}
    		
    		if(!taskCountMap.containsKey(atmTaskTmp.get("carryRouteId") + "B")){
    			taskCountMap.put(atmTaskTmp.get("carryRouteId") + "B",0);
    		}
    		
    		taskCountMap.put(atmTaskTmp.get("carryRouteId") + "A",taskCountMap.get(atmTaskTmp.get("carryRouteId") + "A")+ 1);
    		
    		if(atmTaskTmp.get("status").toString().equals(AtmTaskStatusEnum.FINISH.getValue()+"")){
    			taskCountMap.put(atmTaskTmp.get("carryRouteId") + "B", taskCountMap.get(atmTaskTmp.get("carryRouteId") + "B")+1);
    		}
    	});
    	
    	routeMonitorList = routeList.stream().map(routeTmp -> {
    		RouteMonitorDTO routeMonitorDTO = new RouteMonitorDTO();
    		BeanUtils.copyProperties(routeTmp, routeMonitorDTO);
    		Map a = (Map)vehicleMap.get(routeTmp.getVehicleId() + "");
    		routeMonitorDTO.setSeqno(a == null ? "" : a.get("seqno").toString());
    		routeMonitorDTO.setLpno(a == null ? "" : a.get("lpno").toString());
    		routeMonitorDTO.setTaskTotal(taskCountMap.get(routeMonitorDTO.getId()+"A") == null ? 0 : taskCountMap.get(routeMonitorDTO.getId()+"A"));
    		routeMonitorDTO.setTaskFinish(taskCountMap.get(routeMonitorDTO.getId()+"B") == null ? 0 : taskCountMap.get(routeMonitorDTO.getId()+"B"));
    		return routeMonitorDTO;
    	}).collect(Collectors.toList());
    	
        return Result.success(routeMonitorList);
    }
    
    
    @ApiOperation(value = "清机任务")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/atmTask/{id}")
    public Result atmTask(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<Map<String, Object>> findList = new ArrayList<>();
		Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(findList);
		}
    	
    	findList = atmTaskService.findList(id,bankId,1);
    	if(findList != null && findList.size() > 0){
    		Bank bank = bankService.getById(bankId);
    		
	    	findList.stream().forEach(map->{
	    		map.put("amount", new BigDecimal(map.get("amount").toString()).movePointLeft(5));
	    		map.put("headBank", bank.getShortName());
	    	});
    	}
        return Result.success(findList);
    }
    
    
    @ApiOperation(value = "加钞网点")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/bankClearTree")
    public Result bankClearTree(){
    	
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(new ArrayList<Map<String, Object>>());
		}
    	Bank bank = new Bank();
    	bank.setBankType(0);
    	bank.setParentIds("/" + bankId + "/");
    	List<Map<String, Object>> bankList = bankService.getBankList(bank);
    	
    	//增加顶级银行本身
    	Bank bankTmp = bankService.getById(bankId);
    	Map<String, Object> map = new HashMap<>();
    	map.put("id", bankTmp.getId());
    	map.put("name", bankTmp.getShortName());
    	map.put("pid", 0);
    	bankList.add(map);
    	
    	return Result.success(TreeUtil.listToTree(bankList));
    }
    
    @ApiOperation(value = "查询加钞网点详情")
    @ApiImplicitParam(name = "id", value = "网点id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/bankClearInfo/{id}")
    public Result bankClearInfo(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Bank bank = bankService.getById(id);
    	
    	BankInfoDTO bankInfoDTO = new BankInfoDTO();
    	BeanUtils.copyProperties(bank, bankInfoDTO);
    	
    	String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
		if (parentsIds.length > 1) {
			Bank parent = bankService.getById(parentsIds[parentsIds.length - 1]);
			bankInfoDTO.setParentName(parent.getShortName());
		}
    	
    	return Result.success(bankInfoDTO);
    }
    
    
    @ApiOperation(value = "银行查询加钞网点列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/bankClearOption")
    public Result bankClearOption(){
    	
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(new ArrayList<Map<String, Object>>());
		}
    	Bank bank = new Bank();
    	bank.setBankType(0);
    	bank.setParentIds("/" + bankId + "/");
    	List<Map<String, Object>> bankList = bankService.getBankList(bank);
    	
    	//增加顶级银行本身
    	Bank bankTmp = bankService.getById(bankId);
    	Map<String, Object> map = new HashMap<>();
    	map.put("id", bankTmp.getId());
    	map.put("name", bankTmp.getShortName());
    	map.put("pid", 0);
    	bankList.add(map);
    	
    	return Result.success(TreeUtil.listToTree(bankList));
    }
    
    @ApiOperation(value = "查询ATM设备列表")
    @ApiImplicitParams({
    	  @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "bankId", value = "网点Id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/atmList")
    public Result atmList(@RequestParam Integer page,
                                   @RequestParam Integer limit,
                                   @RequestParam Long bankId,
                                   @RequestParam(required=false) String terNo,
                                   @RequestParam(required=false) String routeNo){
    	AtmDevice atmDevice = new AtmDevice();
    	atmDevice.setBankId(bankId);
    	atmDevice.setTerNo(terNo);
    	IPage<AtmDevice> atmPage = atmService.findListByPage(page, limit, atmDevice , routeNo);
    	
    	List<ATMDTO> atmList = new ArrayList<ATMDTO>();
    	if(atmPage.getTotal() > 0){
  	  	Set<Long> bankIds = new HashSet<>();
  	  	atmPage.getRecords().stream().forEach(atmDeviceTmp-> {
  	  		bankIds.add(atmDeviceTmp.getSubBankId());
  	  		bankIds.add(atmDeviceTmp.getGulpBankId());
  	  	});
      	List<Bank> bankList = bankService.listByIds(bankIds);
      	Map<Long,Bank> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId, Bank -> Bank));
  	  	
  	  	atmList = atmPage.getRecords().stream().map(atmTmp -> {
  	  		ATMDTO atmDTO = new ATMDTO();
  	  		BeanUtils.copyProperties(atmTmp, atmDTO);
  	  		atmDTO.setBankId(atmTmp.getSubBankId());
  	  		atmDTO.setBankNo(bankCleanMap.get(atmTmp.getSubBankId()).getBankNo());
  	  		atmDTO.setBankName(bankCleanMap.get(atmTmp.getSubBankId()).getShortName());
  	  		atmDTO.setGulpBankName(bankCleanMap.get(atmTmp.getGulpBankId()).getShortName());
  	  		return atmDTO;
  	  	}).collect(Collectors.toList());
    	}
    	
    	ResultList resultList = new ResultList.Builder().total(atmPage.getTotal()).list(atmList).build();
        return Result.success(resultList);
    }
    
    @ApiOperation(value = "银行查询任务线路列表")
    @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/routeOption")
    public Result routeOption(@RequestParam Long routeDate) {
    	List<Map<String,Object>> routeOption = new ArrayList<>();
		Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(routeOption);
		}
		
		AtmTask atmTask = new AtmTask();
		atmTask.setBankId(bankId);
		atmTask.setTaskDate(routeDate);
		List<AtmTask> atmTasks = atmTaskService.getATMTaskByCondition(atmTask);
		if(atmTasks.size() <= 0){
			return Result.success(routeOption);
		}
		
		Set<Long> routeIds = atmTasks.stream().map(AtmTask::getRouteId).collect(Collectors.toSet());
		
		List<Route> routeList = routeService.findBankInquiryList(new Route() , routeIds);
		
		routeOption = routeList.stream().map(route -> {
			Map<String,Object> map = new HashMap<>();
			map.put("routeNo", route.getRouteNo());
			map.put("value", route.getId());
			map.put("routeName", route.getRouteName());
			return map;
		}).collect(Collectors.toList());
		
		return Result.success(routeOption);
    }
    
    @ApiOperation(value = "查询ATM任务列表")
    @ApiImplicitParam(name = "id", value = "线路Id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/atmTaskList/{id}")
    public Result atmTaskListByRoute(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Map<String,List<Map<String, Object>>> resultData = new HashMap(); 
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(resultData);
		}
    	List<Map<String, Object>> findList = atmTaskService.findList(id,bankId,0);
    	List<Map<String, Object>> additionCashMap = new ArrayList<>();

    	if(findList != null && findList.size()>0){
    		Set<Long> bankIds = new HashSet<>();
    		if(bankId != null){
    			bankIds.add(bankId);
    		}else{
    			bankIds = findList.stream().map(map-> Long.parseLong(map.get("bankId").toString())).collect(Collectors.toSet());
    		}
    		
	    	List<Bank> bankList = bankService.listByIds(bankIds);
	    	Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
	    	
	    	Set<Long> atmIds = findList.stream().map(map-> Long.parseLong(map.get("atmId").toString())).collect(Collectors.toSet());
	    	List<AtmDevice> atmList = atmService.listByIds(atmIds);
	    	Map<Long, Integer> atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getDenom));
	    	
	    	findList.stream().forEach(map->{
	    		map.put("amount", new BigDecimal(map.get("amount").toString()).movePointLeft(5));
	    		map.put("headBank", bankCleanMap.get(map.get("bankId")));
	    		map.put("denom", atmMap.get(map.get("atmId")));
	    	});
	    	
	    	if(bankIds.size() == 1){
		    	QueryWrapper query = new QueryWrapper();
		    	query.select("bank_id as bankId,denom_value as denomValue,sum(amount) as amount").eq("route_id", id);
		    	query.eq("status_t", AtmAdditionCashStatusEnum.OUTSTORE.getValue());
		    	query.eq("bank_id", Long.parseLong(findList.get(0).get("bankId").toString()));
		    	query.eq("deleted", 0);
		    	query.groupBy("bankId");
		    	query.groupBy("denomValue");
		    	additionCashMap = atmAdditionCashService.listMaps(query);
	    	}
    	}
    	
    	resultData.put("taskData", findList);
    	resultData.put("additionCash", additionCashMap);
        return Result.success(resultData);
    }


	@ApiOperation(value = "ATM任务记录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
			@ApiImplicitParam(name = "beginDate", value = "开始日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "taskType", value = "任务类型", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "status", value = "任务状态", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "errType", value = "异常类型", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "importBatch", value = "下发类型", required = false, dataType = "Integer")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/atmTask/list")
	public Result findListByPage(@RequestParam(defaultValue = "1")  Integer page,
								 @RequestParam(defaultValue = "10")  Integer limit,
								 @RequestParam(required=false) Long beginDate,
								 @RequestParam(required=false) Long endDate,
								 @RequestParam(required=false) String terNo,
								 @RequestParam(required=false) Integer taskType,
								 @RequestParam(required=false) Integer status,
								 @RequestParam(required=false) Integer errType,
								 @RequestParam(required=false) Integer importBatch){

		Long bankId = UserContextHolder.getBankId();

		Map<String, Object> resultMap = new HashMap();
		ResultList resultData = null;
		IPage<AtmTask> atmTaskPage = null;
		Long atmId = null;
		if (!StringUtils.isEmpty(terNo)) {
			AtmDevice atmDevice = atmDeviceService.getByTerNo(terNo);
			if (null != atmDevice) {
				atmId = atmDevice.getId();
			}
		}
		Long dtBegin = null;
		if (beginDate != null) {
			dtBegin = DateTimeUtil.getDailyStartTimeMs(beginDate);
		}
		Long dtEnd = null;
		if (endDate != null) {
			dtEnd = DateTimeUtil.getDailyEndTimeMs(endDate);
		}
		atmTaskPage = atmTaskService.findListByPage(page, limit,bankId,dtBegin,dtEnd,atmId,taskType,status,errType,importBatch);

		List<AtmTaskRecordDTO> resultList = new ArrayList<>();
		if(atmTaskPage.getRecords().size()>0){
			Set<Long> atmIds = atmTaskPage.getRecords().stream().map(AtmTask::getAtmId).collect(Collectors.toSet());
			Set<Long> routeIds = atmTaskPage.getRecords().stream().map(AtmTask::getRouteId).collect(Collectors.toSet());
			Set<Long> bankIds = new HashSet<>();
			atmTaskPage.getRecords().stream().forEach(atmTaskTmp->{
				bankIds.add(atmTaskTmp.getBankId());
				bankIds.add(atmTaskTmp.getSubBankId());
			});

			List<AtmDevice> atmList = atmService.listByIds(atmIds);
			Map<Long,String> atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));

			List<Route> routeList = routeService.listByIds(routeIds);
			Map<Long,String> routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteNo));

			List<Bank> bankList = bankService.listByIds(bankIds);
			Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));

			resultList = atmTaskPage.getRecords().stream().map(atmTaskTmp ->{
				AtmTaskRecordDTO atmTaskRecordDTO = new AtmTaskRecordDTO();
				BeanUtils.copyProperties(atmTaskTmp, atmTaskRecordDTO);
				atmTaskRecordDTO.setTerNo(atmMap.get(atmTaskTmp.getAtmId()));
				atmTaskRecordDTO.setAmount(atmTaskTmp.getAmount().movePointLeft(5));
				atmTaskRecordDTO.setRouteNo(routeMap.get(atmTaskTmp.getRouteId()));
				atmTaskRecordDTO.setHeadBank(bankMap.get(atmTaskTmp.getBankId()));
				atmTaskRecordDTO.setBankName(bankMap.get(atmTaskTmp.getSubBankId()));
				return atmTaskRecordDTO;
			}).collect(Collectors.toList());
		}
		resultData = new ResultList.Builder().total(atmTaskPage.getTotal()).list(resultList).build();
		return Result.success(resultData);
	}

	@ApiOperation(value = "导出ATM任务记录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "beginDate", value = "开始日期", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "taskType", value = "任务类型", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "status", value = "任务状态", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "errType", value = "异常类型", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "importBatch", value = "下发类型", required = false, dataType = "Integer")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/atmTask/export")
	public void exportAtmTask(HttpServletResponse response,
							  @RequestParam Long beginDate,
							  @RequestParam Long endDate,
							  @RequestParam(required=false) String terNo,
							  @RequestParam(required=false) Integer taskType,
							  @RequestParam(required=false) Integer status,
							  @RequestParam(required=false) Integer errType,
							  @RequestParam(required=false) Integer importBatch){

//		List<AtmTask> atmTasks = new ArrayList<>();
		if (beginDate == 0L || endDate == 0L) {
			throw new BusinessException(-1,"无效日期");
		}
		Long bankId = UserContextHolder.getBankId();
		Long atmId = null;
		if (!StringUtils.isEmpty(terNo)) {
			AtmDevice atmDevice = atmDeviceService.getByTerNo(terNo);
			if (null != atmDevice) {
				atmId = atmDevice.getId();
			}
		}
		Long dtBegin = DateTimeUtil.getDailyStartTimeMs(beginDate);
		Long dtEnd = DateTimeUtil.getDailyEndTimeMs(endDate);

		//获取任务集合
		List<AtmTask> taskList = atmTaskService.findListAll(bankId,dtBegin,dtEnd,atmId,taskType,status,errType,importBatch);
		if(taskList.size() == 0){
			throw new BusinessException(-1,"没有记录需要导出");
		}

		Set<Long> atmIds = new HashSet<Long>();
		Set<Long> bankIds = new HashSet<Long>();
		Set<Long> routeIds = new HashSet<Long>();
		taskList.stream().forEach(atmTaskTmp -> {
			atmIds.add(atmTaskTmp.getAtmId());
			bankIds.add(atmTaskTmp.getSubBankId());
			routeIds.add(atmTaskTmp.getRouteId());
		});
		bankIds.add(bankId);

		//得到设备集合
		List<AtmDevice> atmDevices = atmService.listByIds(atmIds);
		Map<Long, String> atmMap = atmDevices.stream().collect(Collectors.toMap(AtmDevice::getId,atmDevice -> atmDevice.getTerNo()));

		//得到设备网点集合
		List<Bank> banks = bankService.listByIds(bankIds);
		Map<Long, String> bankMap = banks.stream().collect(Collectors.toMap(Bank::getId,bank -> bank.getShortName()));

		//得到设备网点集合
		List<Route> routes = routeService.listByIds(routeIds);
		Map<Long, String> routeMap = routes.stream().collect(Collectors.toMap(Route::getId,route -> route.getRouteNo()));

		List<BankAtmTaskRecordHead> taskRecords = new ArrayList<BankAtmTaskRecordHead>();
		for (int i = 0; i < taskList.size(); i++) {
			AtmTask task = taskList.get(i);
			BankAtmTaskRecordHead recordHead = new BankAtmTaskRecordHead();
			recordHead.setIndex(i+1);
			recordHead.setTaskDate(DateTimeUtil.timeStampMs2Date(task.getTaskDate(),"yyyy-MM-dd"));
			recordHead.setRouteNo(routeMap.get(task.getRouteId()));
			recordHead.setBankName(bankMap.get(task.getSubBankId()));
			recordHead.setTerNo(atmMap.get(task.getAtmId()));
			recordHead.setTaskType(AtmTaskTypeEnum.getText(task.getTaskType()));
			recordHead.setAmount(task.getAmount());
			recordHead.setStuckAmount(task.getStuckAmount());
//			recordHead.setBeginTime();
			recordHead.setComments(task.getComments());
			recordHead.setBeginTime(task.getBeginTime() == 0 ? "" : DateTimeUtil.timeStampMs2Date(task.getBeginTime(),"HH:mm:ss"));
			recordHead.setEndTime(task.getEndTime() == 0 ? "" : DateTimeUtil.timeStampMs2Date(task.getEndTime(),"HH:mm:ss"));
			taskRecords.add(recordHead);
		}

		taskRecords.sort((x, y) -> Integer.compare(Integer.parseInt(x.getRouteNo()), Integer.parseInt(y.getRouteNo())));
		taskRecords.forEach((entity) -> entity.setRouteNo(entity.getRouteNo() + "号线"));
		String title = bankMap.get(bankId) + "任务记录";
		try {
			BankAtmTaskRecordHead.setExcelPropertyTitle(title);
			EasyExcelUtil.exportPrintExcel(response,title,BankAtmTaskRecordHead.class,taskRecords,null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(-1,"导出任务记录出错");
		}
	}


	@ApiOperation(value = "查询ATM任务详情")
    @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/atmTaskInfo/{id}")
    public Result atmTaskInfo(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Map<String,Object> resultMap = new HashMap<String, Object>();
    	
    	AtmTask atmTask = atmTaskService.getById(id);
    	if(atmTask.getTaskType().equals(AtmTaskTypeEnum.REPAIR.getValue())){
			AtmTaskRepairDTO atmTaskRepairDTO = new AtmTaskRepairDTO();
			atmTaskRepairDTO.setPlanTime(atmTask.getRepairPlanTime());
			atmTaskRepairDTO.setContent(atmTask.getRepairContent());
			atmTaskRepairDTO.setRepairCompany(atmTask.getRepairCompany());
			atmTaskRepairDTO.setAtmRunStatus(atmTask.getAtmRunStatus());
			atmTaskRepairDTO.setStuckAmount(atmTask.getStuckAmount());
			AtmTaskRepairRecord atmTaskRepairRecord = atmTaskRepairRecordService.getOne((QueryWrapper)Wrappers.query().eq("atm_task_id", atmTask.getId()));
			if(atmTaskRepairRecord != null){
				BeanUtils.copyProperties(atmTaskRepairRecord, atmTaskRepairDTO);
			}
			resultMap.put("atmTaskRepair", atmTaskRepairDTO);
    	}
    	
    	if(atmTask.getTaskType().equals(AtmTaskTypeEnum.CASHIN.getValue()) 
    			|| atmTask.getTaskType().equals(AtmTaskTypeEnum.CLEAN.getValue())){
			AtmTaskCleanDTO atmTaskCleanDTO = new AtmTaskCleanDTO();
			atmTaskCleanDTO.setAmount(atmTask.getAmount().movePointLeft(5));
			if(StringUtils.isBlank(atmTask.getCashboxList())){
				atmTaskCleanDTO.setCashboxMap(new ArrayList<>());
			}else{
				String[] cashBoxIds = atmTask.getCashboxList().split(",");
				List<CashboxPackRecord> cashboxList = cashboxPackRecordService.listByIds(Arrays.asList(cashBoxIds));
				List<Map> cashboxNoMap = cashboxList.stream().map(cashbox->{
					Map map = new HashMap();
					map.put("id", cashbox.getId());
					map.put("boxNo", cashbox.getBoxNo());
					return map;
				}).collect(Collectors.toList());;
//				List<String> cashboxNoList = cashboxList.stream().map(CashboxPackRecord::getBoxNo).collect(Collectors.toList());
				atmTaskCleanDTO.setCashboxMap(cashboxNoMap);
			}
			
			atmTaskCleanDTO.setBarboxList(atmTaskReturnService.getBarBoxCodes(id));
			atmTaskCleanDTO.setClearSite(atmTask.getClearSite());
			atmTaskCleanDTO.setComments(atmTask.getComments());
			
			atmTaskCleanDTO.setAtmRunStatus(atmTask.getAtmRunStatus());
			atmTaskCleanDTO.setStuckAmount(atmTask.getStuckAmount());
			
			Set<Long> userIds = new HashSet<>();
			if(atmTask.getCleanOpManId() != 0){
				userIds.add(atmTask.getCleanOpManId());
			}
			if(atmTask.getCleanKeyManId() != 0){
				userIds.add(atmTask.getCleanKeyManId());
			}
	    	List<Employee> employeeList = new ArrayList<>();
	    	if(userIds.size() != 0){
	    		employeeList = employeeService.listByIds(userIds);
	    	}
	    	Map<Long,String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
	    	atmTaskCleanDTO.setCleanOpManName(employeeMap.get(atmTask.getCleanOpManId()));
	    	atmTaskCleanDTO.setCleanKeyManName(employeeMap.get(atmTask.getCleanKeyManId()));
			
			resultMap.put("atmTaskClean", atmTaskCleanDTO);
    	}
    	
//   	 AtmTaskCheckRecord atmTaskCheckRecord = atmTaskCheckRecordService.getOne((QueryWrapper)Wrappers.query().eq("atm_task_id", id));
//	     if(atmTaskCheckRecord != null){
//			 AtmTaskCheckDTO atmTaskCheckDTO = new AtmTaskCheckDTO();
//			 atmTaskCheckDTO.setCheckItemResult(StringUtils.isBlank(atmTaskCheckRecord.getCheckItemResult())? new CheckItemResultDTO() : JSONObject.parseObject(atmTaskCheckRecord.getCheckItemResult() , CheckItemResultDTO.class));
//			 atmTaskCheckDTO.setComments(atmTaskCheckRecord.getComments());
//			 resultMap.put("atmTaskCheck", atmTaskCheckDTO);
//	     }
   	
    	if(!StringUtils.isBlank(atmTask.getCheckItemResult())){
    		AtmTaskCheckDTO atmTaskCheckDTO = new AtmTaskCheckDTO();
			 atmTaskCheckDTO.setCheckItemResult(JSONObject.parseObject(atmTask.getCheckItemResult() , CheckItemResultDTO.class));
			 resultMap.put("atmTaskCheck", atmTaskCheckDTO);
    	}
    	
        return Result.success(resultMap);
    }

    @ApiOperation(value = "查询吞没卡列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
			@ApiImplicitParam(name = "routeNo", value = "送卡线路", required = false, dataType = "String"),
			@ApiImplicitParam(name = "cardNo", value = "银行卡号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "queryDay", value = "查询日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "statusT", value = "状态", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "queryType", value = "查询类型(0 - 回笼查询，1 - 派送查询)", required = false, dataType = "Integer")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/taskCardList")
    public Result taskCardList(@RequestParam Integer page,
	                                 @RequestParam Integer limit,
	                                 @RequestParam(required=false) String routeNo,
	                                 @RequestParam(required=false) String cardNo,
								 	@RequestParam(required=false) Long queryDay,
	                                 @RequestParam(required=false) Integer statusT,
	                                 @RequestParam(required=true) Integer queryType){
    	
    	ResultList resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(resultList);
		}

    	AtmTaskCard atmTaskCard = new AtmTaskCard();
    	if (queryType == QUERY_GET) { //查询回笼
			atmTaskCard.setRouteNo(routeNo);
			atmTaskCard.setCreateTime(queryDay);
		} else {  //查询配送
			atmTaskCard.setDeliverRouteNo(routeNo);
			if (null != queryDay) {
				atmTaskCard.setDeliverDay(DateTimeUtil.timeStampMs2Date(queryDay, "yyyy-MM-dd"));
			}
		}
    	atmTaskCard.setBankId(bankId);
    	atmTaskCard.setStatusT(statusT);
    	atmTaskCard.setCardNo(cardNo);

    	IPage<AtmTaskCard> atmTaskCardPage = atmTaskCardService.findListByPage(page, limit, atmTaskCard);
    	List<AtmTaskCardDTO> atmTaskCardList = new ArrayList<AtmTaskCardDTO>();
    	
    	if(atmTaskCardPage.getTotal()>0){
    		//数据涉及用户id
	    	Set<Long> userIds = new HashSet<>();
	    	//数据涉及atmId
	    	Set<Long> atmIds = new HashSet<>();
	    	//数据设计bankId
	    	Set<Long> bankIds = new HashSet<>();
	    	
	    	//提取Id
			atmTaskCardPage.getRecords().stream().forEach(atmTaskCardTmp->{
				atmIds.add(atmTaskCardTmp.getAtmId());
				bankIds.add(atmTaskCardTmp.getDeliverBankId());
				bankIds.add(atmTaskCardTmp.getBankId());

				if(atmTaskCardTmp.getCollectManA() != 0){
					userIds.add(atmTaskCardTmp.getCollectManA());
				}
				if(atmTaskCardTmp.getDispatchManA() != 0){
					userIds.add(atmTaskCardTmp.getDispatchManA());
				}
			});

			List<SysUser> userList = new ArrayList<>();
			if(userIds.size()>0){
				userList = userService.listByIds(userIds);
			}
	    	Map<Long,String> userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));

			Map<Long,String> atmMap = new HashMap<>();
			if (atmIds.size() > 0) {
				List<AtmDevice> atmList = atmDeviceService.listByIds(atmIds);
				atmMap =atmList.stream().collect(Collectors.toMap(AtmDevice::getId, AtmDevice::getTerNo));
			}
			
			Map<Long,String> bankCleanMap = new HashMap<>();
			if (bankIds.size() > 0) {
		    	List<Bank> bankList = bankService.listByIds(bankIds);
		    	bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
			}

			Map<Long, String> finalAtmMap = atmMap;
			Map<Long, String> finalBankCleanMap = bankCleanMap;
			atmTaskCardList = atmTaskCardPage.getRecords().stream().map(atmTaskCardTmp -> {
	    		AtmTaskCardDTO atmTaskCardDTO = new AtmTaskCardDTO();
	    		BeanUtils.copyProperties(atmTaskCardTmp, atmTaskCardDTO);
				atmTaskCardDTO.setBankName(finalBankCleanMap.get(atmTaskCardTmp.getBankId()));
				atmTaskCardDTO.setRetriveDay(DateTimeUtil.timeStampMs2Date(atmTaskCardTmp.getCreateTime(),"yyyy-MM-dd"));
	    		atmTaskCardDTO.setAtmTerNo(finalAtmMap.get(atmTaskCardTmp.getAtmId()));
	    		atmTaskCardDTO.setCollectManAName(atmTaskCardTmp.getCollectManA() == 0 ? "" : userMap.get(atmTaskCardTmp.getCollectManA()));
	    		atmTaskCardDTO.setDispatchManAName(atmTaskCardTmp.getDispatchManA() == 0 ? "" : userMap.get(atmTaskCardTmp.getDispatchManA()));
	    		atmTaskCardDTO.setDeliverBankName(finalBankCleanMap.get(atmTaskCardTmp.getDeliverBankId()));
	    		return atmTaskCardDTO;
	    	}).collect(Collectors.toList());
    	}
	    resultList = new ResultList.Builder().total(atmTaskCardPage.getTotal()).list(atmTaskCardList).build();
	    return Result.success(resultList);
    }

//    @ApiOperation(value = "查询线路网点巡检记录")
//    @ApiImplicitParam(name = "routeId", value = "线路id", required = true, dataType = "Long")
//    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
//    @GetMapping("/bankCheckList")
//    public Result bankCheckList(@RequestParam @Min(value=1, message="routeId不能小于1") Long routeId){
//        AtmBankCheckRecord where = new AtmBankCheckRecord();
//        where.setRouteId(routeId);
//        List<AtmBankCheckRecord> list = atmBankCheckRecordService.list(new QueryWrapper<>(where));
//        if (list.size() == 0) {
//            return Result.success(new ArrayList<>());
//        }
//
//        Set<Long> bankIds = list.stream().map(AtmBankCheckRecord::getBankId).collect(Collectors.toSet());
//        Set<Long> suBankIds = list.stream().map(AtmBankCheckRecord::getSubBankId).collect(Collectors.toSet());
//        bankIds.addAll(suBankIds);
//
//        List<Bank> bankList = bankService.listByIds(bankIds);
//        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));
//
//        List<AtmBankCheckRecordDTO> dtoList = list.stream().map(atmBankCheckRecord -> {
//            AtmBankCheckRecordDTO recordDTO = new AtmBankCheckRecordDTO();
//            BeanUtils.copyProperties(atmBankCheckRecord,recordDTO);
//            Bank bank = bankMap.get(atmBankCheckRecord.getBankId());
//            if (null != bank) {
//                recordDTO.setBankName(bank.getFullName());
//            }
//            bank = bankMap.get(atmBankCheckRecord.getSubBankId());
//            if (null != bank) {
//                recordDTO.setSubBankName(bank.getFullName());
//            }
//            Map map = JSONObject.parseObject(atmBankCheckRecord.getRoomCheckResult());
//            Map<String,Integer> resultMap = new HashMap<>();
//            for (Object key : map.keySet()) {
//                resultMap.put((String)key,(Integer) map.get(key));
//            }
//            recordDTO.setRoomCheckResults(resultMap);
//
//            Map map2 = JSONObject.parseObject(atmBankCheckRecord.getHallCheckResult());
//            Map<String,Integer> resultMap2 = new HashMap<>();
//            for (Object key : map2.keySet()) {
//                resultMap2.put((String)key,(Integer) map2.get(key));
//            }
//            recordDTO.setHallCheckResults(resultMap2);
//            return recordDTO;
//        }).collect(Collectors.toList());
//
//    	return Result.success(dtoList);
//    }

	@ApiOperation(value = "查询线路网点巡检记录")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "limit", value = "每页条数", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "beginDate", value = "开始日期", required = false, dataType = "Long"),
		@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "Long"),
		@ApiImplicitParam(name = "subBankId", value = "银行网点", required = false, dataType = "Long"),
		@ApiImplicitParam(name = "checkResult", value = "巡检结果", required = false, dataType = "Integer")
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@GetMapping("/bankCheckList")
	public Result bankCheckList(@RequestParam(defaultValue = "1")  Integer page,
								@RequestParam(defaultValue = "10")  Integer limit,
								@RequestParam(required=false) Long beginDate,
								@RequestParam(required=false) Long endDate,
								@RequestParam(required=false) Long subBankId,
								@RequestParam(required=false) Integer checkResult){
		ResultList resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
		Long dtBegin = null;
		if (beginDate != null) {
			dtBegin = DateTimeUtil.getDailyStartTimeMs(beginDate);
		}
		Long dtEnd = null;
		if (endDate != null) {
			dtEnd = DateTimeUtil.getDailyEndTimeMs(endDate);
		}
		Long bankId = UserContextHolder.getBankId();
		IPage<AtmBankCheckRecord> pageList = atmBankCheckRecordService.findListByPage(page,limit,bankId,dtBegin,dtEnd,subBankId,checkResult);
		if (pageList.getRecords().size() == 0) {
			return Result.success(resultList);
		}

		Set<Long> bankIds = pageList.getRecords().stream().map(AtmBankCheckRecord::getBankId).collect(Collectors.toSet());
		Set<Long> suBankIds = pageList.getRecords().stream().map(AtmBankCheckRecord::getSubBankId).collect(Collectors.toSet());
		Set<Long> routeIds = pageList.getRecords().stream().map(AtmBankCheckRecord::getRouteId).collect(Collectors.toSet());
		bankIds.addAll(suBankIds);
		List<Bank> bankList = bankService.listByIds(bankIds);
		Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));
		List<Route> routeList = routeService.listByIds(routeIds);
		Map<Long,String> routeNoMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteNo,(key1,key2)->key2));
		List<AtmBankCheckRecordDTO> dtoList = pageList.getRecords().stream().map(atmBankCheckRecord -> {
			AtmBankCheckRecordDTO recordDTO = new AtmBankCheckRecordDTO();
			BeanUtils.copyProperties(atmBankCheckRecord,recordDTO);
			recordDTO.setRouteNo(routeNoMap.getOrDefault(atmBankCheckRecord.getRouteId(),""));
			Bank bank = bankMap.get(atmBankCheckRecord.getBankId());
			if (null != bank) {
				recordDTO.setBankName(bank.getFullName());
			}
			bank = bankMap.get(atmBankCheckRecord.getSubBankId());
			if (null != bank) {
				recordDTO.setSubBankName(bank.getFullName());
			}
			Map map = JSONObject.parseObject(atmBankCheckRecord.getRoomCheckResult());
			Map<String,Integer> resultMap = new HashMap<>();
			for (Object key : map.keySet()) {
				resultMap.put((String)key,(Integer) map.get(key));
			}
			recordDTO.setRoomCheckResults(resultMap);

			Map map2 = JSONObject.parseObject(atmBankCheckRecord.getHallCheckResult());
			Map<String,Integer> resultMap2 = new HashMap<>();
			for (Object key : map2.keySet()) {
				resultMap2.put((String)key,(Integer) map2.get(key));
			}
			recordDTO.setHallCheckResults(resultMap2);
			return recordDTO;
		}).collect(Collectors.toList());
		resultList = new ResultList.Builder().total(pageList.getTotal()).list(dtoList).build();
		return Result.success(resultList);
	}
    
    
//    @ApiOperation(value = "清分任务记录列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1", dataTypeClass = Integer.class),
//            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
//            @ApiImplicitParam(name = "taskDate", value = "任务日期", dataType = "String", defaultValue = "", dataTypeClass = String.class),
//            @ApiImplicitParam(name = "routeId", value = "线路ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
//            @ApiImplicitParam(name = "statusT", value = "清分状态", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class)
//    })
//    @GetMapping("/clearTaskList")
//    public Result clearTaskList(@RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam(defaultValue = "10") Integer limit,
//                       @RequestParam(defaultValue = "") String taskDate,
//                       @RequestParam(defaultValue = "-1") Integer statusT,
//                       @RequestParam(defaultValue = "0") Long routeId){
//
//    	ResultList resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
//    	Long bankId = UserContextHolder.getBankId();
//		if(bankId == 0){
//			return Result.success(resultList);
//		}
//
//        AtmClearTask clearTask = new AtmClearTask();
//        clearTask.setBankId(bankId);
//        clearTask.setRouteId(routeId);
//        clearTask.setStatusT(statusT);
//        clearTask.setTaskDate(taskDate);
//
//        IPage<AtmClearTask> taskPage = clearTaskService.findListByPage(page,limit,clearTask);
//        Set<Long> bankIds = taskPage.getRecords().stream().map(AtmClearTask::getBankId).collect(Collectors.toSet());
//        Set<Long> atmIds = taskPage.getRecords().stream().map(AtmClearTask::getAtmId).collect(Collectors.toSet());
//        Set<Long> routeIds = taskPage.getRecords().stream().map(AtmClearTask::getRouteId).collect(Collectors.toSet());
//        Set<Long> empIds = taskPage.getRecords().stream().map(t -> {
//            HashSet<Long> set = new HashSet<>(2);
//            set.add(t.getCheckMan());
//            set.add(t.getClearMan());
//            return set;
//        }).flatMap(Collection::stream).filter(p -> p != 0).collect(Collectors.toSet());
//
//        Map<Long,String> bankMap = new HashMap<>();
//        if (bankIds.size() > 0){
//            List<Bank> bankList = bankService.listByIds(bankIds);
//            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
//        }
//        Map<Long,String> atmMap = new HashMap<>();
//        if (atmIds.size() > 0){
//            List<AtmDevice> atmList = atmService.listByIds(atmIds);
//            atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
//        }
//        //清点员、复点员
//        Map<Long,String> empMap = new HashMap<>();
//        if (empIds.size() > 0){
//            List<Employee> employeeList = employeeService.listByIds(empIds);
//            empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,s -> s.getEmpNo() +"/" +s.getEmpName()));
//        }
//        //计算线路ID
//        Map<Long,String> routeMap = new HashMap<>();
//        if (routeIds.size() > 0){
//            List<Route> routeList = routeService.listByIds(routeIds);
//            routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, r-> r.getRouteName() + "/" + r.getRouteNo()));
//        }
//
//        Map<Long, String> finalBankMap = bankMap;
//        Map<Long, String> finalAtmMap = atmMap;
//        Map<Long, String> finalRouteMap = routeMap;
//        Map<Long, String> finalEmpMap = empMap;
//        List<AtmClearTaskDTO> clearTaskDTOList = taskPage.getRecords().stream().map(item -> {
//            AtmClearTaskDTO clearTaskDTO = new AtmClearTaskDTO();
//            BeanUtils.copyProperties(item,clearTaskDTO);
//            clearTaskDTO.setRouteText(Optional.ofNullable(finalRouteMap.get(item.getRouteId())).orElse(""));
//            clearTaskDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getBankId())).orElse(""));
//            clearTaskDTO.setTerNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
//            clearTaskDTO.setClearManName(Optional.ofNullable(finalEmpMap.get(item.getClearMan())).orElse(""));
//            clearTaskDTO.setCheckManName(Optional.ofNullable(finalEmpMap.get(item.getCheckMan())).orElse(""));
//            return clearTaskDTO;
//        }).collect(Collectors.toList());
//        resultList = new ResultList.Builder<>().total(taskPage.getTotal()).list(clearTaskDTOList).build();
//        return Result.success(resultList);
//    }

	@ApiOperation(value = "清分任务记录列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1", dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "beginDate", value = "开始日期", dataType = "Long", dataTypeClass = Long.class),
			@ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "Long", dataTypeClass = Long.class),
			@ApiImplicitParam(name = "atmNo", value = "设备编号", dataType = "String", dataTypeClass = String.class),
			@ApiImplicitParam(name = "errType", value = "差错类型", dataType = "Integer", dataTypeClass = Integer.class)
	})
	@GetMapping("/clearTaskList")
	public Result queryClearTaskList(@RequestParam(defaultValue = "1") Integer page,
								@RequestParam(defaultValue = "10") Integer limit,
								@RequestParam(required = false) Long beginDate,
								@RequestParam(required = false) Long endDate,
								@RequestParam(required = false) String atmNo,
								@RequestParam(required = false) Integer errType){

		ResultList resultList = null;
		Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
			return Result.success(resultList);
		}
		String dayBegin = null;
		String dayEnd = null;
		if (beginDate != null) {
			dayBegin = DateTimeUtil.timeStampMs2Date(beginDate, "yyyy-MM-dd");
		}
		if (endDate != null) {
			dayEnd = DateTimeUtil.timeStampMs2Date(endDate, "yyyy-MM-dd");
		}
		Long atmId = null;
		if (!StringUtils.isEmpty(atmNo)) {
			AtmDevice atmDevice = atmDeviceService.getByTerNo(atmNo);
			if (null != atmDevice) {
				atmId = atmDevice.getId();
			}
		}
		IPage<AtmClearTask> taskPage = clearTaskService.findListByPage(page,limit,bankId,dayBegin,dayEnd,atmId,errType);
		Set<Long> bankIds = taskPage.getRecords().stream().map(AtmClearTask::getBankId).collect(Collectors.toSet());
		Set<Long> atmIds = taskPage.getRecords().stream().map(AtmClearTask::getAtmId).collect(Collectors.toSet());
		Set<Long> routeIds = taskPage.getRecords().stream().map(AtmClearTask::getRouteId).collect(Collectors.toSet());
		Set<Long> empIds = taskPage.getRecords().stream().map(t -> {
			HashSet<Long> set = new HashSet<>(2);
			set.add(t.getCheckMan());
			set.add(t.getClearMan());
			return set;
		}).flatMap(Collection::stream).filter(p -> p != 0).collect(Collectors.toSet());

		Map<Long,String> bankMap = new HashMap<>();
		if (bankIds.size() > 0){
			List<Bank> bankList = bankService.listByIds(bankIds);
			bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
		}
		Map<Long,String> atmMap = new HashMap<>();
		if (atmIds.size() > 0){
			List<AtmDevice> atmList = atmService.listByIds(atmIds);
			atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
		}
		//清点员、复点员
		Map<Long,String> empMap = new HashMap<>();
		if (empIds.size() > 0){
			List<Employee> employeeList = employeeService.listByIds(empIds);
			empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,s -> s.getEmpNo() +"/" +s.getEmpName()));
		}
		//计算线路ID
		Map<Long,String> routeMap = new HashMap<>();
		if (routeIds.size() > 0){
			List<Route> routeList = routeService.listByIds(routeIds);
			routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, r-> r.getRouteName() + "/" + r.getRouteNo()));
		}

		Map<Long, String> finalBankMap = bankMap;
		Map<Long, String> finalAtmMap = atmMap;
		Map<Long, String> finalRouteMap = routeMap;
		Map<Long, String> finalEmpMap = empMap;
		List<AtmClearTaskDTO> clearTaskDTOList = taskPage.getRecords().stream().map(item -> {
			AtmClearTaskDTO clearTaskDTO = new AtmClearTaskDTO();
			BeanUtils.copyProperties(item,clearTaskDTO);
			clearTaskDTO.setRouteText(Optional.ofNullable(finalRouteMap.get(item.getRouteId())).orElse(""));
			clearTaskDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getBankId())).orElse(""));
			clearTaskDTO.setTerNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
			clearTaskDTO.setClearManName(Optional.ofNullable(finalEmpMap.get(item.getClearMan())).orElse(""));
			clearTaskDTO.setCheckManName(Optional.ofNullable(finalEmpMap.get(item.getCheckMan())).orElse(""));
			return clearTaskDTO;
		}).collect(Collectors.toList());
		resultList = new ResultList.Builder<>().total(taskPage.getTotal()).list(clearTaskDTOList).build();
		return Result.success(resultList);
	}
    
    @ApiOperation(value = "清分任务详情数据")
    @ApiImplicitParam(name = "taskId", value = "ATM清分任务id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @GetMapping("/clearTaskDetail")
    public Result clearTaskDetail(Long taskId){
        ClearTaskVO taskVO = clearTaskService.getDetail(taskId);
        return Result.success(taskVO);
    }

	@ApiOperation(value = "导出清分记录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "beginDate", value = "开始日期", dataType = "Long", dataTypeClass = Long.class),
			@ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "Long", dataTypeClass = Long.class),
			@ApiImplicitParam(name = "terNo", value = "设备编号", dataType = "String", dataTypeClass = String.class),
			@ApiImplicitParam(name = "errType", value = "差错类型", dataType = "Integer", dataTypeClass = Integer.class)
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/clearTask/export")
	public void exportAtmTask(HttpServletResponse response,
							  @RequestParam Long beginDate,
							  @RequestParam Long endDate,
							  @RequestParam(required=false) String terNo,
							  @RequestParam(required=false) Integer errType){

		if (beginDate == 0L || endDate == 0L) {
			throw new BusinessException(-1,"无效日期");
		}
		Long bankId = UserContextHolder.getBankId();
		Long atmId = null;
		if (!StringUtils.isEmpty(terNo)) {
			AtmDevice atmDevice = atmDeviceService.getByTerNo(terNo);
			if (null != atmDevice) {
				atmId = atmDevice.getId();
			}
		}
		String dayBegin = DateTimeUtil.timeStampMs2Date(beginDate, "yyyy-MM-dd");
		String dayEnd = DateTimeUtil.timeStampMs2Date(endDate, "yyyy-MM-dd");

		//获取任务集合
		List<AtmClearTask> taskList = clearTaskService.findListAll(bankId,dayBegin,dayEnd,atmId,errType);
		if(taskList.size() == 0){
			throw new BusinessException(-1,"没有记录需要导出");
		}

		Set<Long> bankIds = taskList.stream().map(AtmClearTask::getBankId).collect(Collectors.toSet());
		Set<Long> atmIds = taskList.stream().map(AtmClearTask::getAtmId).collect(Collectors.toSet());
		Set<Long> routeIds = taskList.stream().map(AtmClearTask::getRouteId).collect(Collectors.toSet());
		Set<Long> empIds = taskList.stream().map(t -> {
			HashSet<Long> set = new HashSet<>(2);
			set.add(t.getCheckMan());
			set.add(t.getClearMan());
			return set;
		}).flatMap(Collection::stream).filter(p -> p != 0).collect(Collectors.toSet());

		Map<Long,String> bankMap = new HashMap<>();
		if (bankIds.size() > 0){
			List<Bank> bankList = bankService.listByIds(bankIds);
			bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
		}
		Map<Long,String> atmMap = new HashMap<>();
		if (atmIds.size() > 0){
			List<AtmDevice> atmList = atmService.listByIds(atmIds);
			atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
		}
		//清点员、复点员
		Map<Long,String> empMap = new HashMap<>();
		if (empIds.size() > 0){
			List<Employee> employeeList = employeeService.listByIds(empIds);
			empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee::getEmpName));
		}
		//计算线路ID
		Map<Long,String> routeMap = new HashMap<>();
		if (routeIds.size() > 0){
			List<Route> routeList = routeService.listByIds(routeIds);
			routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, Route::getRouteNo));
		}

		List<BankClearTaskRecordHead> taskRecords = new ArrayList<BankClearTaskRecordHead>();
		for (int i = 0; i < taskList.size(); i++) {
			AtmClearTask task = taskList.get(i);
			BankClearTaskRecordHead recordHead = new BankClearTaskRecordHead();
			recordHead.setIndex(i+1);
			recordHead.setTaskDate(task.getTaskDate());
			recordHead.setRouteNo(routeMap.get(task.getRouteId()));
			recordHead.setBankName(bankMap.get(task.getBankId()));
			recordHead.setTerNo(atmMap.get(task.getAtmId()));
			recordHead.setErrorType(ClearErrorTypeEnum.getText(task.getErrorType()));
			recordHead.setPlanAmount(task.getPlanAmount());
			recordHead.setClearAmount(task.getClearAmount());
			recordHead.setErrorAmount(task.getErrorAmount());
			taskRecords.add(recordHead);
		}

		taskRecords.sort((x, y) -> Integer.compare(Integer.parseInt(x.getRouteNo()), Integer.parseInt(y.getRouteNo())));
		taskRecords.forEach((entity) -> entity.setRouteNo(entity.getRouteNo() + "号线"));
		String title = bankMap.get(bankId) + "清分记录";
		try {
			BankClearTaskRecordHead.setExcelPropertyTitle(title);
			EasyExcelUtil.exportPrintExcel(response,title,BankClearTaskRecordHead.class,taskRecords,null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(-1,"导出清分记录出错");
		}
	}

    
    @ApiOperation(value = "出入库记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "orderDate", value = "查询日期", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "orderType", value = "类别", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "subType", value = "类型", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class)
    })
    @GetMapping("/vaultOrderList")
    public Result vaultOrderList(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(name = "orderDate", defaultValue = "0") Long orderDate,
                       @RequestParam(name = "orderType", defaultValue = "-1") Integer orderType,
                       @RequestParam(name = "subType", defaultValue = "-1") Integer subType){
    	ResultList resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(resultList);
		}
    	
        VaultOrder vaultOrder = new VaultOrder();
        vaultOrder.setDepartmentId(0L);
        vaultOrder.setBankId(bankId);
        vaultOrder.setOrderDate(orderDate);
        vaultOrder.setOrderType(orderType);
        vaultOrder.setSubType(subType);
        IPage<VaultOrder> orderPage = orderService.findListByPage(page,limit,vaultOrder);

        //获取员工ID
        Set<Long> empIds = orderPage.getRecords().stream().map( t -> {
            HashSet<Long> set = new HashSet<>();
            set.add(t.getWhOpMan());
            set.add(t.getWhCheckMan());
            set.add(t.getWhConfirmMan());
            return set;
        }).flatMap(Collection::stream).filter(r -> r != 0).collect(Collectors.toSet());
        //查询员工表数据
        Map<Long,String> employeeMap = new HashMap<>();
        if (!empIds.isEmpty()){
            List<Employee> employeeList =  employeeService.listByIds(empIds);
            employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        }
        //当前用户ID
        long userId = UserContextHolder.getUserId();
        List<Bank> bankList = bankService.list();
        Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        Map<Long, String> finalEmployeeMap = employeeMap;
        List<VaultOrderDto>  volumeDtoList = orderPage.getRecords().stream().map(t -> {
            VaultOrderDto vaultOrderDto = new VaultOrderDto();
            BeanUtils.copyProperties(t,vaultOrderDto);
            String userIds = t.getNextUserIds();
            if (StringUtils.isEmpty(userIds)){
                vaultOrderDto.setAudit(false);
            }else{
                OptionalLong optional = Arrays.stream(userIds.split(",")).mapToLong(Long::parseLong).filter(s -> s == userId).findFirst();
                vaultOrderDto.setAudit(optional.isPresent());
            }
            vaultOrderDto.setWhOpManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhOpMan())).orElse(""));
            vaultOrderDto.setWhCheckManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhCheckMan())).orElse(""));
            vaultOrderDto.setWhConfirmManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhConfirmMan())).orElse(""));
            vaultOrderDto.setBankName(Optional.ofNullable(bankMap.get(t.getBankId())).orElse(""));
            return vaultOrderDto;
        }).collect(Collectors.toList());

        resultList = ResultList.builder().total(orderPage.getTotal()).list(volumeDtoList).build();
        return Result.success(resultList);
    }

    
    @ApiOperation(value = "出入库单明细")
    @GetMapping("/vaultOrderDetail")
    public Result vaultOrderDetail(Long orderId){
        VaultOrder vaultOrder = orderService.getById(orderId);
        if (vaultOrder == null){
            return Result.fail("数据错误");
        }
       List<VaultOrderRecord> recordList = orderService.getDetail(orderId);

       List<Denom> denomList = denomService.list();
       Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        LinkedList usableList = new LinkedList();
        LinkedList goodList = new LinkedList();
        LinkedList badList = new LinkedList();
        LinkedList unclearList = new LinkedList();
       //数据分类
       recordList.stream().forEach(item -> {
           VaultOrderRecordVO recordVO = new VaultOrderRecordVO();
           BeanUtils.copyProperties(item,recordVO);
           recordVO.setDenomText(Optional.ofNullable(denomMap.get(item.getDenomId())).get() );
           if (item.getDenomType() == DenomTypeEnum.USABLE.getValue()){
               usableList.add(recordVO);
           }else if (item.getDenomType() == DenomTypeEnum.BAD.getValue()){
               badList.add(recordVO);
           }else if (item.getDenomType() == DenomTypeEnum.GOOD.getValue()){
               goodList.add(recordVO);
           } else if (item.getDenomType() == DenomTypeEnum.UNCLEAR.getValue()){
               unclearList.add(recordVO);
           }
       });

        HashMap map = new HashMap(4);
        map.put("usable",usableList);
        map.put("bad",badList);
        map.put("good",goodList);
        map.put("unclear",unclearList);

        //ATM加钞详情
        if (vaultOrder.getSubType() == 0){
            List<RouteTaskDTO> taskDTOList = orderService.getAtmTask(orderId);
            map.put("routeList", taskDTOList);
        }
        //根据出入库类别判断
        String typeEvent = vaultOrder.getOrderType() == 0 ? "STORAGE_IN" : "STORAGE_IN_OUT";
        List<WorkflowRecordDTO> recordDTOList = recordService.getDetailList(orderId,typeEvent, vaultOrder.getDepartmentId());
        List<WorkflowRecordDTO> undoList = recordService.getDetailList(orderId,"STORAGE_CANCEL", vaultOrder.getDepartmentId());
        map.put("audit",recordDTOList);
        map.put("undo", undoList);
       return Result.success(map);
    }
    
    
    @ApiOperation(value = "库存记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10",dataTypeClass = Integer.class)
    })
    @GetMapping("/vaultVolumeList")
    public Result vaultVolumeList(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
    	
    	ResultList resultList = new ResultList.Builder().total(0L).list(new ArrayList<>()).build();
//    	Long bankId = UserContextHolder.getBankId();
//		if(bankId == 0){
//			return Result.success(resultList);
//		}
    	SysUser sysUser = userService.getById(UserContextHolder.getUserId());
    	
    	if(StringUtils.isBlank(sysUser.getStockBank())){
    		return Result.success(resultList);
    	}
    	//获取网点Map
        List<Bank> bankList = bankService.list();
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));
    	//获取部门Map
    	Map<String, String> departmentMap = commonService.getDepartmentMap(null);
    	
        List<VaultVolumeDto>  volumeDtoList = new ArrayList<>();
		String[] stockBankId = sysUser.getStockBank().split(",");
		for (String bankId : stockBankId) {
	        VaultVolum vaultVolum = new VaultVolum();
	        vaultVolum.setBankId(Long.parseLong(bankId));
	        IPage<VaultVolum> vaultVolumPage = volumeService.findListByPage(page,limit,vaultVolum);
	//        List<Denom> denomList = denomService.list();
	//        Map denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
	        List<VaultVolumeDto>  volumeDtoListTmp = vaultVolumPage.getRecords().stream().map(t -> {
	            VaultVolumeDto vaultVolumeDto = new VaultVolumeDto();
	            BeanUtils.copyProperties(t,vaultVolumeDto);
	            vaultVolumeDto.setBankName(departmentMap.get(bankMap.get(t.getBankId()).getDepartmentId().toString()) + "-" +bankMap.get(t.getBankId()).getShortName());
	            return vaultVolumeDto;
	        }).collect(Collectors.toList());
	        volumeDtoList.addAll(volumeDtoListTmp);
		}

        resultList = ResultList.builder().total(volumeDtoList.size()).list(volumeDtoList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "库存银行券别列表")
    @GetMapping("/bankDenom")
    public Result bankDenom(){
    	HashMap map = new HashMap();
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(map);
		}
        List<Denom> denomList = denomService.list();
        Map denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
        List<VaultVolum> volumList = volumeService.getBankList(bankId);
        Map<Integer,List<VaultVolum>> denomTypeMap = volumList.stream().collect(Collectors.groupingBy(VaultVolum::getDenomType));

        denomTypeMap.entrySet().stream().forEach(entry -> {
            List<VaultVolum> volumList1 = entry.getValue();
            List<VaultVolumeDenomDto> newVolumList = volumList1.stream().map(s -> {
                        VaultVolumeDenomDto vaultVolumeDenomDto = new VaultVolumeDenomDto();
                        vaultVolumeDenomDto.setDenomId(s.getDenomId());
                        vaultVolumeDenomDto.setAmount(s.getAmount());
                        vaultVolumeDenomDto.setDenomType(s.getDenomType());
                        vaultVolumeDenomDto.setCount(s.getCount());
                        vaultVolumeDenomDto.setDenomName((String) Optional.ofNullable(denomMap.get(s.getDenomId())).orElse(""));
                        return vaultVolumeDenomDto;
                    }).collect(Collectors.toList());
            map.put(entry.getKey(), newVolumList);
        });

        return Result.success(map);
    }
    
    @ApiOperation(value = "下级机构下拉选项")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/subBankOption")
    public Result subBankOption(){
    	
    	List<Map> mapList = new ArrayList<>();
    	Long bankId = UserContextHolder.getBankId();
		if(bankId == 0){
			return Result.success(mapList);
		}
        Bank bank = new Bank();
        bank.setId(bankId);
        bank.setBankType(0);
        List<Bank> bankList = bankService.getSubBank(bank);
        mapList = bankList.stream().map(item -> {
            HashMap map = new HashMap();
            map.put("id", item.getId());
            map.put("name",item.getShortName());
            map.put("routeNo",item.getRouteNo());
            return map;
        }).collect(Collectors.toList());
        return Result.success(mapList);
    }
    
}
