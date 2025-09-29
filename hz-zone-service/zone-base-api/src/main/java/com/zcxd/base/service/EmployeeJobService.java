package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.EmployeeJobMapper;
import com.zcxd.db.model.EmployeeJob;

/**
 * 
 * @ClassName EmployeeJobService
 * @Description 岗位管理服务类
 * @author 秦江南
 * @Date 2021年5月13日下午4:33:32
 */
@Service
public class EmployeeJobService extends ServiceImpl<EmployeeJobMapper, EmployeeJob>{
	
	@Autowired
    private EmployeeService employeeService;
	
    /**
     * 
     * @Title getEmployeeJobByCondition
     * @Description 根据条件查询岗位集合
     * @param employeeJob
     * @return
     * @return 返回类型 List<EmployeeJob>
     */
	public List<EmployeeJob> getEmployeeJobByCondition(EmployeeJob employeeJob) {
		QueryWrapper<EmployeeJob> queryWrapper = new QueryWrapper<>();
		if(employeeJob != null){
			if(!StringUtils.isBlank(employeeJob.getName())){
				queryWrapper.eq("name", employeeJob.getName());
			}
			
			if(employeeJob.getJobType() != null){
				queryWrapper.eq("job_type", employeeJob.getJobType());
			}
		}
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询岗位列表
	 * @param page
	 * @param limit
	 * @param employeeJob
	 * @return
	 * @return 返回类型 Page<EmployeeJob>
	 */
	public Page<EmployeeJob> findListByPage(Integer page, Integer limit, EmployeeJob employeeJob) {
		Page<EmployeeJob> ipage = new Page<EmployeeJob>(page,limit);
		QueryWrapper<EmployeeJob> queryWrapper = new QueryWrapper<>();
		if(employeeJob != null){
			if(!StringUtils.isBlank(employeeJob.getName())){
				queryWrapper.like("name", employeeJob.getName());
			}
		}
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		
		return baseMapper.selectPage(ipage, queryWrapper);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean updateJob(EmployeeJob employeeJob, boolean flag) {
		if(flag){
			employeeService.updateJobType(employeeJob);
		}
		
		return baseMapper.updateById(employeeJob) >= 1;
	}

}
