package com.zcxd.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.*;
import com.zcxd.base.dto.excel.AtmTaskRecordHead;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.service.*;
import com.zcxd.base.vo.*;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.AtmAdditionCashStatusEnum;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName ATMTaskController
 * @Description ATM任务控制器
 * @author 秦江南
 * @Date 2021年6月2日下午6:38:16
 */
@RestController
@RequestMapping("/atmTask")
@Api(tags="ATM任务")
public class ATMTaskController {

    @Autowired
    private ATMTaskService atmTaskService;
    
	@Autowired
    private CashboxPackRecordService cashboxPackRecordService;
    
    @Autowired
    private ATMTaskReturnService atmTaskReturnService;
    
    @Autowired
    private ATMTaskCheckRecordService atmTaskCheckRecordService;
    
    @Autowired
    private ATMTaskRepairRecordService atmTaskRepairRecordService;
    
    @Autowired
    private RouteService routeService;

//    @Autowired
//    private RouteTemplateService routeTemplateService;
//    
//    @Autowired
//    private RouteTemplateAtmService routeTemplateAtmService;
    
	@Autowired
	private EmployeeService employeeService;
    
	@Autowired
	private BankService bankService;
	
	@Autowired
	private ATMService atmService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DenomService denomService;
	
	@Autowired
	private AtmAdditionCashService atmAdditionCashService;

    @OperateLog(value = "添加ATM维修任务", type=OperateType.ADD)
    @ApiOperation(value = "添加ATM维修任务")
    @ApiImplicitParam(name = "atmTaskRepairBatchVO", value = "ATM维修任务", required = true, dataType = "ATMTaskRepairBatchVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveRepair")
    public Result saveRepair(@RequestBody @Validated ATMTaskRepairBatchVO atmTaskRepairBatchVO){
    	return atmTaskService.savemultiRepair(atmTaskRepairBatchVO);
    }

    @OperateLog(value = "添加ATM清机任务", type=OperateType.ADD)
    @ApiOperation(value = "添加ATM清机任务")
    @ApiImplicitParam(name = "atmTaskCleanBatchVO", value = "ATM清机任务", required = true, dataType = "ATMTaskCleanBatchVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveClean")
    public Result saveClean(@RequestBody @Validated ATMTaskCleanBatchVO atmTaskCleanBatchVO){
    	return atmTaskService.savemultiClean(atmTaskCleanBatchVO,AtmTaskStatusEnum.CONFRIM.getValue());
    }
    
//    @OperateLog(value = "模板添加ATM清机任务", type=OperateType.ADD)
//    @ApiOperation(value = "模板添加ATM清机任务")
//    @ApiImplicitParam(name = "atmTaskCleanBatchVO", value = "ATM清机任务", required = true, dataType = "ATMTaskCleanBatchVO")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/saveTemplateClean")
//    public Result saveTemplateClean(@RequestBody @Validated ATMTaskCleanBatchVO atmTaskCleanBatchVO){
//    	return atmTaskService.savemultiClean(atmTaskCleanBatchVO,AtmTaskStatusEnum.CREATE.getValue());
//    }
//    
//    @ApiOperation(value = "获取线路模板途径设备信息")
//    @ApiImplicitParams({
//  		@ApiImplicitParam(name = "routeTemplateNo", value = "线路模板编号", required = true, dataType = "String"),
//  	    @ApiImplicitParam(name = "bankId", value = "网点id", required = false, dataType = "Long")
//    })
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @GetMapping("/templateAtmInfo")
//    public Result templateAtmInfo(@RequestParam String routeTemplateNo,
//            @RequestParam(required=false) Long bankId){
//      RouteTemplate routeTemplate = routeTemplateService.getOne((QueryWrapper)Wrappers.query().eq("route_no", routeTemplateNo).eq("deleted", 0));
//      if(routeTemplate == null){
//    	  return Result.fail("线路模板不存在");
//      }
//  	  List<Map<String, Object>> routeTemplateAtmList = routeTemplateAtmService.getRouteTemplateAtmList(routeTemplate.getId(), bankId);
//  	  
//  	  if(routeTemplateAtmList != null && routeTemplateAtmList.size()>0){
//	  	  Set<Long> bankIds = routeTemplateAtmList.stream().map(map-> Long.parseLong(map.get("headBankId").toString())).collect(Collectors.toSet());
//	  	  List<Bank> bankList = bankService.listByIds(bankIds);
//	  	  Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
//	  	
//	  	  routeTemplateAtmList.stream().forEach(map->{
//	  		  map.put("headBank", bankCleanMap.get(map.get("headBankId")));
//	  		  map.remove("headBankId");
//	  	  });
//  	  }
//  	  return Result.success(routeTemplateAtmList);
//    }
    
    @OperateLog(value = "删除ATM任务", type=OperateType.DELETE)
    @ApiOperation(value = "删除ATM任务")
    @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	AtmTask atmTaskTmp = atmTaskService.getById(id);
    	if(!atmTaskTmp.getStatusT().equals(AtmTaskStatusEnum.CREATE.getValue())){
    		return Result.fail("当前状态无法删除");
    	}
    	
    	AtmTask atmTask = new AtmTask();
    	atmTask.setId(id);
    	atmTask.setDeleted(1);
    	boolean update = atmTaskService.updateById(atmTask);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除ATM任务失败");
    }

//    @OperateLog(value = "修改ATM任务", type=OperateType.EDIT)
//    @ApiOperation(value = "修改ATM任务")
//    @ApiImplicitParam(name = "atmTaskUpdateVO", value = "ATM任务", required = true, dataType = "ATMTaskUpdateVO")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/update")
//    public Result update(@RequestBody @Validated ATMTaskUpdateVO atmTaskUpdateVO){
//    	if (atmTaskUpdateVO.getId() == null) {
//            return Result.fail("ATM任务Id不能为空");
//        }
//    	
//    	AtmTask atmTask = new AtmTask();
//		BeanUtils.copyProperties(atmTaskUpdateVO, atmTask);
//    	
//		boolean update = atmTaskService.updateById(atmTask);
//    	if(update)
//    		return Result.success();
//    	
//    	return Result.fail("修改ATM任务失败");
//    }
    
    @ApiOperation(value = "查询ATM任务列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "routeId", value = "线路Id", required = true, dataType = "Long"),
    	@ApiImplicitParam(name = "bankId", value = "银行Id", required = false, dataType = "Long"),
    	@ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam @Min(value=1, message="routeId不能小于1") Long routeId,
    		@RequestParam(required=false) Long bankId,
    		@RequestParam Integer routeType){
    	List<Map<String, Object>> findList = atmTaskService.findList(routeId,bankId,routeType);
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
		    	query.select("bank_id as bankId,denom_value as denomValue,sum(amount) as amount").eq("route_id", routeId);
		    	query.eq("status_t", AtmAdditionCashStatusEnum.OUTSTORE.getValue());
		    	query.eq("bank_id", Long.parseLong(findList.get(0).get("bankId").toString()));
		    	query.eq("deleted", 0);
		    	query.groupBy("bankId");
		    	query.groupBy("denomValue");
		    	additionCashMap = atmAdditionCashService.listMaps(query);
	    	}
    	}
    	
    	Map<String,List<Map<String, Object>>> resultData = new HashMap(); 
    	resultData.put("taskData", findList);
    	resultData.put("additionCash", additionCashMap);
        return Result.success(resultData);
    }
    
    @ApiOperation(value = "查询ATM任务详情")
    @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
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
    	
//    	 AtmTaskCheckRecord atmTaskCheckRecord = atmTaskCheckRecordService.getOne((QueryWrapper)Wrappers.query().eq("atm_task_id", id));
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
    
    
    @ApiOperation(value = "查询钞盒装盒数据")
    @ApiImplicitParam(name = "id", value = "装盒记录id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/cashboxPack/{id}")
    public Result cashboxPack(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	CashboxPackRecord cashbox = cashboxPackRecordService.getById(id);
    	CashboxPackRecordDTO packRecordDTO = new CashboxPackRecordDTO();
    	packRecordDTO.setPackTime(cashbox.getPackTime());
    	packRecordDTO.setTaskDate(cashbox.getTaskDate());
		Set<Long> userIds = new HashSet<>();
		userIds.add(cashbox.getClearManId());
		userIds.add(cashbox.getCheckManId());
    	List<Employee> employeeList = employeeService.listByIds(userIds);
    	Map<Long,String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
    	packRecordDTO.setClearManName(employeeMap.get(cashbox.getClearManId()));
    	packRecordDTO.setCheckManName(employeeMap.get(cashbox.getCheckManId()));
    	packRecordDTO.setBoxNo(cashbox.getBoxNo());
    	Device device = deviceService.getById(cashbox.getDevId());
    	packRecordDTO.setDeviceNo(device.getDeviceNo());
    	Denom denom = denomService.getById(cashbox.getDenomId());
    	packRecordDTO.setDenomName(denom.getName());
    	packRecordDTO.setAmount(cashbox.getAmount());
    	return Result.success(packRecordDTO);
    }
    
    
    
    @OperateLog(value = "任务确认", type=OperateType.EDIT)
    @ApiOperation(value = "任务确认")
    @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/confirm/{id}")
    public Result confirm(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	AtmTask atmTask = atmTaskService.getById(id);
    	if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CREATE.getValue())){
    		return Result.fail("当前任务不是新建状态，无法确认");
    	}
    	atmTask.setId(id);
    	atmTask.setStatusT(AtmTaskStatusEnum.CONFRIM.getValue());
    	boolean update = atmTaskService.updateById(atmTask);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("任务确认失败");
    }
    
    @OperateLog(value = "任务撤销", type=OperateType.EDIT)
    @ApiOperation(value = "任务撤销")
    @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/revoke/{id}")
    public Result revoke(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		return atmTaskService.cancelTask(id);
    	//    	AtmTask atmTask = atmTaskService.getById(id);
//    	if(!atmTask.getStatusT().equals(AtmTaskStatusEnum.CONFRIM.getValue())){
//    		return Result.fail("当前任务不是确认状态，无法撤销");
//    	}
//    	atmTask.setId(id);
//    	atmTask.setStatusT(AtmTaskStatusEnum.CANCEL.getValue());
//    	boolean update = atmTaskService.updateById(atmTask);
//
//    	if(update)
//    		return Result.success();
//
//    	return Result.fail("任务撤销失败");
    }
    
    @OperateLog(value = "任务批量确认", type=OperateType.EDIT)
    @ApiOperation(value = "任务批量确认")
    @ApiImplicitParam(name = "ids", value = "任务id", required = true, dataType = "String")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/batchConfirm")
    public Result batchConfirm(@RequestParam String ids){
    	return atmTaskService.batchConfirm(ids);
    }
    
    /**
     * 根据日期，查询各银行的ATM加钞金额任务数情况
     * @param taskDate 任务日期
     * @param departmentId 部门ID
     * @return
     */
    @ApiOperation(value = "银行ATM加钞任务数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long")
    })
    @GetMapping("/getBankTask")
    public Result getBankAtmTask(Long taskDate, Long departmentId){
       List<BankAtmTaskDto> atmTaskDtoList = atmTaskService.getBankAtmTask(taskDate, departmentId);
        return Result.success(atmTaskDtoList);
    }
    
    @OperateLog(value = "导入ATM清机任务", type=OperateType.ADD)
    @ApiOperation(value = "导入ATM清机任务")
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile"),
	    @ApiImplicitParam(name = "bankType", value = "银行类型", required = true, dataType = "Integer"),
	    @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/importAtmTask")
    public Result importAtmTask(MultipartFile file,@RequestParam Integer bankType,@RequestParam Long taskDate) {
    	if(file == null){
    		return Result.fail("文件不存在");
    	}

    	return atmTaskService.cleanExcelImport(bankType , taskDate, file);
    }
    
    @OperateLog(value = "导入添加ATM清机任务", type=OperateType.ADD)
    @ApiOperation(value = "导入添加ATM清机任务")
    @ApiImplicitParam(name = "atmTaskCleanImportSaveVO", value = "导入ATM清机任务", required = true, dataType = "ATMTaskCleanImportSaveVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveImportClean")
    public Result saveImportClean(@RequestBody @Validated ATMTaskCleanImportSaveVO atmTaskCleanImportSaveVO){
    	return atmTaskService.saveImportClean(atmTaskCleanImportSaveVO);
    }
    
    
    @ApiOperation(value = "线路下拉框选项")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "bankId", value = "银行Id", required = false, dataType = "Long"),
    	@ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/routeOption")
    public Result option(@RequestParam Long routeDate,
    		@RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId,
    		@RequestParam(required=false) Long bankId,
    		@RequestParam Integer routeType) {
    	List<Map> routeList = routeService.getTaskRoute(departmentId,routeDate,bankId,routeType);

        return Result.success(routeList);
    }

    @ApiOperation(value = "ATM加钞任务数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "银行Id",required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "orderId", value = "订单Id", dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping("/getTaskList")
    public Result getTaskList(Long taskDate, Long departmentId,Long bankId,Long orderId){
        List<AtmRouteTaskDTO> taskDTOList = atmTaskService.getTaskList(taskDate, departmentId, bankId, orderId);
        return Result.success(taskDTOList);
    }

	  @ApiOperation(value = "任务记录")
	  @ApiImplicitParams({
	  	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
	      @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
	      @ApiImplicitParam(name = "type", value = "查询类型", required = true, dataType = "Integer"),
	      @ApiImplicitParam(name = "departmentId", value = "所属部门", required = true, dataType = "Long"),
	      @ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
	      @ApiImplicitParam(name = "taskType", value = "任务类型", required = false, dataType = "Integer"),
	      @ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long"),
	      @ApiImplicitParam(name = "bankId", value = "所属银行", required = true, dataType = "Long"),
	      @ApiImplicitParam(name = "importBatch", value = "下发类型", required = false, dataType = "Long")
	  })
	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	  @GetMapping("/record")
	  public Result findListByPage(@RequestParam Integer page,
	                                 @RequestParam Integer limit,
	                                 @RequestParam Integer type,
	                                 @RequestParam Long departmentId,
	                                 @RequestParam(required=false) @Size(max=32,message="terNo最大长度为32") String terNo,
	                                 @RequestParam(required=false) Integer taskType,
	                                 @RequestParam(required=false) Long taskDate,
	                                 @RequestParam(required=false) Integer status,
	                                 @RequestParam Long bankId,
	                                 @RequestParam(required=false) Long importBatch){
		  
		Map<String, Object> resultMap = new HashMap();
		ResultList resultData = null;
		IPage<AtmTask> atmTaskPage = null;
		AtmTask atmTask = new AtmTask();
		atmTask.setDepartmentId(departmentId);
		atmTask.setBankId(bankId);
		if(type == 0){
			atmTask.setTaskType(taskType);
			atmTask.setTaskDate(taskDate);
			atmTask.setImportBatch(importBatch);
			atmTask.setStatusT(status);
			if(!StringUtils.isBlank(terNo)){
				AtmDevice atmDevice = atmService.getOne((QueryWrapper)Wrappers.query().eq("ter_no", terNo).eq("deleted", 0));
				if(atmDevice == null){
					resultData = new ResultList.Builder().total(0).list(new ArrayList<>()).build();
					resultMap.put("data", resultData);
					return Result.success(resultMap);
				}else{
					atmTask.setAtmId(atmDevice.getId());
				}
			}
			atmTaskPage = atmTaskService.findListByPage(page, limit, atmTask);
		}else if(type == 1){
			if(taskDate == null){
				return Result.fail("请选择任务日期！");
			}
			atmTask.setTaskDate(taskDate);
			atmTask.setStatusT(AtmTaskStatusEnum.CANCEL.getValue());
			atmTaskPage = atmTaskService.findTrimListByPage(page, limit, atmTask);
		}else{
			return Result.fail("参数有误");
		}
		
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
	  		
	  		if(type == 1){
		  		Map<String, Object> cancelMap = atmTaskService.getMap((QueryWrapper)Wrappers.query()
		  				.select("count(1) as count ,IFNULL(sum(amount),0) as amount").eq("task_date", atmTask.getTaskDate())
		  				.eq("department_id", atmTask.getDepartmentId()).eq("bank_id", atmTask.getBankId()).eq("status_t",atmTask.getStatusT()).eq("deleted", 0));
		  		
		  		Map<String, Object> addMap = atmTaskService.getMap((QueryWrapper)Wrappers.query()
		  				.select("count(1) as count ,IFNULL(sum(amount),0) as amount").eq("task_date", atmTask.getTaskDate())
		  				.eq("department_id", atmTask.getDepartmentId()).eq("bank_id", atmTask.getBankId()).eq("import_batch", 0).eq("deleted", 0));
		  		resultMap.put("cancelMap", cancelMap);
		  		resultMap.put("addMap", addMap);
		  		
	  		}
	  	}
	  	
	  	 resultData = new ResultList.Builder().total(atmTaskPage.getTotal()).list(resultList).build();
	  	 resultMap.put("data", resultData);
	     return Result.success(resultMap);
	  }
	  
	  
	  @ApiOperation(value = "导出派遣单")
	  @ApiImplicitParams({
	      @ApiImplicitParam(name = "departmentId", value = "所属部门", required = true, dataType = "Long"),
	      @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long"),
	      @ApiImplicitParam(name = "bankId", value = "所属银行", required = true, dataType = "Long")
	  })
	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	  @GetMapping("/exportRecord")
	  public void exportRecord(HttpServletResponse response,@RequestParam Long departmentId,
	                                 @RequestParam Long taskDate,
	                                 @RequestParam Long bankId){
		  
		List<AtmTask> atmTasks = new ArrayList<>();
		  
		//获取已完成维修任务集合
	    List<AtmTask> repairTaskList = atmTaskService.list((QueryWrapper)Wrappers.query()
	    		.eq("task_date", taskDate).eq("department_id", departmentId).eq("bank_id", bankId)
	    		.eq("status_t", AtmTaskStatusEnum.FINISH.getValue())
	    		.eq("task_type", AtmTaskTypeEnum.REPAIR.getValue()).eq("deleted", 0));
	    
	   //获取已完成新增清机加钞任务集合
	    List<AtmTask> amountTaskList = atmTaskService.list((QueryWrapper)Wrappers.query()
	    		.eq("task_date", taskDate).eq("department_id", departmentId).eq("bank_id", bankId)
	    		.eq("status_t", AtmTaskStatusEnum.FINISH.getValue())
	    		.in("task_type", AtmTaskTypeEnum.CASHIN.getValue(),AtmTaskTypeEnum.CLEAN.getValue()).eq("import_batch", 0).eq("deleted", 0));
		  
	    atmTasks.addAll(repairTaskList);
	    atmTasks.addAll(amountTaskList);
		  if(atmTasks == null || atmTasks.size() == 0){
			  throw new BusinessException(-1,"没有记录需要导出");
		  }
			 
		  Set<Long> atmIds = new HashSet<Long>();
		  Set<Long> bankIds = new HashSet<Long>();
		  Set<Long> routeIds = new HashSet<Long>();
		  atmTasks.stream().forEach(atmTaskTmp -> {
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
		  
	    	List<AtmTaskRecordHead> taskRecords = new ArrayList<AtmTaskRecordHead>();
	    	for (int i = 0; i < atmTasks.size(); i++) {
	    		AtmTask task = atmTasks.get(i);
	    		AtmTaskRecordHead recordHead = new AtmTaskRecordHead();
	    		recordHead.setIndex(i+1);
	    		recordHead.setRouteNo(routeMap.get(task.getRouteId()));
	    		recordHead.setBankName(bankMap.get(task.getSubBankId()));
	    		recordHead.setTerNo(atmMap.get(task.getAtmId()));
	    		String content = "";
	    		switch (task.getTaskType()) {
				case 1:
					content = task.getRepairContent();
					break;
				case 2:
					content = "新增加钞任务" + task.getAmount().movePointLeft(5).intValue();		
					break;
				case 3:
					content = "新增清机任务";
					break;
				}
	    		recordHead.setRepairContent(content);
	    		recordHead.setEndTime(task.getEndTime() == 0 ? "" : DateTimeUtil.timeStampMs2Date(task.getEndTime(),null));
	    		taskRecords.add(recordHead);
	    	}
	    	
	    	taskRecords.sort((x, y) -> Integer.compare(Integer.parseInt(x.getRouteNo()), Integer.parseInt(y.getRouteNo())));
	    	taskRecords.forEach((entity) -> entity.setRouteNo(entity.getRouteNo() + "号线"));
	    	String title = bankMap.get(bankId) + "任务派遣单";
	    	try {
	    		AtmTaskRecordHead.setExcelPropertyTitle(title);
				EasyExcelUtil.exportPrintExcel(response,title,AtmTaskRecordHead.class,taskRecords,null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(-1,"下载任务派遣单出错");
			}
	  }
	  
	  
    
	  
	  @ApiOperation(value = "撤销任务ATM设备下拉选项")
	  @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long")
	  })
	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	  @GetMapping("/cancelOption")
	  public Result cancelOption(@RequestParam Long departmentId,@RequestParam Long taskDate){
		  
		List<Map<String,Object>> atmTaskOption = new ArrayList<>();
		
		//获取atm编号集合
	    List<Map<String, Object>> atmTaskList = atmTaskService.listMaps((QueryWrapper)Wrappers.query().select("id,atm_id as atmId,route_id as routeId,task_type as taskType,amount,comments")
	    		.eq("department_id", departmentId).eq("task_date", taskDate).eq("status_t", AtmTaskStatusEnum.CONFRIM.getValue()).eq("deleted", 0));
	    
	    if(atmTaskList!=null && atmTaskList.size()>0){
//		    Set<Long> bankIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("bankId").toString())).collect(Collectors.toSet());
//		    List<Bank> bankList = bankService.listByIds(bankIds);
//		    Map<Long, Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,bank->bank));
		    
		    Set<Long> routeIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("routeId").toString())).collect(Collectors.toSet());
		    List<Route> routeList = routeService.listByIds(routeIds);
		    Map<Long, Route> routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,route->route));
		    
		    Set<Long> atmIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("atmId").toString())).collect(Collectors.toSet());
		    List<AtmDevice> atmList = atmService.listByIds(atmIds);
		    Map<Long, AtmDevice> atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,atm->atm));
	    
		    atmTaskOption = atmTaskList.stream().map(atmTask->{
//		    	atmTask.put("bankName", bankMap.get(atmTask.get("bankId")).getShortName());
		    	atmTask.put("routeNo", routeMap.get(atmTask.get("routeId")).getRouteNo() + "号线");
		    	atmTask.put("terNo", atmMap.get(atmTask.get("atmId")).getTerNo());
		    	atmTask.remove("routeId");
//		    	atmTask.remove("bankId");
		    	atmTask.remove("atmId");
		    	return atmTask;
		    }).collect(Collectors.toList());
	    
	    }
	  	return Result.success(atmTaskOption);
	  }
	  
	    @OperateLog(value = "任务批量撤销", type=OperateType.EDIT)
	    @ApiOperation(value = "任务批量撤销")
	    @ApiImplicitParam(name = "ids", value = "任务id", required = true, dataType = "BatchIdsVO")
	    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	    @PostMapping("/batchCancel")
	    public Result batchCancel(@RequestBody BatchIdsVO ids){
	    	return atmTaskService.batchCancel(ids);
	    }
	    
	    
		  @ApiOperation(value = "分配任务ATM设备下拉选项")
		  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
		  @ApiImplicitParam(name = "id", value = "ATM任务id", required = true, dataType = "Long")
		  @GetMapping("/moveOption/{id}")
		  public Result moveOption(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
			List<Map<String,Object>> atmTaskOption = new ArrayList<>();
			
			//获取atm编号集合
		    List<Map<String, Object>> atmTaskList = atmTaskService.listMaps((QueryWrapper)Wrappers.query()
		    		.select("id,atm_id as atmId,task_type as taskType,amount,comments")
		    		.eq("carry_route_id", id).and(wrapper -> wrapper.eq("status_t", AtmTaskStatusEnum.CREATE.getValue())
		    				.or().eq("status_t", AtmTaskStatusEnum.CONFRIM.getValue())).eq("deleted", 0));
		    
		    if(atmTaskList!=null && atmTaskList.size()>0){
//			    Set<Long> bankIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("bankId").toString())).collect(Collectors.toSet());
//			    List<Bank> bankList = bankService.listByIds(bankIds);
//			    Map<Long, Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,bank->bank));
			    
			    Set<Long> atmIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("atmId").toString())).collect(Collectors.toSet());
			    List<AtmDevice> atmList = atmService.listByIds(atmIds);
			    Map<Long, AtmDevice> atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,atm->atm));
		    
			    atmTaskOption = atmTaskList.stream().map(atmTask->{
//			    	atmTask.put("bankName", bankMap.get(atmTask.get("bankId")).getShortName());
			    	atmTask.put("terNo", atmMap.get(atmTask.get("atmId")).getTerNo());
			    	atmTask.remove("routeId");
//			    	atmTask.remove("bankId");
			    	atmTask.remove("atmId");
			    	return atmTask;
			    }).collect(Collectors.toList());
		    
		    }
		  	return Result.success(atmTaskOption);
		  }
		  
	    @OperateLog(value = "任务分配", type=OperateType.EDIT)
	    @ApiOperation(value = "任务分配")
	    @ApiImplicitParam(name = "moveVO", value = "任务分配VO", required = true, dataType = "ATMTaskMoveVO")
	    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	    @PostMapping("/move")
	    public Result move(@RequestBody ATMTaskMoveVO moveVO){
	    	return atmTaskService.move(moveVO);
	    }
	    
	    @ApiOperation(value = "吞没卡线路ATM加钞任务")
	    @ApiImplicitParam(name = "routeId", value = "ATM任务id", required = true, dataType = "Long")
	    @GetMapping("/getRouteTaskList/{routeId}")
	    public Result getRouteTaskList(@PathVariable("routeId") @Min(value=1, message="routeId不能小于1") Long routeId){
			List<Map<String,Object>> atmTaskOption = new ArrayList<>();
			
			//获取atm编号集合
		    List<Map<String, Object>> atmTaskList = atmTaskService.listMaps((QueryWrapper)Wrappers.query()
		    		.select("id,atm_id as atmId,task_type as taskType,amount,comments")
		    		.eq("carry_route_id", routeId).ne("status_t", AtmTaskStatusEnum.CANCEL.getValue()).eq("deleted", 0));
		    
		    if(atmTaskList!=null && atmTaskList.size()>0){
			    Set<Long> atmIds = atmTaskList.stream().map(atmTask -> Long.parseLong(atmTask.get("atmId").toString())).collect(Collectors.toSet());
			    List<AtmDevice> atmList = atmService.listByIds(atmIds);
			    Map<Long, AtmDevice> atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,atm->atm));
		    
			    atmTaskOption = atmTaskList.stream().map(atmTask->{
			    	atmTask.put("terNo", atmMap.get(atmTask.get("atmId")).getTerNo());
			    	atmTask.remove("routeId");
			    	atmTask.remove("atmId");
			    	return atmTask;
			    }).collect(Collectors.toList());
		    
		    }
		  	return Result.success(atmTaskOption);
	    }
		  
}
