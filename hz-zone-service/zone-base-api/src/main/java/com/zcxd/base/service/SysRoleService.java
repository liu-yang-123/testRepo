package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SysRoleMapper;
import com.zcxd.db.model.SysRole;

/**
 * 
 * @ClassName SysRoleServiceImpl
 * @Description 角色管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:49:42
 */
@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {
	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询角色列表
	 * @param page
	 * @param limit
	 * @param role
	 * @return
	 * @return 返回类型 IPage<SysRole>
	 */
	public IPage<SysRole> findListByPage(Integer page, Integer limit, SysRole role){
    	Page<SysRole> ipage=new Page<SysRole>(page,limit);
    	QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
    	
    	if(role != null) {
	    	if(!StringUtils.isBlank(role.getRoleName())){
	    		queryWrapper.like("role_name",role.getRoleName());
	    	}
	    	
	    	if(role.getRoleType() != null){
	    		queryWrapper.like("role_type",role.getRoleType());
	    	}
    	}
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"create_time");
		
        return baseMapper.selectPage(ipage,queryWrapper);
    }

	
	/**
	 * 
	 * @Title getRoleByCondition
	 * @Description 根据条件查询角色集合
	 * @param role
	 * @return 返回类型 List<SysRole>
	 */
	public List<SysRole> getRoleByCondition(SysRole role) {
		
		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
		if(role != null){
			if(!StringUtils.isBlank(role.getRoleName())){
				queryWrapper.eq("role_name", role.getRoleName());
			}
			
			if(role.getRoleType() != null){
	    		queryWrapper.like("role_type",role.getRoleType());
	    	}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}


}
