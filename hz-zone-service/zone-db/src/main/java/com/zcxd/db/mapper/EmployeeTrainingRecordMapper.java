package com.zcxd.db.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.db.model.EmployeeTrainingRecord;

/**
 * <p>
 * 培训记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface EmployeeTrainingRecordMapper extends BaseMapper<EmployeeTrainingRecord> {

	IPage<Map<String, Object>> findListByPage(Page ipage,@Param("empNo") String empNo,
			@Param("empName") String empName, @Param("trainTitle") String trainTitle, @Param("deleted") Integer deleted);

	int insertBatch(@Param("employeeTrainingRecords") List<EmployeeTrainingRecord> employeeTrainingRecords);

}
