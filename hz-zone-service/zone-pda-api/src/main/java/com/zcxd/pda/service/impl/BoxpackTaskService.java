package com.zcxd.pda.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.BoxpackTaskStatusEnum;
import com.zcxd.common.constant.CollectSendTaskReportEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.db.mapper.*;
import com.zcxd.db.model.*;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.BoxpackTaskDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import org.springframework.util.StringUtils;

/**
 * 
 * @ClassName BoxpackTaskService
 * @Description 早送晚收任务服务类
 * @author 秦江南
 * @Date 2021年11月29日下午5:41:55
 */
@AllArgsConstructor
@Service
public class BoxpackTaskService extends ServiceImpl<BoxpackTaskMapper, BoxpackTask>{

	private BankTellerMapper bankTellerMapper;

	private RouteMapper routeMapper;

	private VehicleMapper vehicleMapper;

	/**
	 * 
	 * @Title getStorageTask
	 * @Description 获取库房指定日期早送晚收任务
	 * @param taskDate
	 * @param bankId
	 * @param routeNo
	 * @param taskType
	 * @return
	 * @return 返回类型 BoxpackTask
	 */
	public BoxpackTask getStorageTask(Long taskDate,Long bankId,String routeNo,Integer taskType){
		BoxpackTask where = new BoxpackTask();
        where.setBankId(bankId);
        where.setTaskDate(taskDate);
        where.setRouteNo(routeNo);
        where.setTaskType(taskType);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        List<BoxpackTask> list = baseMapper.selectList(new QueryWrapper<>(where));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
	}

	/**
	 * 查询当天任务列表数据
	 * @return
	 */
	public List<BoxpackTaskDTO> getTaskList(){
		PdaUser pdaUser = UserContextHolder.getUser();
		int userType = pdaUser.getUserType();
		Long bankId = getBankIdByUser(pdaUser);
		if (bankId == 0L){
			return Collections.emptyList();
		}
		//查询当天日期时间戳
		long taskDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		//根据用户类型查询对应的数据字段
		QueryWrapper queryWrapper = Wrappers.query().eq("bank_id",bankId)
				.eq("status_t",BoxpackTaskStatusEnum.CHECKED.getValue())
				.eq("task_date",taskDate).eq("deleted",DeleteFlagEnum.NOT.getValue());
		List<BoxpackTask> taskList = this.baseMapper.selectList(queryWrapper);
		//查询线路ID->车牌数据
		Set<Long> routeIds = taskList.stream().map(BoxpackTask::getRouteId).filter(r -> r > 0).collect(Collectors.toSet());
		Map<Long, String> finalRouteVehicleMap = getRouteVehicleMap(routeIds);
		//格式化输出列表数据
		return taskList.stream().map(item -> {
			BoxpackTaskDTO taskDTO = new BoxpackTaskDTO();
			BeanUtils.copyProperties(item,taskDTO);
			String[] boxpackIdArr = item.getBoxList().split(",");
			taskDTO.setNumber(boxpackIdArr.length);
			taskDTO.setUserType(userType);
			taskDTO.setTaskText(formatTaskText(userType,item.getTaskType()));
			taskDTO.setVehicleNo(Optional.ofNullable(finalRouteVehicleMap.get(item.getRouteId())).orElse(""));
			return taskDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * 分页请求列表数据
	 * @param page
	 * @param limit
	 * @param date
	 * @return
	 */
	public IPage<BoxpackTaskDTO> listPage(Integer page, Integer limit, String date){
		PdaUser pdaUser = UserContextHolder.getUser();
		int userType = pdaUser.getUserType();
		Long bankId = getBankIdByUser(pdaUser);
		if (bankId ==0L){
			return new Page<>();
		}
		IPage<BoxpackTask> iPage = new Page<>(page,limit);
		BoxpackTask boxpackTask = new BoxpackTask();

		if (!StringUtils.isEmpty(date)){
			LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			long taskDate = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
			boxpackTask.setTaskDate(taskDate);
		}
		boxpackTask.setBankId(bankId);
		boxpackTask.setDeleted(DeleteFlagEnum.NOT.getValue());
		QueryWrapper wrapper = Wrappers.query(boxpackTask).orderByDesc("id");
		IPage<BoxpackTask> boxpackTaskIPage = baseMapper.selectPage(iPage,wrapper);
		List<BoxpackTask> taskList = boxpackTaskIPage.getRecords();
		//查询线路ID->车牌数据
//		Set<Long> routeIds = taskList.stream().map(BoxpackTask::getRouteId).filter(r -> r > 0).collect(Collectors.toSet());
//		Map<Long, String> finalRouteVehicleMap = getRouteVehicleMap(routeIds);
		//格式化数据
		List<BoxpackTaskDTO> taskDTOList = taskList.stream().map(item -> {
			BoxpackTaskDTO taskDTO = new BoxpackTaskDTO();
//			BeanUtils.copyProperties(item,taskDTO);
			taskDTO.setUserType(userType);
//			taskDTO.setVehicleNo(Optional.ofNullable(finalRouteVehicleMap.get(item.getRouteId())).orElse(""));
			taskDTO.setId(item.getId());
			taskDTO.setStatusT(item.getStatusT());
			taskDTO.setCreateTime(item.getCreateTime());
			taskDTO.setRouteNo(item.getRouteNo());
			String[] boxpackIdArr = item.getBoxList().split(",");
			taskDTO.setNumber(boxpackIdArr.length);
			taskDTO.setTaskText(formatTaskText(userType,item.getTaskType()));
			return taskDTO;
		}).collect(Collectors.toList());

		IPage<BoxpackTaskDTO> taskDTOIPage =  new Page<>();
		taskDTOIPage.setTotal(boxpackTaskIPage.getTotal());
		taskDTOIPage.setRecords(taskDTOList);
		return taskDTOIPage;
	}

	/**
	 * 线路ID集合查询{线路ID->车牌号}映射关系
	 * @param routeIds
	 * @return
	 */
	private Map<Long,String> getRouteVehicleMap(Set<Long> routeIds){
		if (routeIds.size() == 0){
			return Collections.emptyMap();
		}
		List<Route> routeList = routeMapper.selectBatchIds(routeIds);
		Set<Integer> vehicleIds = routeList.stream().map(Route::getVehicleId).collect(Collectors.toSet());
		Map vehicleMap = Collections.EMPTY_MAP;
		if (vehicleIds.size() > 0){
			List<Vehicle> vehicleList = vehicleMapper.selectBatchIds(vehicleIds);
			vehicleMap = vehicleList.stream().collect(Collectors.toMap(Vehicle::getId,Vehicle::getLpno));
		}
		Map<Long, String> finalVehicleMap = vehicleMap;
		Map<Long,String> routeVehicleMap = routeList.stream().collect(Collectors.toMap(Route::getId, t -> Optional.ofNullable(finalVehicleMap.get(t.getVehicleId().longValue())).orElse("")));
		return routeVehicleMap;
	}

	/**
	 * 当前用户获取bankId
	 * @param pdaUser
	 * @return
	 */
	private Long getBankIdByUser(PdaUser pdaUser){
		int userType = pdaUser.getUserType();
		long userId = pdaUser.getId();
		QueryWrapper wrapper = Wrappers.query().eq("deleted",DeleteFlagEnum.NOT.getValue());
		if (userType == Constant.USER_EMPLOYEE){
			wrapper.eq("emp_id", userId);
		} else{
			wrapper.eq("id",userId);
		}
		BankTeller bankTeller = bankTellerMapper.selectOne(wrapper);
		if (bankTeller == null){
			return 0L;
		}
		return bankTeller.getBankId();
	}

	/**
	 * 格式化任务类型文本
	 * @param userType
	 * @param taskType
	 * @return
	 */
	private String formatTaskText(int userType, int taskType){
		//如果是员工
		if (userType == Constant.USER_EMPLOYEE){
			if (taskType == CollectSendTaskReportEnum.FIXED_ISSUE.getValue()){
				return "出库";
			}else if(taskType == CollectSendTaskReportEnum.TEMP_ISSUE.getValue()){
				return "临时出库";
			}else if(taskType == CollectSendTaskReportEnum.FIXED_PAY.getValue()){
				return "入库";
			}else if(taskType == CollectSendTaskReportEnum.TEMP_PAY.getValue()){
				return "临时入库";
			}
		}
		return CollectSendTaskReportEnum.getText(taskType);
	}

	/**
	 * 获取当天的任务箱包ID
	 * @param type 0-箱包 1-押运员
	 * @return
	 */
	public Set<Long> getTodayIdsByType(Integer type){
		PdaUser pdaUser = UserContextHolder.getUser();
		int userType = pdaUser.getUserType();
		Long bankId = getBankIdByUser(pdaUser);
		if (bankId == 0L){
			return Collections.EMPTY_SET;
		}
		long taskDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		//查询当天的箱包固定  库房查下发类兴业  银行端查上缴类业务
		Set<Integer> typeSet = null;
		if (userType == Constant.USER_EMPLOYEE){
			typeSet = Arrays.asList(CollectSendTaskReportEnum.FIXED_PAY.getValue(),CollectSendTaskReportEnum.TEMP_PAY.getValue())
					.stream().collect(Collectors.toSet());
		} else{
			typeSet = Arrays.asList(CollectSendTaskReportEnum.FIXED_ISSUE.getValue(),CollectSendTaskReportEnum.TEMP_ISSUE.getValue())
					.stream().collect(Collectors.toSet());
		}
		QueryWrapper wrapper = Wrappers.query().eq("deleted",DeleteFlagEnum.NOT.getValue())
				.eq("task_date",taskDate).eq("bank_id",bankId);
		if (type == 0){
			wrapper.in("task_type",typeSet);
		}
		
		List<BoxpackTask> taskList = this.baseMapper.selectList(wrapper);

		if (type == 0){
			return  taskList.stream().map(BoxpackTask::getBoxList).flatMap(r -> Stream.of(r.split(",")))
					.map(Long::new).collect(Collectors.toSet());
		}
		return  taskList.stream().map(BoxpackTask::getRouteId).collect(Collectors.toSet());
	}

}
