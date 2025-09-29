package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VaultVolumMapper;
import com.zcxd.db.model.VaultVolum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Service
public class VaultVolumeService extends ServiceImpl<VaultVolumMapper, VaultVolum> {

    public Page<VaultVolum> findListByPage(Integer page, Integer limit, VaultVolum vaultVolum) {
        Page<VaultVolum> ipage = new Page<>(page, limit);
        QueryWrapper<VaultVolum> queryWrapper = new QueryWrapper<>();

        if(vaultVolum != null){
            if (vaultVolum.getDepartmentId() != null) {
                queryWrapper.eq("department_id",vaultVolum.getDepartmentId());
            }
            if(vaultVolum.getBankId() > 0){
                queryWrapper.eq("bank_id", vaultVolum.getBankId());
            }
        }
        queryWrapper.select("id, department_id,bank_id,denom_type,denom_id,SUM(amount) AS amount,SUM(`count`) AS count");
        queryWrapper.groupBy("bank_id");
        queryWrapper.orderBy(true,false,"bank_id");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    public List<VaultVolum> getBankList(Long bankId){
        QueryWrapper queryWrapper = Wrappers.query().eq("bank_id",bankId);
        return baseMapper.selectList(queryWrapper);
    }

}
