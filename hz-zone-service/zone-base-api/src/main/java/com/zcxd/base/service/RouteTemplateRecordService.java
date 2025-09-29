package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteTemplateRecordMapper;
import com.zcxd.db.model.RouteTemplateRecord;
import com.zcxd.db.model.SchdResultRecord;
import org.springframework.stereotype.Service;

/**
 * @author songanwei
 * @date 2021-08-16
 */
@Service
public class RouteTemplateRecordService extends ServiceImpl<RouteTemplateRecordMapper, RouteTemplateRecord> {


    /**
     *
     * @param page  当前页数
     * @param limit  限制条数
     * @param departmentId 部门ID
     * @return
     */
    public IPage<RouteTemplateRecord> findListByPage(Integer page, Integer limit, Long departmentId){
        QueryWrapper<RouteTemplateRecord> queryWrapper = Wrappers.query();
        if (departmentId != null && departmentId > 0){
            queryWrapper.eq("department_id",departmentId);
        }
        queryWrapper.orderByDesc("create_time");
        IPage<RouteTemplateRecord> iPage = new Page<>(page,limit);
        return baseMapper.selectPage(iPage, queryWrapper);
    }


}
