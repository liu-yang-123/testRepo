package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmTaskRepairRecordMapper;
import com.zcxd.db.model.AtmTaskRepairRecord;
import org.springframework.stereotype.Service;


/**
 * 
 * @ClassName AtmSubTaskRepairService
 * @Description 维修任务管理服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmTaskRepairRecordService extends ServiceImpl<AtmTaskRepairRecordMapper, AtmTaskRepairRecord> {

    /**
     * 查询任务对应的维修任务
     * @param taskId
     * @return
     */
    public AtmTaskRepairRecord getByAtmTaskId(Long taskId) {
        AtmTaskRepairRecord where = new AtmTaskRepairRecord();
        where.setAtmTaskId(taskId);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }
}
