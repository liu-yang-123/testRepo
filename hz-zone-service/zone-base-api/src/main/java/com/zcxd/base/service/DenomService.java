package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.model.Denom;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName DenomService
 * @Description 券别管理服务类
 * @author 秦江南
 * @Date 2021年5月21日下午2:26:17
 */
@Service
public class DenomService extends ServiceImpl<DenomMapper, Denom>{


	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询券别列表
	 * @param page
	 * @param limit
	 * @return
	 * @return 返回类型 IPage<Denom>
	 */
	public IPage<Denom> findListByPage(Integer page, Integer limit) {
		Page<Denom> ipage=new Page<Denom>(page,limit);
    	QueryWrapper<Denom> queryWrapper = new QueryWrapper<>();
    	
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,true,"sort");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}
	
	/**
	 * 
	 * @Title getDenomByCondition
	 * @Description 根据条件查询券别信息
	 * @param denom
	 * @return
	 * @return 返回类型 List<Denom>
	 */
	public List<Denom> getDenomByCondition(Denom denom){
		QueryWrapper<Denom> queryWrapper = new QueryWrapper<>();
		if(denom != null){
			if(denom.getValue() != null){
				queryWrapper.eq("value", denom.getValue());
			}
			
			if(denom.getCurCode() != null){
				queryWrapper.eq("cur_code", denom.getCurCode());
			}
			
			if(denom.getAttr() != null){
				queryWrapper.eq("attr", denom.getAttr());
			}
			
			if(denom.getVersion() != null){
				queryWrapper.eq("version", denom.getVersion());
			}
		}
		
		queryWrapper.eq("deleted", 0).orderByAsc("sort");
		return baseMapper.selectList(queryWrapper);
	}
}
