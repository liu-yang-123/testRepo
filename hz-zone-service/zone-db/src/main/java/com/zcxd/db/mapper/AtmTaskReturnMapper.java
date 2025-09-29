package com.zcxd.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.AtmTaskReturn;

/**
 * <p>
 * 回笼钞袋钞盒 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface AtmTaskReturnMapper extends BaseMapper<AtmTaskReturn> {

	List<AtmTaskReturn> findListByPage(@Param("page") Integer page,@Param("limit") Integer limit,
			@Param("departmentId") Long departmentId,@Param("boxBarCode") String boxBarCode);

}
