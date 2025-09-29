package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmTaskReturnMapper;
import com.zcxd.db.model.AtmTaskReturn;

/**
 * 
 * @ClassName ATMTaskReturnService
 * @Description 回笼钞盒经理服务类
 * @author 秦江南
 * @Date 2021年7月26日下午5:12:46
 */
@Service
public class ATMTaskReturnService extends ServiceImpl<AtmTaskReturnMapper, AtmTaskReturn>{
	
	/**
	 * 
	 * @Title getBarBoxCodes
	 * @Description 获取任务回笼钞盒编号数组
	 * @param taskId
	 * @return
	 * @return 返回类型 String[]
	 */
	public String[] getBarBoxCodes(Long taskId){
	     List<Map<String, Object>> selectMaps = 
	    		 baseMapper.selectMaps((QueryWrapper)Wrappers.query().select("box_bar_code").eq("task_id", taskId));
	     
	     List<String> list = new ArrayList<String>();
	     selectMaps.stream().forEach( tmp -> {
	    	 list.add(tmp.get("box_bar_code").toString());
	     });
	     return list.toArray(new String[list.size()]);
	}

	public List<AtmTaskReturn> findListByPage(Integer page, Integer limit, Long departmentId, String boxBarCode) {
		return baseMapper.findListByPage((page-1)*limit, limit, departmentId, boxBarCode);
	}
}
