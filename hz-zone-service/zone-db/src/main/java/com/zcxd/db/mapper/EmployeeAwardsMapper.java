package com.zcxd.db.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.db.model.EmployeeAwards;

/**
 * <p>
 * 奖惩记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface EmployeeAwardsMapper extends BaseMapper<EmployeeAwards> {

	IPage<Map<String, Object>> findListByPage(Page ipage, @Param("awardsType") String awardsType,@Param("empNo") String empNo,
			@Param("empName") String empName, @Param("deleted") Integer deleted);

}
