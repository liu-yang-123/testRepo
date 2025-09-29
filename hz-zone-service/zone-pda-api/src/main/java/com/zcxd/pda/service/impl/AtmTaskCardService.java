package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.BankCardStatusEnum;
import com.zcxd.db.mapper.AtmTaskCardMapper;
import com.zcxd.db.model.AtmTaskCard;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName AtmTaskCardService
 * @Description atm吞卡记录表
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmTaskCardService extends ServiceImpl<AtmTaskCardMapper, AtmTaskCard> {

    public List<AtmTaskCard> listByCondition(AtmTaskCard where) {
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    public List<AtmTaskCard> listDispatchCard(Long deliverRouteId) {
        QueryWrapper<AtmTaskCard> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("deliver_route_id",deliverRouteId).and(i -> i.eq("status_t", BankCardStatusEnum.DISPATCH.getValue())
                .or().eq("status_t", BankCardStatusEnum.DELIVER.getValue()));
        return baseMapper.selectList(queryWrapper);
    }
}
