package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VehicleMapper;
import com.zcxd.db.model.Vehicle;

/**
 * 
 * @ClassName VehicleService
 * @Description 车辆管理服务类
 * @author 秦江南
 * @Date 2021年5月19日下午3:10:26
 */
@Service
public class VehicleService extends ServiceImpl<VehicleMapper, Vehicle>{

	/**
	 * 
	 * @Title getVehicleByCondition
	 * @Description 根据条件查询车辆信息
	 * @param vehicle
	 * @return
	 * @return 返回类型 List<Vehicle>
	 */
	public List<Vehicle> getVehicleByCondition(Vehicle vehicle) {
		QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
		if(vehicle != null){
			if(!StringUtils.isBlank(vehicle.getSeqno())){
				queryWrapper.eq("seqno", vehicle.getSeqno());
			}
			
			if(!StringUtils.isBlank(vehicle.getLpno())){
				queryWrapper.eq("lpno", vehicle.getLpno());
			}
			
	    	 if(vehicle.getDepartmentId() != null){
	    		 queryWrapper.eq("department_id", vehicle.getDepartmentId());
	    	 }
	    	 
	    	 if(vehicle.getStatusT() != null){
	    		 queryWrapper.eq("status_t", vehicle.getStatusT());
	    	 }
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询车辆信息
	 * @param page
	 * @param limit
	 * @param vehicle
	 * @return
	 * @return 返回类型 IPage<Vehicle>
	 */
	public IPage<Vehicle> findListByPage(Integer page, Integer limit, Vehicle vehicle) {
		Page<Vehicle> ipage=new Page<Vehicle>(page,limit);
    	QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
    	
    	if(vehicle != null){
			if(!StringUtils.isBlank(vehicle.getSeqno())){
				queryWrapper.eq("seqno", vehicle.getSeqno());
			}
			
			if(!StringUtils.isBlank(vehicle.getLpno())){
				queryWrapper.like("lpno", vehicle.getLpno());
			}
			
			if(!StringUtils.isBlank(vehicle.getFactory())){
				queryWrapper.eq("factory", vehicle.getFactory());
			}
			
			if(!StringUtils.isBlank(vehicle.getModel())){
				queryWrapper.eq("model", vehicle.getModel());
			}
			
			if(vehicle.getVhType() != null){
				queryWrapper.eq("vh_type", vehicle.getVhType());
			}
			
			if(vehicle.getDepartmentId() != null){
				queryWrapper.eq("department_id", vehicle.getDepartmentId());
			}
			
			if(vehicle.getStatusT() != null){
				queryWrapper.eq("status_t", vehicle.getStatusT());
			}
		}
    	
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"create_time");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}

}
