package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.VaultOrderRecordMapper;
import com.zcxd.db.model.VaultOrderRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName VaultInoutRecordService
 * @Description 出入库单券别明细服务
 * @author shijin
 * @Date 2021年5月18日上午16:20:05
 */
@Service
public class VaultOrderRecordService extends ServiceImpl<VaultOrderRecordMapper, VaultOrderRecord> {

    /**
     * 根据订单id查询券别明细记录
     * @param orderId
     * @return
     */
    public List<VaultOrderRecord> getListByOrderId(long orderId) {

        VaultOrderRecord where = new VaultOrderRecord();
        where.setOrderId(orderId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 根据订单号删除券别明细
     * @param orderId
     * @return
     */
    public void deleteByOrderId(long orderId) {
        VaultOrderRecord where = new VaultOrderRecord();
        where.setOrderId(orderId);
        baseMapper.delete(new QueryWrapper<>(where));
    }

 }
