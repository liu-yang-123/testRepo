package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.EmployeeTrainingSubjectMapper;
import com.zcxd.db.model.EmployeeTrainingSubject;

/**
 * 
 * @ClassName EmployeeTrainingSubjectService
 * @Description 员工培训主题服务类
 * @author 秦江南
 * @Date 2021年5月18日下午3:54:48
 */
@Service
public class EmployeeTrainingSubjectService extends ServiceImpl<EmployeeTrainingSubjectMapper, EmployeeTrainingSubject>{

	/**
	 * 
	 * @Title findListByPage
	 * @Description 查询培训主题分页列表
	 * @param page
	 * @param limit
	 * @param employeeTrainingSubject
	 * @return
	 * @return 返回类型 Object
	 */
    public IPage<EmployeeTrainingSubject> findListByPage(Integer page, Integer limit, EmployeeTrainingSubject employeeTrainingSubject){
    	Page<EmployeeTrainingSubject> ipage=new Page<EmployeeTrainingSubject>(page,limit);
        QueryWrapper<EmployeeTrainingSubject> queryWrapper=new QueryWrapper<EmployeeTrainingSubject>();
        
        if(employeeTrainingSubject != null){
        	
	        if(employeeTrainingSubject.getTrainDate() != null){
	        	queryWrapper.eq("train_date", employeeTrainingSubject.getTrainDate());
	        }
	        if(!StringUtils.isBlank(employeeTrainingSubject.getTrainTitle())){
	        	queryWrapper.like("train_title", employeeTrainingSubject.getTrainTitle());
	        }
	        if(!StringUtils.isBlank(employeeTrainingSubject.getTrainType())){
	        	queryWrapper.eq("train_type", employeeTrainingSubject.getTrainType());
	        }
        }
        
	    queryWrapper.eq("deleted", 0);
	    queryWrapper.orderBy(true,false,"create_time");

        return baseMapper.selectPage(ipage, queryWrapper);
    }
    
    /**
     * 
     * @Title getEmployeeTrainingSubjectByCondition
     * @Description 查询培训主题列表
     * @param employeeTrainingSubject
     * @return
     * @return 返回类型 List<EmployeeTrainingSubject>
     */
	public List<EmployeeTrainingSubject> getEmployeeTrainingSubjectByCondition(EmployeeTrainingSubject employeeTrainingSubject) {
	
		QueryWrapper<EmployeeTrainingSubject> queryWrapper = new QueryWrapper<>();
		if(employeeTrainingSubject != null){
			if(!StringUtils.isBlank(employeeTrainingSubject.getTrainTitle())){
				queryWrapper.like("train_title", employeeTrainingSubject.getTrainTitle());
			}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

    
}
