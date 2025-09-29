package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.VaultOrder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 
 * @ClassName VaultInoutService
 * @Description 出入库单管理服务
 * @author shijin
 * @Date 2021年5月18日上午16:20:05
 */
@Service
public class BankService extends ServiceImpl<BankMapper, Bank> {


    /**
     * 根据银行网点编码查询银行
     * @param bankNo
     * @return
     */
    public Bank getByBankNo(String bankNo) {
        Bank where = new Bank();
        where.setBankNo(bankNo);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

    public List<Bank> listAllTopBank(Long departmentId) {
        Bank where = new Bank();
        where.setDepartmentId(departmentId);
        where.setParentIds(Constant.TOP_BANK_PARENTS);
//        where.setBankType(BankTypeEnum.ATM.getValue());
        where.setHaveAtm(1);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        where.setStatusT(0);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }
    
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
    
    
}
