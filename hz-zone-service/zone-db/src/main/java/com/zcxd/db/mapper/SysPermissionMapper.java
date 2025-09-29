package com.zcxd.db.mapper;

import com.zcxd.db.model.SysPermission;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

	int insertBatch(@Param("sysPermissions") List<SysPermission> sysPermissions);

}
