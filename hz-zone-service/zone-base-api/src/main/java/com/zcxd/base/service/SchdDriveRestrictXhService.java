package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.model.SchdDriveRestrict;
import com.zcxd.db.model.SchdDriveRestrictXh;
import com.zcxd.db.mapper.SchdDriveRestrictXhMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 假期西湖限行规则 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdDriveRestrictXhService extends ServiceImpl<SchdDriveRestrictXhMapper, SchdDriveRestrictXh> {
    public List<SchdDriveRestrictXh> listByCondition(SchdDriveRestrictXh schdDriveRestrictXh) {
        QueryWrapper<SchdDriveRestrictXh> queryWrapper = new QueryWrapper<>();
        if (schdDriveRestrictXh.getDayType() != null) {
            queryWrapper.eq("day_type", schdDriveRestrictXh.getDayType());
        }
        if (schdDriveRestrictXh.getDepartmentId() != null) {
            queryWrapper.eq("department_id", schdDriveRestrictXh.getDepartmentId());
        }
        queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    public IPage<SchdDriveRestrictXh> listPage(int page, int limit, long departmentId) {
        Page<SchdDriveRestrictXh> pageQuery = new Page<>(page,limit);
        QueryWrapper<SchdDriveRestrictXh> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId).eq("deleted", DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectPage(pageQuery,queryWrapper);
    }
}
