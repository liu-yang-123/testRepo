package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.CashboxMapper;
import com.zcxd.db.mapper.PdaMapper;
import com.zcxd.db.model.Cashbox;
import com.zcxd.db.model.Pda;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @ClassName CashboxService
 * @Description 钞盒管理服务
 * @author shijin
 * @Date 2021年5月21日上午11
 */
@Service
public class CashboxService extends ServiceImpl<CashboxMapper, Cashbox> {

    /**
     * 根据RFID批量读取钞盒列表
     * @param rfids
     * @return
     */
    public List<Cashbox> listByBatchRfid(String[] rfids) {
        QueryWrapper<Cashbox> queryWrapper = new QueryWrapper<>();
        Set<String> rfidSet = new HashSet<>(Arrays.asList(rfids));
        queryWrapper.in("rfid",rfidSet);
        return baseMapper.selectList(queryWrapper);
    }

    public List<Cashbox> listByBatchBoxNo(List<String> boxNos) {
        QueryWrapper<Cashbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("box_no",boxNos);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据RFID查询cashbox
     * @param rfid
     * @return
     */
    public Cashbox getByRfid(String rfid) {
        Cashbox where  = new Cashbox();
        where.setRfid(rfid);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

    /**
     * 根据boxNo 查询
     * @param boxNo
     * @return
     */
    public Cashbox getByBoxNo(String boxNo) {
        Cashbox where  = new Cashbox();
        where.setBoxNo(boxNo);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }
}
