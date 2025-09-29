package com.zcxd.base.service;


import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SysWhitelistMapper;
import com.zcxd.db.model.SysWhitelist;

/**
 * 
 * @ClassName SysWhiteListService
 * @Description 白名单管理服务类
 * @author 秦江南
 * @Date 2021年5月12日下午7:22:31
 */
@Service
public class SysWhiteListService extends ServiceImpl<SysWhitelistMapper, SysWhitelist>{

	@Autowired
	private RedisUtil redisUtil;

	/**
	 *
	 * @param ip
	 * @param mac
	 * @param id
	 * @return
	 */
	public boolean checkExist(String ip, String mac, Long id){
		QueryWrapper<SysWhitelist> queryWrapper = new QueryWrapper<SysWhitelist>();
		if ( ip == null && mac == null){
			return false;
		}
		queryWrapper.eq("deleted",0);
		//验证IP是否存在
		if (!StringUtils.isBlank(ip)){
			queryWrapper.eq("ip_address", ip);
		}
		//验证MAC地址存在
		if (!StringUtils.isBlank(mac)) {
			queryWrapper.eq("mac_address", mac);
		}
		if (id != null && id > 0){
			queryWrapper.notIn("id", id);
		}
		queryWrapper.last("LIMIT 1");

		SysWhitelist whitelist = baseMapper.selectOne(queryWrapper);
		return whitelist != null;
	}

	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询白名单列表
	 * @param page
	 * @param limit
	 * @param whitelist
	 * @return
	 * @return 返回类型 Page<SysWhitelist>
	 */
	public Page<SysWhitelist> findListByPage(Integer page, Integer limit, SysWhitelist whitelist) {
		Page<SysWhitelist> ipage = new Page<SysWhitelist>(page, limit);
		QueryWrapper<SysWhitelist> queryWrapper = new QueryWrapper<>();
		if(whitelist != null){
			if(!StringUtils.isBlank(whitelist.getIpAddress())){
				queryWrapper.like("ip_address", whitelist.getIpAddress());
			}
		}
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		
		return baseMapper.selectPage(ipage, queryWrapper);
	}

	public Set<String> queryAllWhiteIps(int type) {
		String key = CacheKeysDef.WhiteIpsSet;
		String macKey = CacheKeysDef.WhiteMacsSet;
		Set<String> whiteIps = null;
		Set<String> whiteMacs = null;
		if (redisUtil.hasKey(key) || redisUtil.hasKey(macKey)) {
			whiteIps = redisUtil.setMembers(key);
			whiteMacs = redisUtil.setMembers(macKey);
		} else {
			List<SysWhitelist> list = baseMapper.selectList(new QueryWrapper<>());
			whiteIps = list.stream().map(SysWhitelist::getIpAddress).collect(Collectors.toSet());
			if (whiteIps.size() > 0){
				redisUtil.sAddAll(key,whiteIps);
				redisUtil.expire(key,1, TimeUnit.HOURS);
			}
			whiteMacs = list.stream().map(SysWhitelist::getMacAddress).collect(Collectors.toSet());
			if (whiteMacs.size() > 0){
				redisUtil.sAddAll(macKey,whiteMacs);
				redisUtil.expire(macKey,1, TimeUnit.HOURS);
			}
		}
		return type == 1? whiteIps : whiteMacs;
	}

	/**
	*淘汰缓存
	**/
	public void deleteWhiteListCache(){
		String redisKey = CacheKeysDef.WhiteIpsSet;
		String macKey = CacheKeysDef.WhiteMacsSet;
		redisUtil.delete(redisKey);
		redisUtil.delete(macKey);
	}
}
