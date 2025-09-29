package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmClearTaskMapper;
import com.zcxd.db.mapper.VaultOrderTaskMapper;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.VaultOrderTask;
import com.zcxd.pda.dto.AtmClearSumDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName AtmClearTaskService
 * @Description 出入库订单任务记录
 * @author shijin
 * @Date 2021年8月19日上午10:20:05
 */
@Service
public class VaultOrderTaskService extends ServiceImpl<VaultOrderTaskMapper, VaultOrderTask> {

    /**
     * 根据订单查询 任务记录
     * @param id - 订单id
     * @param category - 任务类型(0 - atm加钞，1 - 备用金)
     * @return
     */
    public List<VaultOrderTask> listByOrderId(Long id, Integer category) {
        VaultOrderTask where = new VaultOrderTask();
        where.setOrderId(id);
        where.setCategory(category);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }
}
