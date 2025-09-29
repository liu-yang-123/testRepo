package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmBankCheckRecordMapper;
import com.zcxd.db.mapper.AtmTaskCheckRecordMapper;
import com.zcxd.db.model.AtmBankCheckRecord;
import com.zcxd.db.model.AtmTaskCheckRecord;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName ATMTaskCheckRecordService
 * @Description 网点巡检记录服务类
 * @author shijin
 * @Date 2021年7月26日下午5:10:32
 */
@Service
public class ATMBankCheckRecordService extends ServiceImpl<AtmBankCheckRecordMapper, AtmBankCheckRecord>{

    public IPage<AtmBankCheckRecord>  findListByPage(int page,
                                                     int limit,
                                                     long bankId,
                                                     Long beginDate,
                                                     Long endDate,
                                                     Long subBankId,
                                                     Integer checkReuslt) {

        Page<AtmBankCheckRecord> ipage = new Page<>(page,limit);
        QueryWrapper<AtmBankCheckRecord> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("bank_id",bankId);
        if (null != beginDate) {
            queryWrapper.ge("check_time",beginDate);
        }
        if (null != endDate) {
            queryWrapper.le("check_time",endDate);
        }
        if (null != subBankId) {
            queryWrapper.eq("sub_bank_id",subBankId);
        }
        if (null != checkReuslt) {
            queryWrapper.eq("check_normal",checkReuslt);
        }

        queryWrapper.orderByDesc("check_time");
        return baseMapper.selectPage(ipage,queryWrapper);
    }
}
