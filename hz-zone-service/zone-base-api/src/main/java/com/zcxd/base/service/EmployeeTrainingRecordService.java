package com.zcxd.base.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.EmployeeTrainingRecordBatch;
import com.zcxd.base.vo.EmployeeTrainingRecordBatchVO;
import com.zcxd.db.mapper.EmployeeTrainingRecordMapper;
import com.zcxd.db.model.EmployeeTrainingRecord;

/**
 * 
 * @ClassName EmployeeTrainingRecordService
 * @Description 员工培训记录服务类
 * @author 秦江南
 * @Date 2021年5月18日下午3:55:04
 */
@Service
public class EmployeeTrainingRecordService extends ServiceImpl<EmployeeTrainingRecordMapper, EmployeeTrainingRecord>{
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EmployeeTrainingSubjectService employeeTrainingSubjectService;

	/**
	 * 
	 * @Title savemulti
	 * @Description 批量添加员工培训成绩
	 * @param employeeTrainingRecordBatchVO
	 * @return
	 * @return 返回类型 boolean
	 */
    @Transactional(rollbackFor=Exception.class)
	public boolean savemulti(List<EmployeeTrainingRecord> employeeTrainingRecords) {
		int insert = baseMapper.insertBatch(employeeTrainingRecords);
        if(insert <= 0)
        	throw new BusinessException(-1,"批量添加培训记录失败");
		return true;
	}

    /**
     * 
     * @Title findListByPage
     * @Description 分页查询培训成绩列表
     * @param page
     * @param limit
     * @param empNo
     * @param empName
     * @param trainTitle
     * @return
     * @return 返回类型 IPage<Map<String,Object>>
     */
	public IPage<Map<String,Object>> findListByPage(Integer page, Integer limit, String empNo, String empName, String trainTitle) {
        return baseMapper.findListByPage(new Page(page,limit),empNo,empName,trainTitle,0);
	}

}
