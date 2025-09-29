package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VaultCheckMapper;
import com.zcxd.db.model.VaultCheck;
import org.springframework.stereotype.Service;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Service
public class VaultCheckService extends ServiceImpl<VaultCheckMapper, VaultCheck> {

    public Page<VaultCheck> findListByPage(Integer page, Integer limit, VaultCheck vaultCheck,Long beginDt,Long endDt) {
        Page<VaultCheck> ipage = new Page<>(page, limit);
        QueryWrapper<VaultCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",vaultCheck.getDepartmentId());
        if(vaultCheck != null){
            if(vaultCheck.getBankId() > 0){
                queryWrapper.eq("bank_id", vaultCheck.getBankId());
            }
        }
        if (beginDt != null) {
            queryWrapper.ge("create_time", beginDt);
        }
        if (endDt != null) {
            queryWrapper.le("create_time", endDt);
        }
        queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

}
