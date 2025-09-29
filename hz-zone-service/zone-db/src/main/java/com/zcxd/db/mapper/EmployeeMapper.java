package com.zcxd.db.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.Employee;

/**
 * <p>
 * 员工信息 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

	void updateJobType(@Param("jobId") Long jobId,@Param("jobType") Integer jobType);

}
