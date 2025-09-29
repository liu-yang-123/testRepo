package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DenomTypeEnum;
import com.zcxd.common.constant.DenomVerFlagEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.VaultOrderMapper;
import com.zcxd.db.mapper.VaultVolumMapper;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.VaultOrder;
import com.zcxd.db.model.VaultVolum;
import com.zcxd.pda.dto.VaultVolumDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName VaultVolumService
 * @Description 金库余额账户服务
 * @author shijin
 * @Date 2021年5月19日上午13:20:05
 */
@Service
public class VaultVolumService extends ServiceImpl<VaultVolumMapper, VaultVolum> {


    /**
     * 统计库存余额
     * @return
     */
    public List<VaultVolumDTO> sumVaultVolum(Long departmentId) {
        QueryWrapper<VaultVolum> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("bank_id","sum(amount) as amount")
                .eq("department_id", departmentId)
                .groupBy("bank_id");
        List<Map<String,Object>> mapList = baseMapper.selectMaps(queryWrapper);
        List<VaultVolumDTO> vaultVolumDTOList = new ArrayList<>();
        mapList.forEach(stringObjectMap -> {
            VaultVolumDTO vaultVolumDTO = new VaultVolumDTO();
            vaultVolumDTO.setBankId(Long.parseLong(stringObjectMap.get("bank_id").toString()));
            vaultVolumDTO.setAmount(new BigDecimal(stringObjectMap.get("amount").toString()));
            vaultVolumDTOList.add(vaultVolumDTO);
        });

        return vaultVolumDTOList;
    }

    /**
     * 查询银行库存券别明细
     * @param bankId
     * @return
     */
    public List<VaultVolum> listBankVaultVolum(Long bankId) {
        VaultVolum where = new VaultVolum();
        where.setBankId(bankId);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 增加库存
     * @param bankId
     * @param denom
     * @param denomType
     * @param amount
     * @return
     */
    public void addVolum(Long departmentId,Long bankId, Denom denom, int denomType, BigDecimal amount, Long piecesCount) {
        VaultVolum where = new VaultVolum();
        where.setBankId(bankId);
        where.setDenomId(denom.getId());
        where.setDenomType(denomType);
        VaultVolum vaultVolum = baseMapper.selectOne(new QueryWrapper<>(where));
        if (null == vaultVolum) { //不存在，新增券别
            vaultVolum = new VaultVolum();
            BeanUtils.copyProperties(where,vaultVolum);
            vaultVolum.setDepartmentId(departmentId);
            vaultVolum.setCount(piecesCount);
            vaultVolum.setAmount(amount);
            baseMapper.insert(vaultVolum);
        } else { //增加余额
            if (amount != null) {
                vaultVolum.setAmount(vaultVolum.getAmount().add(amount));
            }
            if (piecesCount != null) {
                vaultVolum.setCount(vaultVolum.getCount() + piecesCount);
            }
            baseMapper.updateById(vaultVolum);
        }
    }

    /**
     * 扣减库存
     * @param bankId
     * @param denom
     * @param denomType
     * @param amount
     * @return
     */
    public int subVolum(Long bankId, Denom denom, int denomType, BigDecimal amount,Long piecesCount) {
        VaultVolum where = new VaultVolum();
        where.setBankId(bankId);
        where.setDenomId(denom.getId());
        where.setDenomType(denomType);
        VaultVolum vaultVolum = baseMapper.selectOne(new QueryWrapper<>(where));
        if (null == vaultVolum) {
            return 0;
        }
        if (denom.getVersion() == DenomVerFlagEnum.VER_BAD.getValue()) {
            if (vaultVolum.getCount() < piecesCount) {
                return 0;
            }
        } else {
            if (vaultVolum.getAmount().compareTo(amount) < 0) {
                return 0;
            }
        }
        vaultVolum.setAmount(vaultVolum.getAmount().subtract(amount));
        vaultVolum.setCount(vaultVolum.getCount() - piecesCount);
        baseMapper.updateById(vaultVolum);
        return 1;
    }
}
