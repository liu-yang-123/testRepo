package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.PdaMapper;
import com.zcxd.db.model.Pda;

/**
 * 
 * @ClassName PDAService
 * @Description 终端信息
 * @author 秦江南
 * @Date 2021年5月26日下午4:29:52
 */
@Service
public class PDAService extends ServiceImpl<PdaMapper, Pda>{

	/**
	 * 
	 * @Title getPdaByCondition
	 * @Description 根据条件查询终端设备
	 * @param pda
	 * @return
	 * @return 返回类型 List<Pda>
	 */
	public List<Pda> getPdaByCondition(Pda pda) {
		QueryWrapper<Pda> queryWrapper = new QueryWrapper<>();
		if(pda != null){
			if(!StringUtils.isBlank(pda.getTersn())){
				queryWrapper.eq("tersn", pda.getTersn());
			}
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询终端设备列表
	 * @param page
	 * @param limit
	 * @param pda
	 * @return
	 * @return 返回类型 IPage<Pda>
	 */
	public IPage<Pda> findListByPage(Integer page, Integer limit, Pda pda) {
		Page<Pda> ipage=new Page<Pda>(page,limit);
    	QueryWrapper<Pda> queryWrapper=new QueryWrapper<Pda>();
    	if(pda != null){
	    	if(!StringUtils.isEmpty(pda.getTersn())){
	    		queryWrapper.eq("tersn", pda.getTersn());
	    	}
	    	
	    	if(pda.getUseType() != null){
	    		queryWrapper.eq("use_type", pda.getUseType());
	    	}
	    	
	    	if(pda.getStatusT() != null){
	    		queryWrapper.eq("status_t", pda.getStatusT());
	    	}
	    	
	    	if(pda.getDepartmentId() != null){
	    		queryWrapper.eq("department_id", pda.getDepartmentId());
	    	}
    	}
    	queryWrapper.eq("deleted", 0);
        return baseMapper.selectPage(ipage, queryWrapper);
	}

}
