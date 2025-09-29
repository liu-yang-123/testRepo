package com.zcxd.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.SchdResultRecordMapper;
import com.zcxd.db.model.SchdResultRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 排班结果记录 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdResultRecordService extends ServiceImpl<SchdResultRecordMapper, SchdResultRecord> {

    /**
     * @Description:根据记录生成日期得到信息
     * @Author: lilanglang
     * @Date: 2021/7/19 9:52
     * @param time:
     **/
    public SchdResultRecord getByPLanDate(Long time) {
        QueryWrapper<SchdResultRecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("plan_date",time);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * @Description:获取未推送的集合信息
     * @Author: lilanglang
     * @Date: 2021/7/19 14:26
     **/
    public List<SchdResultRecord> listByState() {
        QueryWrapper<SchdResultRecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("state",0);
        return this.baseMapper.selectList(queryWrapper);
    }
}