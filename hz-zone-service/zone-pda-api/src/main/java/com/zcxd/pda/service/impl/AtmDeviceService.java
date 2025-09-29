package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmDeviceMapper;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 
 * @ClassName AtmDeviceService
 * @Description ATM设备服务
 * @author shijin
 * @Date 2021年5月21日
 */
@Service
public class AtmDeviceService extends ServiceImpl<AtmDeviceMapper, AtmDevice> {

    public List<AtmDevice> listByAtmNos(Set<String> atmNos) {
        QueryWrapper<AtmDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("ter_no",atmNos).eq("status_t",0).eq("deleted",0);
        return baseMapper.selectList(queryWrapper);
    }

    public AtmDevice getByAtmNo(String atmNo) {
        QueryWrapper<AtmDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("ter_no",atmNo).eq("status_t",0).eq("deleted",0);
        return baseMapper.selectOne(queryWrapper);
    }
}
