package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.SchdVacatePlanMapper;
import com.zcxd.db.model.SchdVacatePlan;
import com.zcxd.db.mapper.SchdVacateSettingMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工休息计划设置 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdVacatePlanService extends ServiceImpl<SchdVacatePlanMapper, SchdVacatePlan>  {

    /**
     * 分页查询设置列表
     * @param page
     * @param limit
     * @param name
     * @return
     */
    public IPage<SchdVacatePlan> listPage(int page, int limit, long departmentId,int planType, String name) {
        Page<SchdVacatePlan> ipage=new Page<>(page,limit);
        QueryWrapper<SchdVacatePlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId)
                .eq("plan_type",planType)
                .eq("deleted", DeleteFlagEnum.NOT);
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("begin_date");
        return baseMapper.selectPage(ipage,queryWrapper);
    }

    public List<SchdVacatePlan> listAll(long departmentId,int planType) {
        QueryWrapper<SchdVacatePlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId)
                .eq("plan_type",planType)
                .eq("deleted", DeleteFlagEnum.NOT)
                .orderByDesc("begin_date");
        return baseMapper.selectList(queryWrapper);
    }
}
