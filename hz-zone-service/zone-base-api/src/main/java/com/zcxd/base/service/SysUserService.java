package com.zcxd.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SysUserMapper;
import com.zcxd.db.model.SysUser;

/**
 * 
 * @ClassName SysUserServiceImpl
 * @Description 用户管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:49:28
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

	@Autowired
	private RedisUtil redisUtil;


	public SysUser getUserById(Long id) {
		SysUser sysUser = null;
		String key = CacheKeysDef.SysUserPrefix+id;
		if (redisUtil.hasKey(key)) {
			String value = redisUtil.get(key);
			sysUser = JSON.parseObject(value,SysUser.class);
		} else {
			sysUser = baseMapper.selectById(id);
			if (null != sysUser) {
				String value = JSON.toJSONString(sysUser);
				redisUtil.setEx(key,value,1, TimeUnit.DAYS);
			}
		}
		return sysUser;
	}

	public void deleteSysUserCache(Serializable id) {
		String key = CacheKeysDef.SysUserPrefix+id;
		redisUtil.delete(key);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询用户列表
	 * @param page
	 * @param limit
	 * @param user
	 * @return 返回类型 Object
	 */
	 public IPage<SysUser> findListByPage(Integer page, Integer limit, SysUser user){
	    	Page<SysUser> ipage=new Page<SysUser>(page,limit);
	    	QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
	    	if(user!=null){
	    		if (!StringUtils.isBlank(user.getUsername())) {
					queryWrapper.like("username",user.getUsername());
				}
	    	}
	    	
	    	queryWrapper.eq("deleted", 0);
	    	queryWrapper.orderBy(true,false,"create_time");
			
	        return baseMapper.selectPage(ipage,queryWrapper);
	 }
	 

	 /**
	  * 
	  * @Title getUserByCondition
	  * @Description 根据条件查询用户集合
	  * @param user
	  * @return
	  * @return 返回类型 List<SysUser>
	  */
	public List<SysUser> getUserByCondition(SysUser user) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
		if(user != null){
			if(!StringUtils.isBlank(user.getRoleIds())){
				queryWrapper.like("role_ids", "/" + user.getRoleIds() + "/");
			}
			if(!StringUtils.isBlank(user.getUsername())){
					queryWrapper.eq("username", user.getUsername());
			}
			if(!StringUtils.isBlank(user.getNickName())){
				queryWrapper.like("nick_name", user.getNickName());
			}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

}
