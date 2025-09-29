package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.model.SchdDriveRestrict;
import com.zcxd.db.mapper.SchdDriveRestrictMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工作日汽车尾号限行规则 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdDriveRestrictService extends ServiceImpl<SchdDriveRestrictMapper, SchdDriveRestrict>{

    public List<SchdDriveRestrict> listByCondition(SchdDriveRestrict schdDriveRestrict) {
        QueryWrapper<SchdDriveRestrict> queryWrapper = new QueryWrapper<>();
        if (schdDriveRestrict.getWeekday() != null) {
            queryWrapper.eq("weekday", schdDriveRestrict.getWeekday());
        }
        if (schdDriveRestrict.getDepartmentId() != null) {
            queryWrapper.eq("department_id", schdDriveRestrict.getDepartmentId());
        }
        queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    public IPage<SchdDriveRestrict> listPage(int page, int limit, long departmentId) {
        Page<SchdDriveRestrict> pageQuery = new Page<>(page,limit);
        QueryWrapper<SchdDriveRestrict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId).eq("deleted", DeleteFlagEnum.NOT.getValue());
        queryWrapper.orderBy(true,true,"weekday");
        return baseMapper.selectPage(pageQuery,queryWrapper);
    }

    public SchdDriveRestrict getByWeekDay(Integer weekday,Long departmentId) {
        SchdDriveRestrict where = new SchdDriveRestrict();
        where.setWeekday(weekday);
        where.setDepartmentId(departmentId);
        where.setDeleted(0);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }
}
