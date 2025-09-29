package com.zcxd.base.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.BoxpackTaskMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.BoxpackTask;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;

/**
 * 
 * @ClassName CollectSendService
 * @Description 早送晚收服务类
 * @author 秦江南
 * @Date 2021年11月30日下午3:57:01
 */
@Service
public class BoxpackTaskService extends ServiceImpl<BoxpackTaskMapper, BoxpackTask>{
	@Autowired
    private BankService bankService;
	
	@Autowired
    private RouteService routeService;
	
	public IPage<BoxpackTask> findListByPage(Integer page, Integer limit, BoxpackTask boxpackTask) {
		Page<BoxpackTask> ipage=new Page<BoxpackTask>(page,limit);
    	QueryWrapper<BoxpackTask> queryWrapper=new QueryWrapper<BoxpackTask>();
    	if(boxpackTask != null){
    		if(boxpackTask.getBankId() != null){
        		Bank bank = new Bank();
		    	bank.setBankType(1);
		    	bank.setParentIds("/" + boxpackTask.getBankId() + "/");
		    	List<Map<String, Object>> bankList = bankService.getBankList(bank);
		    	List<String> bankIds = new ArrayList<String>();
		    	bankList.stream().forEach(bankTmp -> {
		    		bankIds.add(bankTmp.get("id").toString());
		    	});
		    	bankIds.add(boxpackTask.getBankId()+"");
		    	queryWrapper.in("bank_id", bankIds);
        	}
    		
    		if(boxpackTask.getTaskType() != null){
    			queryWrapper.in("task_type", boxpackTask.getTaskType());
    		}
    		if(boxpackTask.getTaskDate() != null){
    			queryWrapper.in("task_date", boxpackTask.getTaskDate());
    		}
    		if(boxpackTask.getStatusT() != null){
    			queryWrapper.in("status_t", boxpackTask.getStatusT());
    		}
    		if(!StringUtils.isBlank(boxpackTask.getRouteNo())){
    			queryWrapper.in("route_no", boxpackTask.getRouteNo());
    		}
    	}
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderByDesc("create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}


	/**
	 * 
	 * @Title initRoute
	 * @Description 定时设置早送晚收任务的线路id
	 * @return
	 * @return 返回类型 boolean
	 */
	public boolean init() {
		//获取第二天时间戳
		LocalDate localDate = LocalDate.now().plusDays(1L);
		long time = LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		return setBoxpackTaskRouteId(time);
	}
	
	/**
	 * 
	 * @Title setBoxpackTaskRouteId
	 * @Description 设置早送晚收任务的线路id
	 * @param time
	 * @return
	 * @return 返回类型 boolean
	 */
	public boolean setBoxpackTaskRouteId(long time) {
		QueryWrapper wrapper = Wrappers.query().eq("task_date",time).eq("route_id", 0).eq("deleted",0);
		List<BoxpackTask> boxpackTaskList = this.baseMapper.selectList(wrapper);
		if(null != boxpackTaskList && boxpackTaskList.size() > 0){
			
			//得到未分配的任务线路集合
			Set<String> routeNos = boxpackTaskList.stream().map(boxpackTask -> boxpackTask.getRouteNo()).collect(Collectors.toSet());
			QueryWrapper routeWrapper = Wrappers.query().eq("route_date",time).in("route_no", routeNos).eq("deleted",0);
    		List<Route> routeList = routeService.list(routeWrapper);
    		Map<String,Long> routeMap = routeList.stream().collect(Collectors.toMap(Route::getRouteNo, route -> route.getId()));
    		
    		boxpackTaskList.stream().forEach(boxpackTask -> {
    			boxpackTask.setRouteId(routeMap.get(boxpackTask.getRouteNo()));
    		});
    		return this.updateBatchById(boxpackTaskList);
		}
		return true;
	}

}
