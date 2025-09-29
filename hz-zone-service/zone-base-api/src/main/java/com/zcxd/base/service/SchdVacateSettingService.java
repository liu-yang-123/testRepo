package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.SchdVacateSettingMapper;
import com.zcxd.db.model.SchdVacateSetting;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 员工休息管理 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdVacateSettingService extends ServiceImpl<SchdVacateSettingMapper, SchdVacateSetting>  {

    
    List<SchdVacateSetting> listByBatchEmpId(Long planId, Set<Long> empIds) {
        QueryWrapper<SchdVacateSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id",planId).in("emp_id",empIds).eq("deleted", DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     *
     * @param page
     * @param limit
     * @param planId
     * @param empIds
     * @return
     */
    IPage<SchdVacateSetting> listPage(int page,int limit, long planId, Set<Long> empIds, Integer weekday) {
        Page<SchdVacateSetting> settingIPage = new Page<>(page,limit);
        QueryWrapper<SchdVacateSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id",planId).eq("deleted", DeleteFlagEnum.NOT.getValue());
        if (empIds != null && empIds.size() > 0) {
            queryWrapper.in("emp_id", empIds);
        }
        //查询星期
        queryWrapper.apply(weekday != null, "FIND_IN_SET (" + weekday +",vacate_days)");

        queryWrapper.orderByAsc("id");
        return baseMapper.selectPage(settingIPage,queryWrapper);
    }

    List<SchdVacateSetting> listByPlanId(long planId,Set<Long> empIds) {
        QueryWrapper<SchdVacateSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id",planId).eq("deleted", DeleteFlagEnum.NOT.getValue());
        if (empIds != null && empIds.size() > 0) {
            queryWrapper.in("emp_id", empIds);
        }
        queryWrapper.orderByAsc("id");
        return baseMapper.selectList(queryWrapper);
    }

}
