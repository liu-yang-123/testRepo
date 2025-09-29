package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmTaskCheckRecordMapper;
import com.zcxd.db.model.AtmTaskCheckRecord;
import org.springframework.stereotype.Service;


/**
 * 
 * @ClassName AtmSubTaskCheckService
 * @Description 巡检任务管理服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmTaskCheckRecordService extends ServiceImpl<AtmTaskCheckRecordMapper, AtmTaskCheckRecord> {


    public AtmTaskCheckRecord getByAtmTaskId(Long taskId) {
        AtmTaskCheckRecord where = new AtmTaskCheckRecord();
        where.setAtmTaskId(taskId);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }
}
