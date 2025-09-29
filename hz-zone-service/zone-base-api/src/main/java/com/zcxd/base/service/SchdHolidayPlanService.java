package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.base.vo.SchdHolidayPlanVO;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.model.SchdHolidayPlan;
import com.zcxd.db.mapper.SchdHolidayPlanMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.model.SchdVacateSetting;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 放假，调休安排 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdHolidayPlanService extends ServiceImpl<SchdHolidayPlanMapper, SchdHolidayPlan>  {

    public SchdHolidayPlan getByCondition(SchdHolidayPlan where) {
        if (where != null) {
            QueryWrapper<SchdHolidayPlan> queryWrapper = new QueryWrapper<>();
            if (where.getHolidayType() != null) {
                queryWrapper.eq("holiday_type", where.getHolidayType());
            }
            if (where.getPlanDay() != null) {
                queryWrapper.eq("plan_day", where.getPlanDay());
            }
            return baseMapper.selectOne(queryWrapper);
        }
        return null;
    }

    public IPage<SchdHolidayPlan> listPage(int page, int limit) {
        Page<SchdHolidayPlan> planPage = new Page<>(page,limit);
        QueryWrapper<SchdHolidayPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("plan_day");
        return baseMapper.selectPage(planPage,queryWrapper);
    }
}
