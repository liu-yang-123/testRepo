package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.db.mapper.DepartmentMapper;
import com.zcxd.db.model.Department;

/**
 * 
 * @ClassName DepartmentServiceImpl
 * @Description 部门管理服务类
 * @author 秦江南
 * @Date 2021年5月6日下午3:34:48
 */
@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {
	
	/**
	 * 
	 * @Title getDepartmentList
	 * @Description 获取部门数据集合
	 * @param department
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getDepartmentList(Department department){
		 QueryWrapper<Department> queryWrapper=new QueryWrapper<Department>();
	     queryWrapper.select("id,name,SUBSTRING_INDEX(SUBSTRING_INDEX(parent_ids, '/', -2), '/', 1) as pid");
	     if(department != null){
	    	 if(!StringUtils.isBlank(department.getParentIds())){
	    		 queryWrapper.like("parent_ids", department.getParentIds());
	    	 }
	     }
	     queryWrapper.eq("deleted", 0);
	     
	     return baseMapper.selectMaps(queryWrapper);
	}

	
	/**
	 * 
	 * @Title getAuthDepartmentList
	 * @Description 得到权限部门列表
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getAuthDepartmentList(){
		String authDepartments = UserContextHolder.getUser().getAuthDepartments();
		String[] departmentIds = authDepartments.substring(1, authDepartments.length()-1).split("/");
		List<Map<String, Object>> selectMaps = new ArrayList<Map<String,Object>>();
		for (String departmentId : departmentIds) {
			QueryWrapper<Department> queryWrapper=new QueryWrapper<Department>();
		     queryWrapper.select("id,name,SUBSTRING_INDEX(SUBSTRING_INDEX(parent_ids, '/', -2), '/', 1) as pid")
		     .and(wrapper -> wrapper.like("parent_ids", "/" + departmentId +"/").or().eq("id", departmentId)).eq("deleted", 0);
		     selectMaps.addAll(baseMapper.selectMaps(queryWrapper));
		}
		return selectMaps;
	}
	
	/**
	 * 
	 * @Title getDepartmentByCondition
	 * @Description 根据条件查询部门信息
	 * @param department
	 * @return
	 * @return 返回类型 List<Department>
	 */
	public List<Department> getDepartmentByCondition(Department department) {
		QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
		if(department != null){
			if(!StringUtils.isBlank(department.getName())){
				queryWrapper.eq("name", department.getName());
			}
			if(!StringUtils.isBlank(department.getParentIds())){
				queryWrapper.eq("parent_ids", department.getParentIds());
			}
			if(department.getId() != null){
				queryWrapper.eq("id", department.getId());
			}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title getTopDepartment
	 * @Description 获得顶级部门列表
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getTopDepartment() {
		return baseMapper.selectMaps(new QueryWrapper<Department>().select("id","name").eq("parent_ids","/0/").eq("deleted", 0));
	}

	/**
	 * 
	 * @Title getAuthDepartment
	 * @Description 获得权限部门列表
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getAuthDepartment() {
		String authDepartments = UserContextHolder.getUser().getAuthDepartments();
		String[] departmentIds = authDepartments.substring(1, authDepartments.length()-1).split("/");
		return baseMapper.selectMaps(new QueryWrapper<Department>().select("id","name").in("id",departmentIds));
	}

	public boolean isAuthDepartment(Integer id) {
		String authDepartments = UserContextHolder.getUser().getAuthDepartments();
		String[] departmentIds = authDepartments.substring(1, authDepartments.length()-1).split("/");
		List<Integer> list = Arrays.stream(departmentIds).map(Integer::valueOf).collect(Collectors.toList());
		return list.contains(id);
	}
}
