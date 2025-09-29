package com.zcxd.base.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.db.mapper.SysPermissionMapper;
import com.zcxd.db.model.SysPermission;


/**
 * 
 * @ClassName SysPermissionServiceImpl
 * @Description 权限管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:48:51
 */
@Service
public class SysPermissionService extends ServiceImpl<SysPermissionMapper, SysPermission> {

	@Autowired
	private RedisUtil redisUtil;
	/**
	 * 根据roleid查询权限列表, 缓存存在，从缓存加载，如果不存在从数据库加载
	 * @param roleId
	 * @return
	 */
	public Set<String> getPermissionByRoleId(Long roleId) {
		String redisKey = CacheKeysDef.RoleAuthSetPrefix+roleId;
		if (redisUtil.hasKey(redisKey)) {
			Set<String> authSet = redisUtil.setMembers(redisKey);
			return authSet;
		} else {
			Set<String> authSet = new HashSet<>();
			List<SysPermission> list = getPermissionByRoleFromDB(roleId);
			if (list.size() > 0) {
				authSet = list.stream().map(SysPermission::getPermission).collect(Collectors.toSet());
				redisUtil.sAddAll(redisKey,authSet);
				redisUtil.expire(redisKey,1, TimeUnit.HOURS);
			}
			return authSet;
		}
	}

	/**
	 * 检查角色权限是否存在
	 * @param roleId - 角色id
	 * @param perssionUrl - 权限地址
	 * @return
	 */
	public boolean checkPerssionByRoleId(Long roleId,String perssionUrl) {
		String redisKey = CacheKeysDef.RoleAuthSetPrefix+roleId;
		if (redisUtil.hasKey(redisKey)) {
			return redisUtil.sIsMember(redisKey,perssionUrl);
		} else {
			Set<String> set = getPermissionByRoleId(roleId);
			return set.contains(perssionUrl);
		}
	}

	/**
	 * 淘汰缓存
	 * @param roleId
	 */
	public void deletePerssionCache(Long roleId) {
		String redisKey = CacheKeysDef.RoleAuthSetPrefix+roleId;
		redisUtil.delete(redisKey);
	}

	private List<SysPermission> getPermissionByRoleFromDB(Long roleId) {
		SysPermission where = new SysPermission();
		where.setRoleId(roleId);
		where.setDeleted(0);
		return baseMapper.selectList(new QueryWrapper<>(where));
	}
	/**
	 * 
	 * @Title getPermissionByRoleIds
	 * @Description 根据角色列表获取权限集合
	 * @param roleIds
	 * @return
	 * @return 返回类型 Set<String>
	 */
    public Set<String> getPermissionByRoleIds(String[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if(roleIds.length == 0){
            return permissions;
        }
        List<SysPermission> permissionList = baseMapper.selectList(new QueryWrapper<SysPermission>()
                .in("role_id", Arrays.asList(roleIds))
                .eq("deleted","0"));
        permissionList.stream().forEach(permission -> {
            permissions.add(permission.getPermission());
        });
        return permissions;
    }

//    /**
//     * 
//     * @Title savePermission
//     * @Description 保存权限列表
//     * @param permissions
//     * @param roleId
//     * @return 返回类型 void
//     */
//    @Transactional(rollbackFor=Exception.class)
//	public boolean savePermission(List<String> permissions, Long roleId) {
//		for (String permission : permissions) {
//			SysPermission Permission = new SysPermission();
//            Permission.setRoleId(roleId);
//            Permission.setPermission(permission);
//            int insert = baseMapper.insert(Permission);
//            if(insert <= 0)
//            	throw new BusinessException(-1,"保存权限列表失败");
//        }
//		return true;
//	}
	
//	/**
//	 * 
//	 * @Title deleteByRoleId
//	 * @Description 根据角色id删除权限
//	 * @param roleId
//	 * @return 返回类型 void
//	 */
//	public void deleteByRoleId(Long roleId) {
//        baseMapper.delete(new QueryWrapper<SysPermission>().eq("role_id",roleId));
//    }

	@Transactional(rollbackFor=Exception.class)
	public boolean updatePermission(List<SysPermission> sysPermissions, Long roleId) {
		baseMapper.delete(new QueryWrapper<SysPermission>().eq("role_id",roleId));
		
		if(sysPermissions.size()>0){
	        int insert = baseMapper.insertBatch(sysPermissions);
	        if(insert <= 0)
	        	throw new BusinessException(-1,"保存权限列表失败");
		}
		return true;
	}


}
