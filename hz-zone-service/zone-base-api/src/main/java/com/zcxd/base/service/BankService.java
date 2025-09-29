package com.zcxd.base.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.zcxd.common.constant.BusinessModeEnum;
import com.zcxd.common.constant.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.BankCategoryTypeEnum;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.model.Bank;

/**
 * 
 * @ClassName BankService
 * @Description 机构管理服务类
 * @author 秦江南
 * @Date 2021年5月20日上午10:02:29
 */
@Service
public class BankService extends ServiceImpl<BankMapper, Bank> {

	/**
	 * 
	 * @Title getBankList
	 * @Description 根据条件查询网点树
	 * @param bank
	 * @return
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getBankList(Bank bank) {
		 QueryWrapper<Bank> queryWrapper=new QueryWrapper<>();
	     queryWrapper.select("id,short_name as name,SUBSTRING_INDEX(SUBSTRING_INDEX(parent_ids, '/', -2), '/', 1) as pid,bank_category as bankCategory");
	     if(bank != null){
	    	 if(bank.getBankType() != null){
	    		 queryWrapper.eq("bank_type", bank.getBankType());
	    	 }
	    	 if(bank.getDepartmentId() != null){
	    		 queryWrapper.eq("department_id", bank.getDepartmentId());
	    	 }
	    	 
	    	 if(!StringUtils.isBlank(bank.getRouteNo())){
	    		 queryWrapper.eq("route_no", bank.getRouteNo());
	    	 }
	    	 
			if (!StringUtils.isBlank(bank.getParentIds())) {
				queryWrapper.like("parent_ids", bank.getParentIds());
			}
	     }
	     queryWrapper.eq("deleted", 0);
	     return baseMapper.selectMaps(queryWrapper); 
	}

	public List<Map<String, Object>> getTopBankList(Long departmentId) {
		QueryWrapper<Bank> queryWrapper=new QueryWrapper<>();
		queryWrapper.select("id,short_name as name,SUBSTRING_INDEX(SUBSTRING_INDEX(parent_ids, '/', -2), '/', 1) as pid,bank_category as bankCategory");
		queryWrapper.eq("department_id", departmentId).eq("parent_ids", Constant.TOP_BANK_PARENTS).eq("deleted",0);
		return baseMapper.selectMaps(queryWrapper);
	}

	public List<Bank> listByCondition(Bank bank) {
		QueryWrapper<Bank> queryWrapper=new QueryWrapper<>(bank);
		if (bank.getDeleted() == null) {
			queryWrapper.eq("deleted",0);
		}
		return baseMapper.selectList(queryWrapper);
	}

	public List<Bank> listSubBank(Long topId, String name, Integer bankType, Integer departmentId) {
		String parentsIds = null;
		if (topId != null) {
			parentsIds = "/0/" + topId + "/";
		}

		LambdaQueryWrapper<Bank> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(Bank::getId, Bank::getBankNo, Bank::getFullName)
				.like(!StringUtils.isBlank(parentsIds), Bank::getParentIds, parentsIds)
				.like(!StringUtils.isBlank(name), Bank::getFullName, name)
				.eq(bankType != null, Bank::getBankType, bankType)
				.eq(Bank::getDeleted, 0)
				.eq(Bank::getDepartmentId, departmentId);
		return baseMapper.selectList(queryWrapper);
	}
	
	/**
	 * 
	 * @Title findListByIDs
	 * @Description 根据id列表分页查询网点信息
	 * @param page
	 * @param limit
	 * @param idList
	 * @return
	 * @return 返回类型 IPage<Bank>
	 */
	public IPage<Bank> findListByIDs(Integer page, Integer limit, List<String> idList) {
		Page<Bank> ipage=new Page<Bank>(page,limit);
    	QueryWrapper<Bank> queryWrapper=new QueryWrapper<Bank>();
    	queryWrapper.in("id", idList);
        return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * 查询顶级银行机构
	 * @param bank
	 * @return
	 */
	public List<Bank> getTopBank(Bank bank){
		QueryWrapper wrapper = Wrappers.query()
				.eq("parent_ids","/0/").eq("bank_type", bank.getBankType()).eq("status_t",0).eq("deleted",0);
		if(bank.getDepartmentId() != null) {
			wrapper.eq("department_id",bank.getDepartmentId());
		}
		return baseMapper.selectList(wrapper);
	}

	/**
	 * 查询顶级银行机构
	 * @param bank
	 * @return
	 */
	public List<Bank> getSubBank(Bank bank){

		QueryWrapper wrapper = Wrappers.query().select("id,short_name,route_no")
				.like("parent_ids","/"+bank.getId()+"/").eq("bank_type", bank.getBankType()).eq("status_t",0).eq("deleted",0);
		return baseMapper.selectList(wrapper);
	}
	
	/**
	 * 
	 * @Title getStorageBank
	 * @Description 获取部门库房机构
	 * @param departmentId
	 * @return
	 * @return 返回类型 Bank
	 */
    public Bank getStorageBank(Long departmentId) {
        Bank where = new Bank();
        where.setDepartmentId(departmentId);
        where.setBankCategory(BankCategoryTypeEnum.STORAGE.getValue());
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        where.setStatusT(0);
        List<Bank> list = baseMapper.selectList(new QueryWrapper<>(where));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<Bank> listTopBank(Long departmentId, BusinessModeEnum businessModeEnum) {
    	Bank where = new Bank();
    	if (businessModeEnum != null) {
			switch (businessModeEnum) {
				case ATM:
					where.setHaveAtm(1);
					break;
				case BOX:
					where.setHaveBox(1);
					break;
				case CLEAR:
					where.setHaveClear(1);
					break;
				case STORE:
					where.setHaveStore(1);
					break;
				default:
					break;
			}
		}
		where.setDepartmentId(departmentId);
		where.setParentIds(Constant.TOP_BANK_PARENTS);
		where.setDeleted(0);
		return baseMapper.selectList(new QueryWrapper<>(where));
	}
}
