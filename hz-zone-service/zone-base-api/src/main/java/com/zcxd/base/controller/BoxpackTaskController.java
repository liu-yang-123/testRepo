package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.BoxpackDTO;
import com.zcxd.base.dto.BoxpackTaskDTO;
import com.zcxd.base.dto.BoxpackTaskInfoDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.BankTellerService;
import com.zcxd.base.service.BoxpackService;
import com.zcxd.base.service.BoxpackTaskService;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.service.VehicleService;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Boxpack;
import com.zcxd.db.model.BoxpackTask;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.Vehicle;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Api(tags = "早送晚收任务")
@RequestMapping("/boxpackTask")
@RestController
public class BoxpackTaskController {
    @Autowired
    private BoxpackTaskService boxpackTaskService;
    
    @Autowired
    private BoxpackService boxpackService;
    
    @Autowired
    private BankService bankService;
    
    @Autowired
    private BankTellerService bankTellerService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private VehicleService vehicleService;
    
	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
	private CommonService commonService;
    
    @ApiOperation(value = "任务列表")
	  @ApiImplicitParams({
	  	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
	      @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
	      @ApiImplicitParam(name = "bankId", value = "网点id", required = true, dataType = "Long"),
	      @ApiImplicitParam(name = "taskType", value = "任务类型", required = false, dataType = "Integer"),
	      @ApiImplicitParam(name = "statusT", value = "任务状态", required = false, dataType = "Integer"),
	      @ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long"),
	      @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
	  })
	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	  @GetMapping("/list")
	  public Result findListByPage(@RequestParam Integer page,
	                                 @RequestParam Integer limit,
	                                 @RequestParam Long bankId,
	                                 @RequestParam(required=false) Integer taskType,
	                                 @RequestParam(required=false) Integer statusT,
	                                 @RequestParam(required=false) Long taskDate,
	                                 @RequestParam(required=false) String routeNo){
		  
		BoxpackTask boxpackTask = new BoxpackTask();
		boxpackTask.setBankId(bankId);
		boxpackTask.setTaskType(taskType);
		boxpackTask.setStatusT(statusT);
		boxpackTask.setTaskDate(taskDate);
		boxpackTask.setRouteNo(routeNo);
	  	IPage<BoxpackTask> boxpackTaskPage = boxpackTaskService.findListByPage(page, limit, boxpackTask);
	  	
	  	List<BoxpackTaskDTO> boxpackTaskList = new ArrayList<BoxpackTaskDTO>();
	  	if(boxpackTaskPage.getTotal() > 0){
	  		Set<Long> bankIds = boxpackTaskPage.getRecords().stream().map(BoxpackTask::getBankId).collect(Collectors.toSet());
	  		Set<Long> routeIds = boxpackTaskPage.getRecords().stream().map(BoxpackTask::getRouteId).collect(Collectors.toSet());
	  		routeIds.remove(0L);
	    	List<Bank> bankList = bankService.listByIds(bankIds);
	    	Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId, Bank -> Bank));
		  	
	    	Map<Long,Route> routeMap = new HashMap<Long,Route>();
	    	if(routeIds.size()>0){
	    		List<Route> routeList = routeService.listByIds(routeIds);
	    		routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, Route -> Route));
	    	}
	    	
	    	Vehicle vehicle = new Vehicle();
	    	vehicle.setDepartmentId(boxpackTaskPage.getRecords().get(0).getDepartmentId());
	        List<Vehicle> vehicleList = vehicleService.getVehicleByCondition(vehicle);
	        Map<Long,Vehicle> vehicleMap = vehicleList.stream().collect(Collectors.toMap(Vehicle::getId, Vehicle -> Vehicle));
	        
	        final Map<Long,Route> routeMapFinal = routeMap;
	    	boxpackTaskList = boxpackTaskPage.getRecords().stream().map(boxpackTaskTmp -> {
	    		BoxpackTaskDTO boxpackTaskDTO = new BoxpackTaskDTO();
		  		BeanUtils.copyProperties(boxpackTaskTmp, boxpackTaskDTO);
		  		boxpackTaskDTO.setBankName(bankMap.get(boxpackTaskTmp.getBankId()).getShortName());
		  		boxpackTaskDTO.setLpno("");
		  		if(boxpackTaskTmp.getRouteId() != 0L){
		  			Vehicle vehicleTmp = vehicleMap.get(routeMapFinal.get(boxpackTaskTmp.getRouteId()).getVehicleId().longValue());
		  			if(vehicleTmp != null){
		  				boxpackTaskDTO.setLpno(vehicleTmp.getLpno());
		  			}
		  		}
		  		return boxpackTaskDTO;
		  	}).collect(Collectors.toList());
	  }
	  ResultList resultList = new ResultList.Builder().total(boxpackTaskPage.getTotal()).list(boxpackTaskList).build();
	  return Result.success(resultList);
    }

	/**
	 * 获取
	 * @param id 箱包ID
	 * @return 查询结果
	 */
	@PostMapping("/findById")
	public Result<BoxpackTask> findById(@RequestParam("id") Long id){
		BoxpackTask boxpackTask = boxpackTaskService.getById(id);
		if (boxpackTask.getDeleted() == 1) {
			boxpackTask = null;
		}
		return boxpackTask == null ? Result.fail("无指定箱包") : Result.success(boxpackTask);
	}


	@ApiOperation(value = "查询任务详情")
    @ApiImplicitParam(name = "id", value = "任务id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	BoxpackTaskInfoDTO infoDTO = new BoxpackTaskInfoDTO();
    	BoxpackTask boxpackTask = boxpackTaskService.getById(id);
//  		BeanUtils.copyProperties(boxpackTask, infoDTO);
//  		infoDTO.setBankName(bankService.getById(boxpackTask.getBankId()).getShortName());
//  		infoDTO.setLpno("");
    	infoDTO.setKeyManPhoto("");
    	infoDTO.setOperManPhoto("");
  		if(boxpackTask.getRouteId() != 0L){
  			Route route = routeService.getById(boxpackTask.getRouteId());
//  			Vehicle vehicleTmp = vehicleService.getById(route.getVehicleId().longValue());
//  			if(vehicleTmp != null){
//  				infoDTO.setLpno(vehicleTmp.getLpno());
//  			}
//  			
  			Set<Long> empIds = new HashSet<>();
  			empIds.add(route.getRouteKeyMan());
  			empIds.add(route.getRouteOperMan());
  			empIds.remove(0L);
  			
  			Map<Long,Employee> employeeMap = new HashMap<Long,Employee>();
	    	if(empIds.size()>0){
	    		List<Employee> employeeList = employeeService.listByIds(empIds);
	    		employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee -> Employee));
	    	}
	    	Employee keyMan = employeeMap.get(route.getRouteKeyMan());
	    	Employee OperMan = employeeMap.get(route.getRouteOperMan());
	    	if(keyMan != null && !StringUtils.isBlank(keyMan.getPhotoUrl())){
	    		infoDTO.setKeyManPhoto(commonService.showImg(keyMan.getPhotoUrl()));
	    	}
	    	
	    	if(OperMan != null && !StringUtils.isBlank(OperMan.getPhotoUrl())){
	    		infoDTO.setOperManPhoto(commonService.showImg(OperMan.getPhotoUrl()));
	    	}
  		}
  		
  		infoDTO.setHandOpMansName(boxpackTask.getHandOpMansName());
  		infoDTO.setHandTime(boxpackTask.getHandTime());
  		infoDTO.setCreateUserName(boxpackTask.getCreateUser() == 0 
  				? "" : bankTellerService.getById(boxpackTask.getCreateUser()).getTellerName());
  		
  		
    	String[] boxs= boxpackTask.getBoxList().split(",");
		Set<String> boxSet = new HashSet<>(Arrays.asList(boxs));
		List<Boxpack> boxpackList = boxpackService.listByIds(boxSet);
		
		List<BoxpackDTO> boxpackDTOs = new ArrayList<>();
		boxpackList.stream().forEach(boxpack -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			boxpackDTO.setId(boxpack.getId());
			boxpackDTO.setBoxNo(boxpack.getBoxNo());
			boxpackDTO.setBoxName(boxpack.getBoxName());
			boxpackDTOs.add(boxpackDTO);
		});
		
		boxpackDTOs.sort((x, y) -> x.getBoxNo().compareTo(y.getBoxNo()));
		infoDTO.setBoxpackList(boxpackDTOs);
        return Result.success(infoDTO);
    }
    
    
}
