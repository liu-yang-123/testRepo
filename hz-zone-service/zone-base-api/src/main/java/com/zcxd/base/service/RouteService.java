package com.zcxd.base.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.RouteHandoverVO;
import com.zcxd.base.vo.RouteVO;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.constant.RouteHandoverChangeEnum;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.db.mapper.DepartmentMapper;
import com.zcxd.db.mapper.RouteMapper;
import com.zcxd.db.mapper.RouteTemplateMapper;
import com.zcxd.db.mapper.RouteTemplateRecordMapper;
import com.zcxd.db.mapper.SchdResultMapper;
import com.zcxd.db.mapper.VehicleMapper;
import com.zcxd.db.model.Department;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.RouteBoxbagChange;
import com.zcxd.db.model.RouteEmpChange;
import com.zcxd.db.model.RouteTemplate;
import com.zcxd.db.model.RouteTemplateRecord;
import com.zcxd.db.model.SchdResult;
import com.zcxd.db.model.Vehicle;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName RouteService
 * @Description 线路管理服务类
 * @author 秦江南
 * @Date 2021年5月28日下午3:20:48
 */
@Slf4j
@AllArgsConstructor
@Service
public class RouteService extends ServiceImpl<RouteMapper, Route> {

	private RouteTemplateMapper templateMapper;

	private SchdResultMapper resultMapper;

	private VehicleMapper vehicleMapper;

	private RouteTemplateService templateService;

	private RouteTemplateRecordMapper recordMapper;

	private DepartmentMapper departmentMapper;
	
	private RouteEmpChangeService routeEmpChangeService;
	
	private RouteBoxBagChangeService routeBoxBagChangeService;
	/**
	 * 
	 * @Title getRouteByCondition
	 * @Description 根据条件查询线路列表
	 * @param route
	 * @return
	 * @return 返回类型 List<Route>
	 */
	public List<Route> getRouteByCondition(Route route) {
		QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
		if(route != null){
			if(!StringUtils.isBlank(route.getRouteNumber())){
				queryWrapper.eq("route_number", route.getRouteNumber());
			}
			if(route.getDepartmentId() != null ){
				queryWrapper.eq("department_id", route.getDepartmentId());
			}
			if(route.getRouteDate() != null){
				queryWrapper.eq("route_date", route.getRouteDate());
			}
			if(!StringUtils.isBlank(route.getRouteNo())){
				queryWrapper.eq("route_no", route.getRouteNo());
			}
			if(route.getRouteType() != null){
				queryWrapper.eq("route_type", route.getRouteType());
			}
		}
		queryWrapper.eq("deleted", 0);
//		queryWrapper.orderBy(true,false,"create_time");
		queryWrapper.orderBy(true,true,"route_no * 1");
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询线路列表
	 * @param page
	 * @param limit
	 * @param route
	 * @return
	 * @return 返回类型 IPage<Route>
	 */
	public IPage<Route> findListByPage(Integer page, Integer limit, Route route) {
		Page<Route> ipage=new Page<Route>(page,limit);
    	QueryWrapper<Route> queryWrapper=new QueryWrapper<Route>();
    	if(route != null){
	    	if(!StringUtils.isEmpty(route.getRouteNo())){
	    		queryWrapper.eq("route_no", route.getRouteNo());
	    	}
	    	if(!StringUtils.isEmpty(route.getRouteName())){
	    		queryWrapper.eq("route_name", route.getRouteName());
	    	}
	    	if(route.getDepartmentId() != null ){
	    		queryWrapper.eq("department_id", route.getDepartmentId());
	    	}
	    	if(route.getRouteDate() != null){
	    		queryWrapper.eq("route_date", route.getRouteDate());
	    	}
			if(route.getRouteType() != null){
				queryWrapper.eq("route_type", route.getRouteType());
			}
    	}
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"route_date");
    	queryWrapper.orderBy(true,true,"route_no * 1");
        return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * 初始化当天线路任务
	 * @return
	 */
//	@PostConstruct
	public boolean initRoute(){
		//获取第二天时间戳
		LocalDate localDate = LocalDate.now().plusDays(1L);
		long time = LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		//初始化部门数据
		QueryWrapper wrapper = Wrappers.query().eq("parent_ids","/0/").eq("deleted",0);
		List<Department> departmentList = departmentMapper.selectList(wrapper);
		if (departmentList == null || departmentList.size() == 0){
			return false;
		}
        //根据部门ID进行线路生成
		departmentList.stream().forEach(r -> {
			List<Route> routeList = getNewRouteList(time, r.getId());
			boolean b = saveTemplateResult(routeList,time, r.getId());
			log.info("定时线路任务执行结果:"+b);
		});
		return true;
	}

	/**
	 * 执行线路任务操作
	 * @param departmentId
	 * @return
	 */
	public boolean executeRoute(long departmentId){
		LocalDate localDate = LocalDate.now();
		long time = LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();

		//检查当天是否已经执行
		QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId).eq("route_date",time);
		RouteTemplateRecord templateRecord = recordMapper.selectOne(wrapper);
		if (templateRecord != null && templateRecord.getState() == 1){
			throw new BusinessException(1,"初始化数据任务线路已经生成");
		}
		List<Route>  routeList = getNewRouteList(time,departmentId);
		return saveTemplateResult(routeList,time, departmentId);
	}

	/**
	 * 获取线路末班中为生成线路数据
	 * @param time  当天生成线路的时间戳
 	 * @param departmentId 部门ID
	 * @return
	 */
	public List<Route> getNewRouteList(long time, long departmentId){
		LocalDate localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneOffset.of("+8")).toLocalDate();
		String currentDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//查询当天的线路数据
		QueryWrapper routeWrapper = Wrappers.query().eq("route_date",time).eq("department_id",departmentId).eq("deleted",0);
		List<Route> routeList = this.baseMapper.selectList(routeWrapper);
		//查询模板数据
		QueryWrapper templateWrapper = Wrappers.query().eq("deleted",0).eq("department_id",departmentId);
		List<RouteTemplate> templateList = templateMapper.selectList(templateWrapper);
		//过滤特殊线路
		List<RouteTemplate> excludeRouteList = templateService.getExcludeRoute(departmentId,time);

		Set<Long> excludeRouteIds = excludeRouteList.stream().map(RouteTemplate::getId).collect(Collectors.toSet());
		List<RouteTemplate> existRouteList = templateList.stream().filter(s -> !excludeRouteIds.contains(s.getId())).collect(Collectors.toList());
		//获取未初始化的线路编号数据
		List<RouteTemplate> newTemplateList = existRouteList.stream().filter(r -> {
			Optional<Route> routeOptional = routeList.stream().filter(t -> t.getRouteNo().equals(r.getRouteNo())).findFirst();
			return !routeOptional.isPresent();
		}).collect(Collectors.toList());

		//获取车辆数据
		QueryWrapper vehicleWrapper = Wrappers.query().eq("department_id",departmentId);
		List<Vehicle> vehicleList = vehicleMapper.selectList(vehicleWrapper);
		Map<String,Long> vehicleMap = vehicleList.stream().collect(Collectors.toMap(Vehicle::getLpno,Vehicle::getId));

		//获取排班任务数据
		QueryWrapper resultWrapper = Wrappers.query().eq("plan_day",time).eq("deleted",0);
		List<SchdResult> resultList = resultMapper.selectList(resultWrapper);
		String date = currentDate.replaceAll("-","");

		return newTemplateList.stream().map(item -> {
			//线路编号
			String routeNo = item.getRouteNo();
			Optional<SchdResult> resultOptional = resultList.stream().filter(t -> t.getRouteNo().equals(routeNo)).findFirst();
			SchdResult schdResult = resultOptional.orElseGet(SchdResult::new);
			//实例化对象
			Route route = new Route();
			route.setRouteNo(routeNo);
			route.setRouteName(item.getRouteName());
			String number = String.format("%03d", Integer.parseInt(item.getRouteNo()));
			route.setRouteNumber(date+number);
			//线路类型
			route.setTemplateType(item.getRouteType());
			//固定线路
			route.setRouteType(0);
			route.setDepartmentId(item.getDepartmentId());
			long vehicleId =Optional.ofNullable(vehicleMap.get(schdResult.getVehicleNo())).orElse(0L);
			route.setVehicleId((int) vehicleId);
			route.setRouteDate(time);
			route.setDriver(Optional.ofNullable(schdResult.getDriver()).orElse(0L));
			route.setSecurityA(Optional.ofNullable(schdResult.getScurityA()).orElse(0L));
			route.setSecurityB(Optional.ofNullable(schdResult.getScurityB()).orElse(0L));
			route.setRouteKeyMan(Optional.ofNullable(schdResult.getKeyMan()).orElse(0L));
			route.setRouteOperMan(Optional.ofNullable(schdResult.getOpMan()).orElse(0L));
			route.setFollower(0L);
			route.setCreateUser(0L);
			return route;
		}).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean saveTemplateResult(List<Route> routeList, long time, long departmentId){
		if (routeList.size() == 0){
			return false;
		}
		Integer result = this.baseMapper.batchInsert(routeList);

		//查询当天数据是否已经生成
        QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId).eq("route_date",time);
        RouteTemplateRecord templateRecord = recordMapper.selectOne(wrapper);

		//更新明天特殊线路
		templateService.updateRouteSign(departmentId);
		//写入记录数据
		Integer state = result != null && result >= 1 ? 1: 0;
		if (templateRecord != null){
			templateRecord.setState(state);
			recordMapper.updateById(templateRecord);
		} else{
			templateRecord = new RouteTemplateRecord();
			templateRecord.setCreateTime(System.currentTimeMillis());
			templateRecord.setDepartmentId(departmentId);
			templateRecord.setOpType(0);
			templateRecord.setRouteDate(time);
			templateRecord.setState(state);
			recordMapper.insert(templateRecord);
		}
		return true;
	}

	/**
	 * 
	 * @Title getTaskRoute
	 * @Description 获得拥有对应银行任务的线路
	 * @param departmentId
	 * @param routeDate
	 * @param bankId
	 * @param routeType 
	 * @return
	 * @return 返回类型 List<Map>
	 */
	public List<Map> getTaskRoute(Long departmentId, Long routeDate,Long bankId, Integer routeType) {
		// TODO Auto-generated method stub
		return baseMapper.getTaskRoute(departmentId, routeDate, bankId,routeType,0);
	}

	
	/**
	 * 
	 * @Title changeEmp
	 * @Description 人员调整
	 * @param routeVO
	 * @param route
	 * @return 返回类型 void
	 */
	@Transactional(rollbackFor=Exception.class)
	public void changeEmp(RouteVO routeVO, Route route) {
		boolean flag = false;
		//修改司机
		if(!route.getDriver().equals(routeVO.getDriver())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), JobTypeEnum.DRIVER.getValue(), 
					route.getDriver(), routeVO.getDriver() , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"司机调整出错");
			}
			route.setDriver(routeVO.getDriver());
		}
		
		//修改护卫A
		if(!route.getSecurityA().equals(routeVO.getSecurityA())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), JobTypeEnum.GUARD.getValue(), 
					route.getSecurityA(), routeVO.getSecurityA() , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"护卫A调整出错");
			}
			route.setSecurityA(routeVO.getSecurityA());
		}
		
		//修改护卫B
		if(!route.getSecurityB().equals(routeVO.getSecurityB())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), JobTypeEnum.GUARD.getValue(), 
					route.getSecurityB(), routeVO.getSecurityB() , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"护卫B调整出错");
			}
			route.setSecurityB(routeVO.getSecurityB());
		}
		
		//修改钥匙员
		if(!route.getRouteKeyMan().equals(routeVO.getRouteKeyMan())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), JobTypeEnum.KEY.getValue(), 
					route.getRouteKeyMan(), routeVO.getRouteKeyMan() , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"钥匙员调整出错");
			}
			route.setRouteKeyMan(routeVO.getRouteKeyMan());
		}
		
		//修改密码员
		if(!route.getRouteOperMan().equals(routeVO.getRouteOperMan())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), JobTypeEnum.CLEAN.getValue(), 
					route.getRouteOperMan(), routeVO.getRouteOperMan() , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"密码员调整出错");
			}
			route.setRouteOperMan(routeVO.getRouteOperMan());
		}
		
		//修改车辆
		if(!route.getVehicleId().equals(routeVO.getVehicleId())){
			flag = true;
			boolean save = saveEmpChange(route.getId(), new Integer(-1), 
					Long.valueOf(route.getVehicleId()), Long.valueOf(routeVO.getVehicleId()) , routeVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"车辆调整出错");
			}
			route.setVehicleId(routeVO.getVehicleId());
		}
		
		//修改线路
		if(flag){
			route.setEmpChange(1);
			boolean update = this.updateById(route);
			if(!update) {
				throw new BusinessException(-1,"修改线路信息失败");
			}
		}
	}
	
	/**
	 * 
	 * @Title saveEmpChange
	 * @Description 保存线路人员调整记录
	 * @param routeVO
	 * @param route
	 * @param jobType
	 * @return
	 * @return 返回类型 boolean
	 */
	public boolean saveEmpChange(Long routeId,Integer jobType,Long oldManId, Long newManId, String comments){
		RouteEmpChange empChange = new RouteEmpChange();
		empChange.setRouteId(routeId);
		empChange.setJobType(jobType);
		empChange.setOldManId(oldManId);
		empChange.setNewManId(newManId);
		empChange.setComments(comments);
		return routeEmpChangeService.save(empChange);
	}
	
	
	/**
	 * 
	 * @Title editHandover
	 * @Description 交接变更
	 * @param routeHandoverVO
	 * @param route
	 * @return 返回类型 void
	 */
	@Transactional(rollbackFor=Exception.class)
	public void editHandover(RouteHandoverVO routeHandoverVO, Route route) {
		boolean flag = false;
		if(!route.getReturnBoxCount().equals(routeHandoverVO.getBoxCount())){
			flag = true;
			boolean save = saveHandoverChange(route.getId(), RouteHandoverChangeEnum.BOX.getValue(), 
					route.getReturnBoxCount(), routeHandoverVO.getBoxCount() , routeHandoverVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"钞盒数调整出错");
			}
			route.setReturnBoxCount(routeHandoverVO.getBoxCount());
		}
		
		if(!route.getReturnBagCount().equals(routeHandoverVO.getBagCount())){
			flag = true;
			boolean save = saveHandoverChange(route.getId(), RouteHandoverChangeEnum.BAG.getValue(), 
					route.getReturnBagCount(), routeHandoverVO.getBagCount() , routeHandoverVO.getComments());
			if(!save){
	        	throw new BusinessException(-1,"钞袋数调整出错");
			}
			route.setReturnBagCount(routeHandoverVO.getBagCount());
		}
		
		//修改线路
		if(flag){
			route.setHandoverChange(1);
			boolean update = this.updateById(route);
			if(!update) {
				throw new BusinessException(-1,"修改线路信息失败");
			}
		}
	}
	
	private boolean saveHandoverChange(Long routeId, Integer changeType, Integer oldCount, Integer newCount,
			String comments) {
		RouteBoxbagChange boxBagChange = new RouteBoxbagChange();
		boxBagChange.setRouteId(routeId);
		boxBagChange.setChangeType(changeType);;
		boxBagChange.setOldCount(oldCount);
		boxBagChange.setNewCount(newCount);
		boxBagChange.setComments(comments);
		return routeBoxBagChangeService.save(boxBagChange);
	}

	/**
	 * 
	 * @Title findBankInquiryListByPage
	 * @Description 分页查询银行业务线路列表
	 * @param page
	 * @param limit
	 * @param route
	 * @param routeIds
	 * @return
	 * @return 返回类型 IPage<Route>
	 */
	public List<Route> findBankInquiryList(Route route, Set<Long> routeIds) {
    	QueryWrapper<Route> queryWrapper=new QueryWrapper<Route>();
    	if(route != null){
	    	if(!StringUtils.isEmpty(route.getRouteNo())){
	    		queryWrapper.eq("route_no", route.getRouteNo());
	    	}
	    	if(!StringUtils.isEmpty(route.getRouteName())){
	    		queryWrapper.eq("route_name", route.getRouteName());
	    	}
			if(route.getRouteType() != null){
				queryWrapper.eq("route_type", route.getRouteType());
			}
    	}
    	queryWrapper.in("id", routeIds);
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,true,"route_no * 1");
        return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 查询当日已确认的线路
	 * @param routeDate
	 * @return
	 */
	public List<Route> findConfirmList(Long routeDate) {
		QueryWrapper<Route> queryWrapper=new QueryWrapper<Route>();
		queryWrapper.eq("route_date", routeDate)
				.ge("status_t", RouteStatusEnum.CHECKED.getValue())
				.lt("status_t", RouteStatusEnum.FINISH.getValue())
				.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}
}
