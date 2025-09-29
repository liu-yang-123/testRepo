package com.zcxd.gun.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.Employee;
import org.apache.ibatis.annotations.Param;

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
