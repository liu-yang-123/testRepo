package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.ATMDTO;
import com.zcxd.db.mapper.AtmDeviceMapper;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.Bank;

/**
 * 
 * @ClassName ATMService
 * @Description ATM设备管理
 * @author 秦江南
 * @Date 2021年5月27日下午3:53:09
 */
@Service
public class ATMService extends ServiceImpl<AtmDeviceMapper, AtmDevice>{
	
	@Autowired
    private BankService bankService;

	/**
	 * 
	 * @Title getATMByCondition
	 * @Description 根据条件查询ATM设备
	 * @param atmDevice
	 * @return
	 * @return 返回类型 List<AtmDevice>
	 */
	public List<AtmDevice> getATMByCondition(AtmDevice atmDevice) {
		QueryWrapper<AtmDevice> queryWrapper = new QueryWrapper<>();
		if(atmDevice != null){
			if(!StringUtils.isBlank(atmDevice.getTerNo())){
				queryWrapper.eq("ter_no", atmDevice.getTerNo());
			}
			
			if(atmDevice.getSubBankId() != null){
				queryWrapper.eq("sub_bank_id", atmDevice.getSubBankId());
			}
			
		}
		
		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	public AtmDevice getByTerNo(String terNo) {
		QueryWrapper<AtmDevice> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("ter_no", terNo)
				.eq("deleted", 0);
		List<AtmDevice> list = baseMapper.selectList(queryWrapper);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 查询ATM设备分页列表
	 * @param page
	 * @param limit
	 * @param atmDevice
	 * @param routeNo 
	 * @return
	 * @return 返回类型 IPage<AtmDevice>
	 */
	public IPage<AtmDevice> findListByPage(Integer page, Integer limit, AtmDevice atmDevice, String routeNo) {
		Page<AtmDevice> ipage=new Page<AtmDevice>(page,limit);
    	QueryWrapper<AtmDevice> queryWrapper=new QueryWrapper<AtmDevice>();
    	if(atmDevice != null){
	    	if(!StringUtils.isEmpty(atmDevice.getTerNo())){
	    		queryWrapper.like("ter_no", atmDevice.getTerNo());
	    	}
	    	
//	    	if(!StringUtils.isEmpty(atmDevice.getTerType())){
//	    		queryWrapper.eq("ter_type", atmDevice.getTerType());
//	    	}
//	    	
//	    	if(!StringUtils.isEmpty(atmDevice.getTerFactory())){
//	    		queryWrapper.eq("ter_factory", atmDevice.getTerFactory());
//	    	}
	    	
			if (atmDevice.getBankId() != null) {
		    	Bank bank = new Bank();
		    	bank.setBankType(0);
		    	bank.setParentIds("/" + atmDevice.getBankId() + "/");
		    	if(!StringUtils.isBlank(routeNo)){
		    		bank.setRouteNo(routeNo);
		    	}
		    	List<Map<String, Object>> bankList = bankService.getBankList(bank);
		    	List<String> bankIds = new ArrayList<String>();
		    	bankList.stream().forEach(bankTmp -> {
		    		bankIds.add(bankTmp.get("id").toString());
		    	});
		    	bankIds.add(atmDevice.getBankId()+"");
		    	queryWrapper.in("sub_bank_id", bankIds);
			}
	    	
    	}
    	queryWrapper.eq("deleted", 0);
        return baseMapper.selectPage(ipage, queryWrapper);
	}


//	/**
//	 * 
//	 * @Title getATMOption
//	 * @Description ATM设备下拉选项数据
//	 * @param bankId
//	 * @return
//	 * @return 返回类型 List<Map<String,Object>>
//	 */
//	public List<Map<String, Object>> getATMOption(Long bankId) {
//		return baseMapper.getATMOption(bankId);
//	}

	/**
	 *
	 * @param idList  atm设备ID列表
	 * @return
	 */
	public List<ATMDTO> findListByIds(List<Long> idList) {
		if (idList.size() == 0){
			return new LinkedList<>();
		}
		QueryWrapper queryWrapper = Wrappers.query().in("id",idList).eq("deleted",0);
        List<AtmDevice> deviceList = baseMapper.selectList(queryWrapper);

        return deviceList.stream().map(item -> {
        	ATMDTO atmdto = new ATMDTO();
			BeanUtils.copyProperties(item, atmdto);
			return atmdto;
		}).collect(Collectors.toList());
	}


}
