package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.BankTellerMapper;
import com.zcxd.db.mapper.PdaMapper;
import com.zcxd.db.model.BankTeller;
import com.zcxd.db.model.Pda;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName PdaService
 * @Description PDA设备管理服务
 * @author shijin
 * @Date 2021年5月18日上午14:50:05
 */
@Service
public class PdaService extends ServiceImpl<PdaMapper, Pda> {

    /**
     * 根据sn号码查询
     * @param terSN
     * @return
     */
    public Pda getByTerSN(String terSN) {
        Pda where = new Pda();
        where.setTersn(terSN);
        where.setDeleted(0);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

}
