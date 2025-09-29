package com.zcxd.base.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.EmployeeAwardsMapper;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.EmployeeAwards;

/**
 * 
 * @ClassName EmployeeAwardsService
 * @Description 员工奖惩记录服务类
 * @author 秦江南
 * @Date 2021年5月19日上午9:15:02
 */
@Service
public class EmployeeAwardsService extends ServiceImpl<EmployeeAwardsMapper, EmployeeAwards>{
	@Autowired
    EmployeeMapper employeeMapper;
	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询奖惩记录列表
	 * @param page
	 * @param limit
	 * @param awardsType
	 * @param empNo
	 * @param empName
	 * @return
	 * @return 返回类型 IPage<Map<String,Object>>
	 */
    public  IPage<Map<String,Object>> findListByPage(Integer page, Integer limit, String awardsType, String empNo, String empName){
        return baseMapper.findListByPage(new Page(page,limit),awardsType,empNo,empName,0);
    }

}
