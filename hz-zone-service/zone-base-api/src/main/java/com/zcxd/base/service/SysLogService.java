package com.zcxd.base.service;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SysLogMapper;
import com.zcxd.db.model.SysLog;

/**
 * 
 * @ClassName SysLogServiceImpl
 * @Description 日志管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:50:05
 */
@Service
public class SysLogService extends ServiceImpl<SysLogMapper, SysLog> {

	/**
	 * 	
	 * @Title findListByPage
	 * @Description 分页获取日志信息
	 * @param page
	 * @param limit
	 * @param log 
	 * @param ids
	 * @return 返回类型 IPage<SysLog>
	 */
	public IPage<SysLog> findListByPage(Integer page, Integer limit, SysLog log, Set<Long> ids) {
		
		Page<SysLog> ipage=new Page<SysLog>(page,limit);
    	QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
    	
    	if(ids.size() >0 ){
    		queryWrapper.in("user_id",ids);
    	}
    	
    	if(log.getTypeT() != null){
    		queryWrapper.eq("type_t",log.getTypeT());
    	}
    	
    	if(!StringUtils.isBlank(log.getResult())){
    		queryWrapper.eq("result",log.getResult());
    	}
    	
    	queryWrapper.orderBy(true,false,"create_time");
		
        return  baseMapper.selectPage(ipage,queryWrapper);
	}
}
