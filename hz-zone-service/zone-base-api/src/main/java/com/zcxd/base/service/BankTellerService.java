package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.BankTellerMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.BankTeller;

/**
 * 
 * @ClassName BankTellerService
 * @Description 早送晚收机构员工服务类
 * @author 秦江南
 * @Date 2021年11月23日上午9:10:10
 */
@Service
public class BankTellerService extends ServiceImpl<BankTellerMapper, BankTeller>{

	@Autowired
    private BankService bankService;
	
	/**
	 * 
	 * @Title getBankTellerByCondition
	 * @Description 按条件查询员工列表
	 * @param bankTeller
	 * @return
	 * @return 返回类型 List<BankTeller>
	 */
	public List<BankTeller> getBankTellerByCondition(BankTeller bankTeller) {
		QueryWrapper<BankTeller> queryWrapper = new QueryWrapper<>();
		if(bankTeller != null){
			if(!StringUtils.isBlank(bankTeller.getTellerNo())){
				queryWrapper.eq("teller_no", bankTeller.getTellerNo());
			}
			
			if(bankTeller.getBankId() != null){
				queryWrapper.eq("bank_id", bankTeller.getBankId());
			}
			
			if(bankTeller.getDepartmentId() != null){
				queryWrapper.eq("department_id", bankTeller.getDepartmentId());
			}
		}

		queryWrapper.eq("deleted", 0);
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询员工列表
	 * @param page
	 * @param limit
	 * @param bankTeller
	 * @return
	 * @return 返回类型 IPage<BankTeller>
	 */
	public IPage<BankTeller> findListByPage(Integer page, Integer limit, BankTeller bankTeller) {
		QueryWrapper<BankTeller> queryWrapper = new QueryWrapper<>();
        IPage<BankTeller> ipage=new Page<BankTeller>(page,limit);
        if(bankTeller != null) {
        	if (!StringUtils.isBlank(bankTeller.getTellerNo())) {
        		queryWrapper.eq("teller_no", bankTeller.getTellerNo());
        	}
        	
        	if (!StringUtils.isBlank(bankTeller.getTellerName())) {
        		queryWrapper.like("teller_name", bankTeller.getTellerName());
        	}
        	
        	if(bankTeller.getBankId() != null){
        		Bank bank = new Bank();
		    	bank.setBankType(1);
		    	bank.setParentIds("/" + bankTeller.getBankId() + "/");
		    	List<Map<String, Object>> bankList = bankService.getBankList(bank);
		    	List<String> bankIds = new ArrayList<String>();
		    	bankList.stream().forEach(bankTmp -> {
		    		bankIds.add(bankTmp.get("id").toString());
		    	});
		    	bankIds.add(bankTeller.getBankId()+"");
		    	queryWrapper.in("bank_id", bankIds);
        	}
        }
        
		queryWrapper.eq("deleted", 0);
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectPage(ipage, queryWrapper);
	}

}
