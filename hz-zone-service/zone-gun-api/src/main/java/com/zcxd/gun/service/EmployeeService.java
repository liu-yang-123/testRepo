package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.gun.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Employee;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 * @ClassName EmployeeService
 * @Description 员工档案服务类
 * @author 秦江南
 * @Date 2021年5月14日上午10:55:42
 */
@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee>{
	/**
	 * 通过员工ID和部门号获取员工信息
	 * @param id 员工ID
	 * @param departmentId 部门ID
	 * @return 员工信息
	 */
	public Employee getEmployeeById(Long id, Integer departmentId) {
		QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
		if (departmentId != null) {
			queryWrapper.eq("department_id", departmentId);
		}
		if (id != null) {
			queryWrapper.eq("id", id);
		}
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectOne(queryWrapper);
	}

    /**
	 * 
	 * @Title getEmployeeByCondition
	 * @Description 根据条件查询员工档案集合
	 * @param employee
	 * @return
	 * @return 返回类型 List<Employee>
	 */
	public List<Employee> getEmployeeByCondition(Employee employee) {
		QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
		if(employee != null){
			if(!StringUtils.isBlank(employee.getEmpNo())){
				queryWrapper.eq("emp_no", employee.getEmpNo());
			}
			
			if (!StringUtils.isBlank(employee.getEmpName())) {
        		queryWrapper.like("emp_name", employee.getEmpName());
        	}
			
			if (employee.getDepartmentId() != null) {
				queryWrapper.eq("department_id", employee.getDepartmentId());
			}
			
			if (employee.getJobIds() != null) {
				queryWrapper.eq("job_ids", employee.getJobIds());
			}
			
			if (employee.getJobType() != null) {
				queryWrapper.eq("job_type", employee.getJobType());
			}
			
			if (employee.getStatusT() != null) {
				queryWrapper.eq("status_t", employee.getStatusT());
			}
			
			if (employee.getRoleId() != null) {
				queryWrapper.eq("role_id", employee.getRoleId());
			}
		}

		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}
}
