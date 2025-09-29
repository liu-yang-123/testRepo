package com.zcxd.db.mapper;

import com.zcxd.db.model.VehicleMaintain;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 车辆维保记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface VehicleMaintainMapper extends BaseMapper<VehicleMaintain> {

	IPage<Map<String, Object>> findListByPage(Page page,@Param("lpno") String lpno,@Param("seqno") String seqno,
			@Param("mtType") String mtType,@Param("mtResult") Integer mtResult,@Param("departmentId") Long departmentId, @Param("deleted") Integer deleted);

}
