package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.EmployeeStatusEnum;
import com.zcxd.db.mapper.DepartmentMapper;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Department;
import com.zcxd.db.model.Employee;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName DepartmentService
 * @Description 部门信息服务
 * @author shijin
 * @Date 2021年5月26日上午10:50:05
 */
@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {


}
