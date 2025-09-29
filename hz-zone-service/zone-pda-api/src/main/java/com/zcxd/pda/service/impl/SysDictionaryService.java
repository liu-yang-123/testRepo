package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SysDictionaryMapper;
import com.zcxd.db.model.SysDictionary;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName SysDictionaryService
 * @Description 数据字典服务类
 * @author 秦江南
 * @Date 2021年5月13日下午7:47:41
 */
@Service
public class SysDictionaryService extends ServiceImpl<SysDictionaryMapper, SysDictionary>{

	/**
	 * 
	 * @Title getDictionaryByCondition
	 * @Description 根据条件查询数据字典集合
	 * @param dictionary
	 * @return
	 * @return 返回类型 List<SysDictionary>
	 */
	public List<SysDictionary> getDictionaryByCondition(SysDictionary dictionary) {
		QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
		if(dictionary != null){
			if(!StringUtils.isBlank(dictionary.getCode())){
				queryWrapper.eq("code", dictionary.getCode());
			}
			
			if(!StringUtils.isBlank(dictionary.getGroups())){
				queryWrapper.eq("groups", dictionary.getGroups());
			}
			
			if(!StringUtils.isBlank(dictionary.getContent())){
				queryWrapper.eq("content", dictionary.getContent());
			}
		}
		
		queryWrapper.eq("deleted", 0).orderByAsc("sort");
		return baseMapper.selectList(queryWrapper);
	}

}
