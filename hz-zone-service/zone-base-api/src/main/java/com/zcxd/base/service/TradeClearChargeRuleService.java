package com.zcxd.base.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.ClearErrorCancelDTO;
import com.zcxd.base.dto.ClearErrorCheckDTO;
import com.zcxd.base.dto.TradeClearChargeRuleDTO;
import com.zcxd.base.dto.TradeClearErrorDto;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.util.DenomUtil;
import com.zcxd.base.vo.TradeClearChargeRuleVO;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.mapper.TradeClearChargeRuleMapper;
import com.zcxd.db.mapper.TradeClearErrorMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.TradeClearChargeRule;
import com.zcxd.db.model.TradeClearError;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 商业清分-收费规则 服务类
 * </p>
 *
 * @author admin
 * @since 2020-09-16
 */
@Service
public class TradeClearChargeRuleService extends ServiceImpl<TradeClearChargeRuleMapper, TradeClearChargeRule> {
    @Resource
    private DenomMapper denomMapper;
    @Resource
    private BankMapper bankMapper;

    private TradeClearChargeRule getOne(long bankId,int gbFlag,String denom,String attr) {
        QueryWrapper wrapper = Wrappers.query().eq("bank_id",bankId)
                                                .eq("gb_flag",gbFlag)
                                                .eq("denom",denom)
                                                .eq("attr",attr)
                                                .eq("deleted", DeleteFlagEnum.NOT.getValue())
                                                .last("limit 1");
        List<TradeClearChargeRule> list = baseMapper.selectList(wrapper);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Result create(TradeClearChargeRuleVO tradeClearChargeRuleVO) throws BusinessException {
        TradeClearChargeRule existChargeRule = this.getOne(tradeClearChargeRuleVO.getBankId(),
                                                            tradeClearChargeRuleVO.getGbFlag(),
                                                            tradeClearChargeRuleVO.getDenom(),
                                                            tradeClearChargeRuleVO.getAttr());
        if (existChargeRule != null) {
            throw new BusinessException(-1,"重复添加");
        }

        TradeClearChargeRule newChargeRule = new TradeClearChargeRule();
        BeanUtils.copyProperties(tradeClearChargeRuleVO,newChargeRule);
        newChargeRule.setId(null);
        int iRet = baseMapper.insert(newChargeRule);
        return iRet>0? Result.success() : Result.fail();
    }

    public Result edit(TradeClearChargeRuleVO tradeClearChargeRuleVO) throws BusinessException {
        TradeClearChargeRule existChargeRule = this.getOne(tradeClearChargeRuleVO.getBankId(),
                tradeClearChargeRuleVO.getGbFlag(),
                tradeClearChargeRuleVO.getDenom(),
                tradeClearChargeRuleVO.getAttr());
        if (existChargeRule != null && existChargeRule.getId().equals(tradeClearChargeRuleVO.getId())) {
            throw new BusinessException(-1,"重复规则");
        }

        TradeClearChargeRule newChargeRule = new TradeClearChargeRule();
        BeanUtils.copyProperties(tradeClearChargeRuleVO,newChargeRule);
        int iRet = baseMapper.updateById(newChargeRule);
        return iRet>0? Result.success() : Result.fail();
    }

    public Result delete(int id) {
        int iRet = baseMapper.deleteById(id);
        return iRet>0? Result.success() : Result.fail();
    }

    public List<TradeClearChargeRuleDTO> listByBank(long bankId) {
        QueryWrapper wrapper = Wrappers.query().eq("bank_id",bankId)
                .eq("deleted", DeleteFlagEnum.NOT.getValue());
        List<TradeClearChargeRule> list = baseMapper.selectList(wrapper);

        List<TradeClearChargeRuleDTO> ruleDTOList = list.stream().map(tradeClearChargeRule -> {
            TradeClearChargeRuleDTO dto = new TradeClearChargeRuleDTO();
            BeanUtils.copyProperties(tradeClearChargeRule,dto);
            boolean isCoin = tradeClearChargeRule.getAttr().equals(DenomAttrEnum.COIN.getValue());
            String denomName = DenomUtil.denomValueToName(tradeClearChargeRule.getDenom(),isCoin);
            dto.setDenomName(denomName);
            return dto;
        }).collect(Collectors.toList());
        return ruleDTOList;
    }
}
