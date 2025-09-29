package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.SchdResultRecordDTO;
import com.zcxd.db.mapper.SchdResultRecordMapper;
import com.zcxd.db.model.SchdResult;
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
     *
     * @param page  当前页数
     * @param limit  限制条数
     * @return
     */
    public IPage<SchdResultRecord> findListByPage(Integer page, Integer limit, Long departmentId){
        QueryWrapper<SchdResultRecord> queryWrapper = Wrappers.query();
        if (departmentId != null && departmentId > 0){
            queryWrapper.eq("department_id",departmentId);
        }
        queryWrapper.orderByDesc("create_time");
        IPage<SchdResultRecord> iPage = new Page<>(page,limit);
        return baseMapper.selectPage(iPage, queryWrapper);
    }

    /**
     * 检查当天是否已经生成
     * @param time
     * @return
     */
    public boolean checkTime(long time){
        QueryWrapper wrapper = Wrappers.query().eq("plan_day",time)
                .eq("op_type",0).last("LIMIT 1");
        SchdResultRecord record = baseMapper.selectOne(wrapper);
        return record != null;
    }


}