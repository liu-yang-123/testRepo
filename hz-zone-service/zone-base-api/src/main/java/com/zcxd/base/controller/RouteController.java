package com.zcxd.base.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.CashboxPackRecordDTO;
import com.zcxd.base.dto.RouteBoxBagChangeDTO;
import com.zcxd.base.dto.RouteDTO;
import com.zcxd.base.dto.RouteEmpChangeDTO;
import com.zcxd.base.dto.RouteLogDTO;
import com.zcxd.base.dto.RouteLogItem;
import com.zcxd.base.dto.RouteMonitorDTO;
import com.zcxd.base.dto.WxNoticeMessage;
import com.zcxd.base.service.ATMTaskService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.CashboxPackRecordService;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.RouteBoxBagChangeService;
import com.zcxd.base.service.RouteEmpChangeService;
import com.zcxd.base.service.RouteLogService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.service.RouteTemplateService;
import com.zcxd.base.service.VehicleService;
import com.zcxd.base.vo.BatchIdsVO;
import com.zcxd.base.vo.RouteHandoverVO;
import com.zcxd.base.vo.RouteVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.RouteHandoverChangeEnum;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.CashboxPackRecord;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.RouteBoxbagChange;
import com.zcxd.db.model.RouteEmpChange;
import com.zcxd.db.model.RouteLog;
import com.zcxd.db.model.RouteTemplate;
import com.zcxd.db.model.Vehicle;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName RouteController
 * @Description 线路管理控制器
 * @author 秦江南
 * @Date 2021年5月28日下午3:20:18
 */
@Slf4j
@RestController
@RequestMapping("/route")
@Api(tags="线路管理")
public class RouteController {

	@Value("${wechat.push}")
	private int wxPushNotice;

    @Autowired
    private RouteService routeService;
    
    @Autowired
    private ATMTaskService atmTaskService;
    
	@Autowired
    private CommonService commonService;
	
	@Autowired
    private BankService bankService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private RouteTemplateService routeTemplateService;
	
	@Autowired
	private RouteEmpChangeService routeEmpChangeService;
	@Autowired
	private RouteBoxBagChangeService routeBoxBagChangeService;
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private CashboxPackRecordService cashboxPackRecordService;

	@Autowired
	private RouteLogService routeLogService;
	
	@Autowired
	private DenomService denomService;

	@Autowired
	private RedisUtil redisUtil;

    @OperateLog(value = "添加线路", type=OperateType.ADD)
    @ApiOperation(value = "添加线路")
    @ApiImplicitParam(name = "routeVO", value = "线路", required = true, dataType = "RouteVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated RouteVO routeVO){
    	
    	if(routeVO.getTemplateType().equals(0)){
	    	if(routeVO.getRouteKeyMan().equals(routeVO.getRouteOperMan())){
	    		return Result.fail("业务员不能相同!");
	    	}
	    	
	    	if(routeVO.getSecurityA().equals(routeVO.getSecurityB())){
	    		return Result.fail("护卫A和护卫B不能相同!");
	    	}
    	}
    	
    	Route route = new Route();
    	
    	String routeNumber = DateTimeUtil.timeStampMs2Date(routeVO.getRouteDate(), "yyyyMMdd") + String.format("%03d", Integer.parseInt(routeVO.getRouteNo()));
    	route.setRouteNumber(routeNumber);
		List<Route> routeList = routeService.getRouteByCondition(route);
		if(routeList != null && routeList.size()>0)
			return Result.fail("该线路编号已存在，请重新填写!");
		
		BeanUtils.copyProperties(routeVO, route);
		route.setId(null);
		RouteTemplate routeTemplate = routeTemplateService.getOne((QueryWrapper)Wrappers.query().eq("route_no", route.getRouteNo()).eq("deleted", 0));
		route.setRouteType(0);
		if(routeTemplate == null){
			route.setRouteType(1);
		}
//		route.setPlanBeginTime(DateTimeUtil.convertDateToLong(DateTimeUtil.timeStampMs2Date(routeVO.getRouteDate(), "yyyy-MM-dd") + " " + routeVO.getPlanBeginTime() + ":00"));
//		route.setPlanFinishTime(DateTimeUtil.convertDateToLong(DateTimeUtil.timeStampMs2Date(routeVO.getRouteDate(), "yyyy-MM-dd") + " " + routeVO.getPlanFinishTime() + ":00"));
//		if(route.getPlanFinishTime() <= route.getPlanBeginTime()){
//			return Result.fail("线路计划时间填写有误");
//		}
		
		boolean save = routeService.save(route);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加线路失败");
    }
    
    @OperateLog(value = "复制线路", type=OperateType.ADD)
    @ApiOperation(value = "复制线路")
    @ApiImplicitParam(name = "routeVO", value = "线路", required = true, dataType = "RouteVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/copy")
    public Result copy(@RequestBody @Validated RouteVO routeVO){
    	if(routeVO.getTemplateType().equals(0)){
	    	if(routeVO.getRouteKeyMan().equals(routeVO.getRouteOperMan())){
	    		return Result.fail("业务员不能相同!");
	    	}
	    	
	    	if(routeVO.getSecurityA().equals(routeVO.getSecurityB())){
	    		return Result.fail("护卫A和护卫B不能相同!");
	    	}
    	}
    	
    	Route route = new Route();
    	
    	String routeNumber = DateTimeUtil.timeStampMs2Date(routeVO.getRouteDate(), "yyyyMMdd") + String.format("%03d", Integer.parseInt(routeVO.getRouteNo()));
    	route.setRouteNumber(routeNumber);
		List<Route> routeList = routeService.getRouteByCondition(route);
		if(routeList != null && routeList.size()>0)
			return Result.fail("该线路编号已存在，请重新填写!");
		
		BeanUtils.copyProperties(routeVO, route);
		route.setId(null);
		RouteTemplate routeTemplate = routeTemplateService.getOne((QueryWrapper)Wrappers.query().eq("route_no", route.getRouteNo()).eq("deleted", 0));
		route.setRouteType(0);
		if(routeTemplate == null){
			route.setRouteType(1);
		}
		
		boolean save = routeService.save(route);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加线路失败");
    }
    

    @OperateLog(value = "删除线路", type=OperateType.DELETE)
    @ApiOperation(value = "删除线路")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Route route = routeService.getById(id);
    	if(!route.getStatusT().equals(RouteStatusEnum.CREATE.getValue())){
    		return Result.fail("当前线路不是新建状态，无法删除");
    	}
    	
    	Map<String, Object> taskCountMap = atmTaskService.getMap((QueryWrapper)Wrappers.query()
  				.select("count(1) as count").and(wrapper -> wrapper.eq("route_id", route.getId()).or().eq("carry_route_id", route.getId())).eq("deleted", 0));
    	
    	if(Integer.parseInt(taskCountMap.get("count").toString()) > 0){
    		return Result.fail("线路含有任务，无法删除");
    	}
    	route.setId(id);
    	route.setDeleted(1);
    	boolean update = routeService.updateById(route);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除线路失败");
    }
    
    
    @OperateLog(value = "手动交接线路", type=OperateType.EDIT)
    @ApiOperation(value = "手动交接线路")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/over/{id}")
    public Result over(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Route route = routeService.getById(id);
    	if(route.getStatusT() < RouteStatusEnum.DISPATCH.getValue()){
    		return Result.fail("当前状态线路不能进行手动交接");
    	}
    	
    	route.setStatusT(RouteStatusEnum.FINISH.getValue());
    	route.setActFinishTime(System.currentTimeMillis());
    	route.setHdoverTime(System.currentTimeMillis());
    	boolean update = routeService.updateById(route);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除线路失败");
    }
    

    @OperateLog(value = "修改线路", type=OperateType.EDIT)
    @ApiOperation(value = "修改线路")
    @ApiImplicitParam(name = "routeVO", value = "线路", required = true, dataType = "RouteVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated RouteVO routeVO){
    	if(routeVO.getTemplateType().equals(0)){
	    	if(routeVO.getRouteKeyMan().equals(routeVO.getRouteOperMan())){
	    		return Result.fail("业务员不能相同!");
	    	}
	    	
	    	if(routeVO.getSecurityA().equals(routeVO.getSecurityB())){
	    		return Result.fail("护卫A和护卫B不能相同!");
	    	}
    	}
		
		if (routeVO.getId() == null) {
			return Result.fail("线路Id不能为空");
		}
		Route route = routeService.getById(routeVO.getId());
		if(route.getStatusT().equals(RouteStatusEnum.FINISH.getValue())){
			return Result.fail("当前线路已经交接完成，无法修改");
		}
		
		if(route.getStatusT().equals(RouteStatusEnum.CREATE.getValue())){
			String routeNumber = DateTimeUtil.timeStampMs2Date(routeVO.getRouteDate(), "yyyyMMdd") + String.format("%03d", Integer.parseInt(routeVO.getRouteNo()));
			route.setRouteNumber(routeNumber);
			List<Route> routeList = routeService.getRouteByCondition(route);
			if(routeList != null && routeList.size()>0) {
				if (!routeList.get(0).getId().equals(routeVO.getId())) {
					return Result.fail("该线路编号已存在，请重新填写！");
				}
			}
			BeanUtils.copyProperties(routeVO, route);

			boolean update = routeService.updateById(route);
			if(update) {
				return Result.success();
			}
			return Result.fail("修改线路失败");
		}else{
			Route routeOld = new Route();
			BeanUtils.copyProperties(route,routeOld);
			routeService.changeEmp(routeVO, route);
			//推送消息
			if (wxPushNotice == 1) {
				pushEmployeeChangeNotice(routeOld, routeVO);
			}
			return Result.success();
		}
		
    }
    
    @OperateLog(value = "信息变更", type=OperateType.EDIT)
    @ApiOperation(value = "信息变更")
    @ApiImplicitParam(name = "routeVO", value = "线路", required = true, dataType = "RouteVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/edit")
    public Result edit(@RequestBody @Validated RouteVO routeVO){
    	if(routeVO.getTemplateType().equals(0)){
	    	if(routeVO.getRouteKeyMan().equals(routeVO.getRouteOperMan())){
	    		return Result.fail("业务员不能相同!");
	    	}
	    	
	    	if(routeVO.getSecurityA().equals(routeVO.getSecurityB())){
	    		return Result.fail("护卫A和护卫B不能相同!");
	    	}
    	}
		
		if (routeVO.getId() == null) {
			return Result.fail("线路Id不能为空");
		}
		Route route = routeService.getById(routeVO.getId());
		if(route.getStatusT().equals(RouteStatusEnum.CREATE.getValue())){
			return Result.fail("线路未确认，请在编辑处修改线路信息");
		}
		
		Route routeOld = new Route();
		BeanUtils.copyProperties(route,routeOld);
		routeService.changeEmp(routeVO, route);
		//推送消息
		if (wxPushNotice == 1) {
			pushEmployeeChangeNotice(routeOld, routeVO);
		}
		return Result.success();
		
    }
    
    @OperateLog(value = "交接变更", type=OperateType.EDIT)
    @ApiOperation(value = "交接变更")
    @ApiImplicitParam(name = "routeHandoverVO", value = "交接信息", required = true, dataType = "RouteHandoverVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/editHandover")
    public Result editHandover(@RequestBody @Validated RouteHandoverVO routeHandoverVO){
		Route route = routeService.getById(routeHandoverVO.getId());
		if(!route.getStatusT().equals(RouteStatusEnum.FINISH.getValue())){
			return Result.fail("线路暂未交接，无法变更");
		}
		routeService.editHandover(routeHandoverVO, route);
		return Result.success();
		
    }

	@PostMapping("/findById")
	public Result<Route> findById(@RequestParam("id") Long id) {
		Route route = routeService.getById(id);
		return route == null ? Result.fail() : Result.success(route);
	}
    

    @ApiOperation(value = "查询线路列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeName", value = "线路名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = false, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam Integer page,
                                   @RequestParam Integer limit,
                                   @RequestParam(required=false) Integer routeType,
                                   @RequestParam(required=false) @Size(max=32,message="routeNo最大长度为32") String routeNo,
                                   @RequestParam(required=false) @Size(max=32,message="routeName最大长度为32") String routeName,
                                   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId,
                                   @RequestParam(required=false) Long routeDate){
		Route route = new Route();
		route.setRouteNo(routeNo);
		route.setRouteName(routeName);
		route.setRouteType(routeType);
		route.setDepartmentId(departmentId);
		route.setRouteDate(routeDate);
		IPage<Route> routePage = routeService.findListByPage(page, limit, route);

		Set<Long> userIds = routePage.getRecords().stream().map(t -> {
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
		List<RouteDTO> routeList = routePage.getRecords().stream().map(routeTmp -> {
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

		ResultList resultList = ResultList.builder().total(routePage.getTotal()).list(routeList).build();
		return Result.success(resultList);
    }
    
    @ApiOperation(value = "线路下拉框选项")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option(@RequestParam Long routeDate,
    		@RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId) {
    	Route route = new Route();
    	route.setRouteDate(routeDate);
    	route.setDepartmentId(departmentId);
        List<Route> routeList = routeService.getRouteByCondition(route);
        
        List<Map<String, Object>> options = new ArrayList<>(routeList.size());
        routeList.stream().forEach(routeTmp -> {
        	Map<String, Object> option = new HashMap<>(2);
            option.put("value", routeTmp.getId());
            option.put("routeNo", routeTmp.getRouteNo());
            option.put("routeName", routeTmp.getRouteName());
            option.put("type", routeTmp.getRouteType());
            option.put("status", routeTmp.getStatusT());
            options.add(option);
        });
        return Result.success(options);
    }

	/**
	 * 检查线路配置是否正确
	 * @param curRoute
	 * @return
	 */
	private String checkRouteResouceOverload(Route curRoute) {

		List<Route> routeList = routeService.findConfirmList(curRoute.getRouteDate());
		if (routeList.size() > 0) {
			for (Route route : routeList) {
				//检查车辆是否可以分配
				if (route.getVehicleId() == curRoute.getVehicleId().longValue()) {
					return "车辆与"+route.getRouteNo()+"号线重复";
				}
				//检查司机是否重复
				if (route.getDriver() == curRoute.getDriver().longValue()) {
					return "司机与"+route.getRouteNo()+"号线重复";
				}
				//检查业务员是否重复
				if (route.getRouteKeyMan() == curRoute.getRouteKeyMan().longValue()
				|| route.getRouteKeyMan() == curRoute.getRouteOperMan().longValue()) {
					Employee employee = employeeService.getById(route.getRouteKeyMan());
					if (employee != null) {
						return "业务员 "+employee.getEmpName()+"与"+ route.getRouteNo() + "号线重复";
					} else {
						return "业务员与"+ route.getRouteNo() + "号线重复";
					}
				}
				if (route.getRouteOperMan() == curRoute.getRouteKeyMan().longValue()
						|| route.getRouteOperMan() == curRoute.getRouteOperMan().longValue()) {
					Employee employee = employeeService.getById(route.getRouteOperMan());
					if (employee != null) {
						return "业务员 "+employee.getEmpName()+"与"+ route.getRouteNo() + "号线重复";
					} else {
						return "业务员与"+ route.getRouteNo() + "号线重复";
					}
				}

				//检查护卫是否重复
				if (route.getSecurityA() == curRoute.getSecurityA().longValue()
						|| route.getSecurityA() == curRoute.getSecurityB().longValue()) {
					Employee employee = employeeService.getById(route.getSecurityA());
					if (employee != null) {
						return "护卫 "+employee.getEmpName()+"与"+ route.getRouteNo() + "号线重复";
					} else {
						return "护卫与"+ route.getRouteNo() + "号线重复";
					}
				}
				if (route.getSecurityB() == curRoute.getSecurityB().longValue()
						|| route.getSecurityB() == curRoute.getSecurityA().longValue()) {
					Employee employee = employeeService.getById(route.getSecurityB());
					if (employee != null) {
						return "护卫 "+employee.getEmpName()+"与"+ route.getRouteNo() + "号线重复";
					} else {
						return "护卫与"+ route.getRouteNo() + "号线重复";
					}
				}
			}
		}
		return null;
	}

    @OperateLog(value = "线路确认", type=OperateType.EDIT)
    @ApiOperation(value = "线路确认")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/confirm/{id}")
    public Result confirm(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Route route = routeService.getById(id);
    	if(!route.getStatusT().equals(RouteStatusEnum.CREATE.getValue())){
    		return Result.fail("当前线路不是新建状态，无法确认");
    	}
    	
    	if(route.getVehicleId() == 0){
    		return Result.fail("请先分配车辆，再进行确认");
    	}
    	
    	if(route.getDriver() == 0){
    		return Result.fail("请先分配司机，再进行确认");
    	}
    	
    	if(route.getTemplateType().equals(0)){
        	if(route.getRouteKeyMan() == 0){
        		return Result.fail("请先分配钥匙员，再进行确认");
        	}
        	
        	if(route.getRouteOperMan() == 0){
        		return Result.fail("请先分配密码员，再进行确认");
        	}
        	
        	if(route.getSecurityA() == 0){
        		return Result.fail("请先分配护卫A，再进行确认");
        	}
        	
        	if(route.getSecurityB() == 0){
        		return Result.fail("请先分配护卫B，再进行确认");
        	}
    	}
//
//    	String errMsg = checkRouteResouceOverload(route);
//    	if (null != errMsg) {
//			return Result.fail(errMsg);
//		}
    	
    	route.setId(id);
    	route.setStatusT(RouteStatusEnum.CHECKED.getValue());
    	boolean update = routeService.updateById(route);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("线路确认失败");
    }
    
    @ApiOperation(value = "清机任务")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/atmTask/{id}")
    public Result atmTask(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<Map<String, Object>> findList = atmTaskService.findList(id,null,1);
    	if(findList != null && findList.size() > 0){
	    	Set<Long> bankIds = findList.stream().map(map-> Long.parseLong(map.get("bankId").toString())).collect(Collectors.toSet());
	    	List<Bank> bankList = bankService.listByIds(bankIds);
	    	Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
	    	
	    	findList.stream().forEach(map->{
	    		map.put("amount", new BigDecimal(map.get("amount").toString()).movePointLeft(5));
	    		map.put("headBank", bankCleanMap.get(map.get("bankId")));
	    	});
    	}
        return Result.success(findList);
    }
    
    
    
    @ApiOperation(value = "线路监控")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeType", value = "线路类型", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeName", value = "线路名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "routeDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/monitor")
    public Result monitor(@RequestParam(required=false) Integer routeType,
                                   @RequestParam(required=false) @Size(max=32,message="routeNo最大长度为32") String routeNo,
                                   @RequestParam(required=false) @Size(max=32,message="routeName最大长度为32") String routeName,
                                   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId,
                                   @RequestParam Long routeDate){
    	Route route = new Route();
    	route.setRouteNo(routeNo);
    	route.setRouteName(routeName);
    	route.setRouteType(routeType);
    	route.setDepartmentId(departmentId);
    	route.setRouteDate(routeDate);
    	List<Route> routeList = routeService.getRouteByCondition(route);

    	Map<String, Object> vehicleMap = commonService.getVehicleMap();
    	
    	List<Map<String,Object>> atmTaskList = atmTaskService.listMaps((QueryWrapper)Wrappers.query().select("id,carry_route_id as carryRouteId,status_t as status")
    			.eq("task_date", routeDate).ne("status_t", AtmTaskStatusEnum.CANCEL.getValue()).eq("deleted", 0));
    	
    	Map<String,Integer> taskCountMap = new HashMap<>();
    	atmTaskList.stream().forEach(atmTask -> {
    		if(!taskCountMap.containsKey(atmTask.get("carryRouteId") + "A")){
    			taskCountMap.put(atmTask.get("carryRouteId") + "A",0);
    		}
    		
    		if(!taskCountMap.containsKey(atmTask.get("carryRouteId") + "B")){
    			taskCountMap.put(atmTask.get("carryRouteId") + "B",0);
    		}
    		
    		taskCountMap.put(atmTask.get("carryRouteId") + "A",taskCountMap.get(atmTask.get("carryRouteId") + "A")+ 1);
    		
    		if(atmTask.get("status").toString().equals(AtmTaskStatusEnum.FINISH.getValue()+"")){
    			taskCountMap.put(atmTask.get("carryRouteId") + "B", taskCountMap.get(atmTask.get("carryRouteId") + "B")+1);
    		}
    	});
    	
    	List<RouteMonitorDTO> routeMonitorList = routeList.stream().map(routeTmp -> {
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
    
    @ApiOperation(value = "查询人员调整记录")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/empChange/{id}")
    public Result empChange(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<RouteEmpChange> empChanges = routeEmpChangeService.list((QueryWrapper)Wrappers.query().eq("route_id", id).orderByAsc("create_time"));
    	Set<Long> userIds = new HashSet<>();
    	empChanges.stream().filter(t->!t.getJobType().equals(-1)).forEach(empChange -> {
    		userIds.add(empChange.getOldManId());
    		userIds.add(empChange.getNewManId());
    	});
    	
    	Set<Long> vehicleIds = new HashSet<>();
    	empChanges.stream().filter(t->t.getJobType().equals(-1)).forEach(empChange -> {
    		vehicleIds.add(empChange.getOldManId());
    		vehicleIds.add(empChange.getNewManId());
    	});
    	
		Map<Long,String> employeeMap = new HashMap<>();
		if(userIds.size() != 0){
			List<Employee> employeeList = employeeService.listByIds(userIds);
			employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
		}
		
		Map<Long,String> vehicleMap = new HashMap<>();
		if(vehicleIds.size() != 0){
			List<Vehicle> vehicleList = vehicleService.listByIds(vehicleIds);
			vehicleMap = vehicleList.stream().collect(Collectors.toMap(Vehicle::getId,Vehicle::getLpno));
		}
		
		Map<Long, String> finalEmployeeMap = employeeMap;
		Map<Long, String> finalVehicleMap = vehicleMap;
		List<RouteEmpChangeDTO> empChangeDTOs = empChanges.stream().map(empChang -> {
			RouteEmpChangeDTO empChangeDTO = new RouteEmpChangeDTO();
			BeanUtils.copyProperties(empChang, empChangeDTO);
			if(!empChang.getJobType().equals(-1)){
				empChangeDTO.setOldManName(finalEmployeeMap.get(empChang.getOldManId()));
				empChangeDTO.setNewManName(finalEmployeeMap.get(empChang.getNewManId()));
			}else{
				empChangeDTO.setOldManName(finalVehicleMap.get(empChang.getOldManId()));
				empChangeDTO.setNewManName(finalVehicleMap.get(empChang.getNewManId()));
			}
			return empChangeDTO;
		}).collect(Collectors.toList());
    	
    	return Result.success(empChangeDTOs);
    }
    
    @ApiOperation(value = "查询交接调整记录")
    @ApiImplicitParam(name = "id", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/handoverChange/{id}")
    public Result handoverChange(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<RouteBoxbagChange> boxBagChanges = routeBoxBagChangeService.list((QueryWrapper)Wrappers.query().eq("route_id", id).orderByAsc("create_time"));

		List<RouteBoxBagChangeDTO> boxBagChangeDTOs = boxBagChanges.stream().map(boxBagChange -> {
			RouteBoxBagChangeDTO boxBagChangeDTO = new RouteBoxBagChangeDTO();
			BeanUtils.copyProperties(boxBagChange, boxBagChangeDTO);
			if(boxBagChange.getChangeType().equals(RouteHandoverChangeEnum.BOX.getValue())){
				boxBagChangeDTO.setChangeType(RouteHandoverChangeEnum.BOX.getText());
			}else if(boxBagChange.getChangeType().equals(RouteHandoverChangeEnum.BAG.getValue())){
				boxBagChangeDTO.setChangeType(RouteHandoverChangeEnum.BAG.getText());
			}
			return boxBagChangeDTO;
		}).collect(Collectors.toList());
    	
    	return Result.success(boxBagChangeDTOs);
    }

	//推送排班通知
	//只有确认过的线路才能发送排班通知
	private void setShcdNotice(List<Long> routeIds) {
		final String redisKey = CacheKeysDef.Scheduling;
		if(redisUtil.hasKey(redisKey)) {
			redisUtil.delete(redisKey);
		}
		List<String> dataList = new ArrayList<>();
		for (Long id : routeIds) {
			dataList.add(id.toString());
		}
		redisUtil.lLeftPushAll(redisKey,dataList);
//		redisUtil.expire(redisKey,5,TimeUnit.MINUTES);
	}

	@OperateLog(value="发送排班通知",type= OperateType.ADD)
	@ApiOperation(value = "发送排班通知")
	@PostMapping({"/push/notice"})
	public Result pushSchdNotice(@RequestBody BatchIdsVO batchIdsVO){
    	if (batchIdsVO.getIds() == null || batchIdsVO.getIds().size() == 0) {
    		return Result.fail("请选择线路");
		}
    	if (wxPushNotice == 1) {
			setShcdNotice(batchIdsVO.getIds());
		}
		return Result.success();
	}

	public List<WxNoticeMessage> getChangeMessage(RouteVO routeVO, Route route) {

		List<WxNoticeMessage> messages = new ArrayList<>();
		Vehicle vehicle = vehicleService.getById(route.getVehicleId());
		String vehicleNo = vehicle == null? "" : vehicle.getLpno();
		String routeName = route.getRouteNo() + "号线";
		String dateStr = DateTimeUtil.timeStampMs2Date(route.getRouteDate(),"yyyy-MM-dd");
		//修改司机
		if(!route.getDriver().equals(routeVO.getDriver())){
			WxNoticeMessage message = new WxNoticeMessage();
			message.setTemplate(1);
			message.setVehicleNo(vehicleNo);
			message.setRouteDate(dateStr);

			Set<Long> ids = new HashSet<>();
			ids.add(route.getDriver());
			ids.add(routeVO.getDriver());
			List<Employee> employees = employeeService.listByIds(ids);
			if (employees.size() > 0) {
				Map<Long,String> map = employees.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName,(key1,key2)->key2));
				String change = map.getOrDefault(route.getDriver(),"") + " -> "+map.getOrDefault(routeVO.getDriver(),"");
				String empNames =  map.getOrDefault(routeVO.getDriver(),"");
				message.setChangeText(change);
				message.setEmpNames(empNames);
			}
			message.setEmpIds(ids);
			message.setRouteNo(routeName);
			message.setComments("司机变更");
			messages.add(message);
		}

		//修改护卫
		if(!route.getSecurityA().equals(routeVO.getSecurityA())
		|| !route.getSecurityB().equals(routeVO.getSecurityB())){
			WxNoticeMessage message = new WxNoticeMessage();
			message.setTemplate(1);
			message.setVehicleNo(vehicleNo);
			message.setRouteDate(dateStr);
			Set<Long> ids = new HashSet<>();
			ids.add(routeVO.getSecurityA());
			ids.add(route.getSecurityA());
			ids.add(routeVO.getSecurityB());
			ids.add(route.getSecurityB());
			List<Employee> employees = employeeService.listByIds(ids);
			Map<Long,String> map = employees.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName,(key1,key2)->key2));
			String changeText = "";
			if (!route.getSecurityA().equals(routeVO.getSecurityA())){
				changeText += map.getOrDefault(route.getSecurityA(),"") + "->"+map.getOrDefault(routeVO.getSecurityA(),"");
				changeText += ",";
			}

			if (!route.getSecurityB().equals(routeVO.getSecurityB())){
				changeText += map.getOrDefault(route.getSecurityB(),"") + "->"+map.getOrDefault(routeVO.getSecurityB(),"");
			}
			String empNames = map.getOrDefault(routeVO.getSecurityA(),"") + "," + map.getOrDefault(routeVO.getSecurityB(),"");
			message.setEmpNames(empNames);
			message.setChangeText(changeText);
			message.setEmpIds(ids);
			message.setRouteNo(routeName);
			message.setComments("");
			messages.add(message);
		}

		if(!route.getRouteKeyMan().equals(routeVO.getRouteKeyMan())
				|| !route.getRouteOperMan().equals(routeVO.getRouteOperMan())){
			WxNoticeMessage message = new WxNoticeMessage();
			message.setTemplate(1);
			message.setVehicleNo(vehicleNo);
			message.setRouteDate(dateStr);
			Set<Long> ids = new HashSet<>();
			ids.add(routeVO.getRouteKeyMan());
			ids.add(route.getRouteKeyMan());
			ids.add(routeVO.getRouteOperMan());
			ids.add(route.getRouteOperMan());
			List<Employee> employees = employeeService.listByIds(ids);
			Map<Long,String> map = employees.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName,(key1,key2)->key2));

			String empNames = map.getOrDefault(routeVO.getRouteKeyMan(),"") + "," + map.getOrDefault(routeVO.getRouteOperMan(),"");
			message.setEmpNames(empNames);

			String changeText = "";
			if (!route.getRouteKeyMan().equals(routeVO.getRouteKeyMan())){
				changeText += map.getOrDefault(route.getRouteKeyMan(),"") + "->"+map.getOrDefault(routeVO.getRouteKeyMan(),"");
				changeText += ",";
			}

			if (!route.getRouteOperMan().equals(routeVO.getRouteOperMan())){
				changeText += map.getOrDefault(route.getRouteOperMan(),"") + "->"+map.getOrDefault(routeVO.getRouteOperMan(),"");
			}
			message.setChangeText(changeText);
			message.setEmpIds(ids);
			message.setRouteNo(routeName);
			message.setComments("");
			messages.add(message);
		}
		return messages;
	}


	//推送人员变动通知
	private void pushEmployeeChangeNotice(Route route,RouteVO routeVO) {
		List<WxNoticeMessage> wxNoticeMessages = this.getChangeMessage(routeVO,route);
		if (wxNoticeMessages != null) {
			final String redisKey = CacheKeysDef.SchdChangeQueue;
			for (WxNoticeMessage message : wxNoticeMessages) {
				String data = JSON.toJSONString(message);
				log.info("notice : " + data);
				redisUtil.lLeftPush(redisKey,data);
			}
		}
//		String msg = redisUtil.lRightPop(CacheKeysDef.SchdChangeQueue);
//		if (!StringUtils.isEmpty(msg)) {
//			log.info("notice send : " + msg);
//		}
	}

	@ApiOperation(value = "查询车长日志")
	@PostMapping({"/leaderLog"})
	public Result getLeaderLog(@RequestParam(name = "routeId") Long routeId){

		RouteLog routeLog = routeLogService.getByRouteId(routeId);
		if (null == routeLog) {
			return Result.fail();
		}
		RouteLogDTO routeLogDTO = new RouteLogDTO();
		routeLogDTO.setComments(routeLog.getComments());

		List<RouteLogItem> items = new ArrayList<>();
		Object objectList = JSONObject.parse(routeLog.getDetail());
		if (objectList instanceof java.util.List) {
			for(Object object : (List)objectList) {
//				if (object instanceof RouteLogItem) {
//					items.add((RouteLogItem)object);
//				}
				JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
				if (jsonObject.containsKey("chk") && jsonObject.containsKey("code") && jsonObject.containsKey("cmt")) {
					RouteLogItem item = new RouteLogItem();
					item.setChk(jsonObject.getIntValue("chk"));
					item.setCode(jsonObject.getString("code"));
					item.setCmt(jsonObject.getString("cmt"));
					items.add(item);
				}
			}
		}
		routeLogDTO.setDetails(items);
		if (routeLog.getLeader() != null) {
			Employee employee = employeeService.getById(routeLog.getLeader());
			if (null != employee) {
				routeLogDTO.setLeaderName(employee.getEmpName());
			}
		}
		return Result.success(routeLogDTO);
	}
	
	@ApiOperation(value = "查询配钞详情")
	@PostMapping({"/dispatch"})
	public Result dispatchInfo(@RequestParam(name = "routeId") Long routeId){
		List<CashboxPackRecord> cashboxPackPage = cashboxPackRecordService.listByRoute(routeId);
		
    	Set<Long> denomIds = new HashSet<>();
    	Set<Long> bankIds = new HashSet<>();
    	
    	
    	cashboxPackPage.stream().forEach(cashboxPackTmp->{
    		denomIds.add(cashboxPackTmp.getDenomId());
    		bankIds.add(cashboxPackTmp.getBankId());
    	});
    	
    	List<Denom> denomList = new ArrayList<>();
    	if(denomIds.size()>0){
    		denomList = denomService.listByIds(denomIds);
    	}
    	Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
    	
    	List<Bank> bankList = new ArrayList<>();
    	if(bankIds.size()>0){
    		bankList = bankService.listByIds(bankIds);
    	}
    	Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));

		
 		List<CashboxPackRecordDTO> cashboxPackRecordDTOList = cashboxPackPage.stream().map(cashboxPackTmp->{
 	    	CashboxPackRecordDTO packRecordDTO = new CashboxPackRecordDTO();
 	    	packRecordDTO.setBoxNo(cashboxPackTmp.getBoxNo());
 	    	packRecordDTO.setDenomName(denomMap.get(cashboxPackTmp.getDenomId()));
 	    	packRecordDTO.setAmount(cashboxPackTmp.getAmount());
 	    	packRecordDTO.setStatusT(cashboxPackTmp.getStatusT());
 	    	packRecordDTO.setBankName(bankMap.get(cashboxPackTmp.getBankId()));
 	    	return packRecordDTO;
 		}).collect(Collectors.toList());
		return Result.success(cashboxPackRecordDTOList);
	}
	
}
