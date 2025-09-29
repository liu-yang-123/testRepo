package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.CurrencyMapper;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.model.Currency;
import com.zcxd.db.model.Denom;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName DenomService
 * @Description 券别服务
 * @author shijin
 * @Date 2021年5月18日上午16:20:05
 */
@Service
public class CurrencyService extends ServiceImpl<CurrencyMapper, Currency> {

    public Currency getByCurCode(String curCode) {
        Currency where = new Currency();
        where.setCurCode(curCode);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

}
