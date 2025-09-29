package com.zcxd.base.service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.SchdCondutorDTO;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.vo.EmployeeQueryVO;
import com.zcxd.base.vo.PDAUserQueryVO;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.DepartmentMapper;
import com.zcxd.db.mapper.EmployeeJobMapper;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Department;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.EmployeeJob;

/**
 * 
 * @ClassName EmployeeService
 * @Description 员工档案服务类
 * @author 秦江南
 * @Date 2021年5月14日上午10:55:42
 */
@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee>{

	@Autowired
	EmployeeJobMapper employeeJobMapper;

	@Autowired
	DepartmentMapper departmentMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisUtil redisUtil;

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询员工列表
	 * @param page
	 * @param limit
	 * @param employeeQueryVO
	 * @return
	 * @return 返回类型 IPage<Employee>
	 */
    public IPage<Employee> findListByPage(Integer page, Integer limit, EmployeeQueryVO employeeQueryVO){
    	QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        IPage<Employee> ipage=new Page<Employee>(page,limit);
        if(employeeQueryVO != null) {
        	
        	if (!StringUtils.isBlank(employeeQueryVO.getEmpNo())) {
        		queryWrapper.eq("emp_no", employeeQueryVO.getEmpNo());
        	}
        	
        	if (!StringUtils.isBlank(employeeQueryVO.getEmpName())) {
        		queryWrapper.like("emp_name", employeeQueryVO.getEmpName());
        	}
        	
	        if (employeeQueryVO.getMinAge() != null) {
	        	queryWrapper.ge("birthday", DateTimeUtil.getPreYearTime(employeeQueryVO.getMinAge()));
			}
        	
	        if (employeeQueryVO.getMaxAge() != null) {
	        	queryWrapper.le("birthday", DateTimeUtil.getPreYearTime(employeeQueryVO.getMaxAge()));
			}

        	if (!StringUtils.isBlank(employeeQueryVO.getPolitic())) {
        		queryWrapper.like("politic", employeeQueryVO.getPolitic());
        	}
        	
        	if (!StringUtils.isBlank(employeeQueryVO.getEducation())) {
        		queryWrapper.like("education", employeeQueryVO.getEducation());
        	}
        	
        	if (employeeQueryVO.getSex() != null) {
        		queryWrapper.eq("sex", employeeQueryVO.getSex());
        	}
        	
        	if (employeeQueryVO.getJobId() != null) {
        		queryWrapper.eq("job_ids", employeeQueryVO.getJobId());
        	}

	
	        if (employeeQueryVO.getDepartmentId() != null) {
	        	Department department = new Department();
	        	department.setParentIds("/" + employeeQueryVO.getDepartmentId().toString() + "/");
	        	Map<String, String> departmentMap = commonService.getDepartmentMap(department);
	        	Set<String> keySet = departmentMap.keySet();
	        	Set<String> setT = new HashSet<String>();
	        	setT.addAll(keySet);
	        	setT.add(employeeQueryVO.getDepartmentId()+"");
				queryWrapper.in("department_id", setT);
			}
	        
        	if (employeeQueryVO.getStatus() != null) {
        		queryWrapper.eq("status_t", employeeQueryVO.getStatus());
        	}

        }
        
        
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectPage(ipage, queryWrapper);
    }

	/**
	 * 通过员工ID和部门号获取员工信息
	 * @param id 员工ID
	 * @param departmentId 部门ID
	 * @return 员工信息
	 */
	public Employee getEmployeeById(Long id, Long departmentId) {
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
	        	Department department = new Department();
	        	department.setParentIds("/" + employee.getDepartmentId().toString() + "/");
	        	Map<String, String> departmentMap = commonService.getDepartmentMap(department);
	        	Set<String> keySet = departmentMap.keySet();
	        	Set<String> setT = new HashSet<String>();
	        	setT.addAll(keySet);
	        	setT.add(employee.getDepartmentId()+"");
	        	
				queryWrapper.in("department_id", setT);
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
	
	
	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 根据条件查询PDA用户分页列表
	 * @param page
	 * @param limit
	 * @param pdaUserQueryVO
	 * @return
	 * @return 返回类型 IPage<Employee>
	 */
	public IPage<Employee> findListByPage(Integer page, Integer limit, PDAUserQueryVO pdaUserQueryVO){
    	QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        IPage<Employee> ipage=new Page<Employee>(page,limit);
        if(pdaUserQueryVO != null) {
        	
        	if (!StringUtils.isBlank(pdaUserQueryVO.getEmpNo())) {
        		queryWrapper.eq("emp_no", pdaUserQueryVO.getEmpNo());
        	}
        	
        	if (!StringUtils.isBlank(pdaUserQueryVO.getEmpName())) {
        		queryWrapper.like("emp_name", pdaUserQueryVO.getEmpName());
        	}
        	
	
	        if (pdaUserQueryVO.getDepartmentId() != null) {
	        	Department department = new Department();
	        	department.setParentIds("/" + pdaUserQueryVO.getDepartmentId().toString() + "/");
	        	Map<String, String> departmentMap = commonService.getDepartmentMap(department);
	        	Set<String> keySet = departmentMap.keySet();
	        	Set<String> setT = new HashSet<String>();
	        	setT.addAll(keySet);
	        	setT.add(pdaUserQueryVO.getDepartmentId()+"");
				queryWrapper.in("department_id", setT);
			}
	        
        	if (pdaUserQueryVO.getPdaEnable() != null) {
        		queryWrapper.eq("pda_enable", pdaUserQueryVO.getPdaEnable());
        	}
        	
        	if (pdaUserQueryVO.getJobId() != null) {
        		queryWrapper.eq("job_ids", pdaUserQueryVO.getJobId());
        	}

        }
        
        queryWrapper.eq("status_t", 0);
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectPage(ipage, queryWrapper);
    }

	public void updateJobType(EmployeeJob employeeJob) {
		deleteBatchUsersCacheByPrefix(); //岗位类型变更，淘汰所有app用户信息
		baseMapper.updateJobType(employeeJob.getId(), employeeJob.getJobType());
	}
	
	/**
	 * @Description:获取车长资格列表数据
	 * @Author: lilanglang
	 * @Date: 2021/7/7 15:48
	 * @param page:
	 * @param limit:
	 * @param schdCondutorDTO:
	 * @param jobTypeList 岗位类型列表数据
	 **/
	public IPage<Employee> listSchdConductorPage(Integer page, Integer limit, SchdCondutorDTO schdCondutorDTO,List<Integer> jobTypeList) {
		QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
		IPage<Employee> ipage=new Page<Employee>(page,limit);
		if(schdCondutorDTO!=null){
			if(StringUtils.isNotEmpty(schdCondutorDTO.getEmpName())){
				queryWrapper.like("emp_name",schdCondutorDTO.getEmpName());
			}
			if(schdCondutorDTO.getDepartmentId()!=null){
				Department department = new Department();
				department.setParentIds("/" + schdCondutorDTO.getDepartmentId().toString() + "/");
				Map<String, String> departmentMap = commonService.getDepartmentMap(department);
				Set<String> keySet = departmentMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
				keySet.add(schdCondutorDTO.getDepartmentId()+"");
				queryWrapper.in("department_id", keySet);
			}
			if (schdCondutorDTO.getRouteLeader() != null){
				queryWrapper.in("route_leader", schdCondutorDTO.getRouteLeader());
			}
		}
		if (jobTypeList != null && jobTypeList.size() > 0){
			queryWrapper.in("job_type", jobTypeList);
		}
		queryWrapper.eq("status_t", 0);
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * @Description:获取部门下的所有人员信息
	 * @Author: lilanglang
	 * @Date: 2021/7/9 16:54
	 * @param departmentId:
	 **/
    public List<Employee> listByDepartMent(Long departmentId) {
		QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
		if(departmentId!=null){
			Department department = new Department();
			department.setParentIds("/" + departmentId.toString() + "/");
			Map<String, String> departmentMap = commonService.getDepartmentMap(department);
			Set<String> keySet = departmentMap.keySet();
			Set<String> setT = new HashSet<String>();
			setT.addAll(keySet);
			setT.add(departmentId+"");
			queryWrapper.in("department_id", setT);
		}
		return baseMapper.selectList(queryWrapper);
    }

	/**
	 * 查询清机、密码岗的车长信息
	 * @param departmentId
	 * @return
	 */
	public List<Employee> getLeader(Long departmentId){
		Collection<Integer> types = Arrays.asList(3,4);
		QueryWrapper wrapper = Wrappers.query().eq("deleted",0).in("job_type",types);
		Department department = new Department();
		department.setParentIds("/" + departmentId + "/");
		Map<String, String> departmentMap = commonService.getDepartmentMap(department);
		Set<String> keySet = departmentMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        keySet.add(String.valueOf(departmentId));

		return baseMapper.selectList(wrapper);
	}

	public void deleteEmployeeUserCache(Serializable id) {
		String key = CacheKeysDef.ComAppUserPrefix+id;
		redisUtil.delete(key);
	}
	public void deleteBatchUsersCacheByPrefix() {
		String key = CacheKeysDef.ComAppUserPrefix+"*";
		redisUtil.delete(key);
	}
}
