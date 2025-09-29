package com.zcxd.db.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.SysUser;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
