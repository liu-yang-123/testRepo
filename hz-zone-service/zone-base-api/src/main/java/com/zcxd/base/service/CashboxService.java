package com.zcxd.base.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.CashboxMapper;
import com.zcxd.db.model.Cashbox;

/**
 * 
 * @ClassName CashboxService
 * @Description 钞盒管理服务类
 * @author 秦江南
 * @Date 2021年5月24日上午9:43:45
 */
@Service
public class CashboxService extends ServiceImpl<CashboxMapper, Cashbox>{

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询钞盒列表
	 * @param page
	 * @param limit
	 * @param cashbox
	 * @return
	 * @return 返回类型 IPage<Cashbox>
	 */
	public IPage<Cashbox> findListByPage(Integer page, Integer limit, Cashbox cashbox) {
		Page<Cashbox> ipage=new Page<Cashbox>(page,limit);
    	QueryWrapper<Cashbox> queryWrapper = new QueryWrapper<>();
		
    	if(cashbox != null){
    		if (cashbox.getUsed() != null) {
    			queryWrapper.eq("used",cashbox.getUsed());
    		}
    		if (cashbox.getBoxType() != null) {
    			queryWrapper.eq("box_type",cashbox.getBoxType());
    		}
    		
	    	if(!StringUtils.isBlank(cashbox.getBoxNo())){
	    		queryWrapper.like("box_no",cashbox.getBoxNo());
	    	}
	    	
	    	if(!StringUtils.isBlank(cashbox.getRfid())){
	    		queryWrapper.like("rfid",cashbox.getRfid());
	    	}
    	}
    	
    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"id");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}

	public List<String> listAllBoxNo(Integer type) {
		QueryWrapper<Cashbox> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("box_no").eq("box_type",type);
		List<Cashbox> cashboxList = baseMapper.selectList(queryWrapper);
		return cashboxList.stream().map(Cashbox::getBoxNo).collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @Title getCashboxByCondition
	 * @Description 根据条件查询钞盒列表
	 * @param cashbox
	 * @return
	 * @return 返回类型 List<Cashbox>
	 */
	public List<Cashbox> getCashboxByCondition(Cashbox cashbox){
		QueryWrapper<Cashbox> queryWrapper = new QueryWrapper<>();
		if(cashbox != null){
			if(cashbox.getRfid() != null){
				queryWrapper.eq("rfid", cashbox.getRfid());
			}
		}
		queryWrapper.eq("deleted", 0).orderByAsc("id");
		return baseMapper.selectList(queryWrapper);
	}


}
