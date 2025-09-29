package com.zcxd.base.service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.RouteTemplateMapper;
import com.zcxd.db.model.RouteTemplate;
import com.zcxd.db.model.RouteTemplateAtm;

/**
 * 
 * @ClassName RouteTemplateService
 * @Description 线路管理模板服务类
 * @author 秦江南
 * @Date 2021年5月28日上午11:04:09
 */
@Service
public class RouteTemplateService extends ServiceImpl<RouteTemplateMapper, RouteTemplate>{

	/**
	 * 
	 * @Title getRouteTemplateByCondition
	 * @Description 根据条件查询线路模板列表
	 * @param routeTemplate
	 * @return
	 * @return 返回类型 List<RouteTemplate>
	 */
	public List<RouteTemplate> getRouteTemplateByCondition(RouteTemplate routeTemplate) {
		QueryWrapper<RouteTemplate> queryWrapper = new QueryWrapper<>();
		if(routeTemplate != null){
			if(!StringUtils.isBlank(routeTemplate.getRouteNo())){
				queryWrapper.eq("route_no", routeTemplate.getRouteNo());
			}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询线路模板列表
	 * @param page
	 * @param limit
	 * @param routeTemplate
	 * @return
	 * @return 返回类型 IPage<RouteTemplate>
	 */
	public IPage<RouteTemplate> findListByPage(Integer page, Integer limit, RouteTemplate routeTemplate) {
		Page<RouteTemplate> ipage=new Page<RouteTemplate>(page,limit);
    	QueryWrapper<RouteTemplate> queryWrapper=new QueryWrapper<RouteTemplate>();
    	if(routeTemplate != null){
	    	if(!StringUtils.isEmpty(routeTemplate.getRouteNo())){
	    		queryWrapper.eq("route_no", routeTemplate.getRouteNo());
	    	}
	    	
	    	if(!StringUtils.isEmpty(routeTemplate.getRouteName())){
	    		queryWrapper.eq("route_name", routeTemplate.getRouteName());
	    	}
	    	
	    	if(routeTemplate.getDepartmentId() != null ){
	    		queryWrapper.eq("department_id", routeTemplate.getDepartmentId());
	    	}
	    	
    	}
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,true,"route_no * 1");
//    	queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}


	/**
	 * 获取当天不需要生成的线路
	 * @param time
	 * @return
	 */
	public List<RouteTemplate> getExcludeRoute(long departmentId, long time){
		QueryWrapper wrapper = Wrappers.query().eq("rule",1).eq("department_id",departmentId).eq("deleted",0);
        List<RouteTemplate> routeTemplateList = baseMapper.selectList(wrapper);
        // 线路生成规则 0-每天 1-隔天
		return routeTemplateList.stream().filter(s -> {
			//如果为1，则提取sign字段,此标志为明天该线路是否生成
			int sign = s.getSign();
			LocalDate toLocalDate = LocalDate.now().plusDays(1L);
			LocalDate targetLocalDate = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDate();
            //获取明天的
			Period period = Period.between(targetLocalDate,toLocalDate);
			int days = period.getDays();
			if (days % 2 == 0){
				return sign == 1;
			}
			return sign == 0;
		}).collect(Collectors.toList());
	}

	/**
	 * 特殊线路处理，60、61号线间隔生成，更新sign标志位
	 * @return
	 */
	public boolean updateRouteSign(Long departmentId){
		QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId).eq("rule",1).eq("deleted",0);
		List<RouteTemplate> routeTemplateList = baseMapper.selectList(wrapper);
		// 线路生成规则1-隔天
		routeTemplateList.stream().forEach(s -> {
			//如果为1，n则提取sig段,此标志为明天该线路是否生成
			int sign = s.getSign();
			int newSign = sign == 0 ? 1 : 0;
			RouteTemplate routeTemplate = new RouteTemplate();
			routeTemplate.setId(s.getId());
			routeTemplate.setUpdateUser(0L);
			routeTemplate.setSign(newSign);
			baseMapper.updateById(routeTemplate);
		});

		return true;
	}

}
