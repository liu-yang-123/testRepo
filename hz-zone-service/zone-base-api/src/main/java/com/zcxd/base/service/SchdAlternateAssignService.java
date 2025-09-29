package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.SchdAlternateAssignMapper;
import com.zcxd.db.model.SchdAlternateAssign;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 司机主替班配置 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdAlternateAssignService extends ServiceImpl<SchdAlternateAssignMapper, SchdAlternateAssign> {

    /**
     * 验证主班数据是否已添加
     * @param planType
     * @param alternateType
     * @param routeIds
     * @param id
     * @return
     */
    public List<SchdAlternateAssign> validateExist(long planId, int planType, int alternateType, String routeIds, long id) {
        QueryWrapper wrapper = Wrappers.query()
                .eq("plan_id", planId)
                .eq("plan_type", planType)
                .eq("alternate_type",alternateType)
                .apply(!routeIds.isEmpty(),"FIND_IN_SET ('"+routeIds+"', route_ids)")
                .eq("deleted",DeleteFlagEnum.NOT.getValue());
        if (id > 0){
            wrapper.notIn("id", id);
        }
        return baseMapper.selectList(wrapper);
    }

    public IPage<SchdAlternateAssign> listPage(int page, int limit, long departmentId, int planType, long planId, long routeId, int alternateType, Set<Long> empIds) {
        Page<SchdAlternateAssign> planPage = new Page<>(page,limit);
        QueryWrapper queryWrapper = Wrappers.query().eq("plan_type",planType)
                .eq("department_id",departmentId)
                .eq("deleted",DeleteFlagEnum.NOT.getValue());
        if (planId > 0){
            queryWrapper.eq("plan_id", planId);
        }
        if (alternateType > -1){
            queryWrapper.eq("alternate_type", alternateType);
        }
        if (empIds!= null){
            queryWrapper.in("employee_id", empIds);
        }
        queryWrapper.apply(routeId > 0, "FIND_IN_SET (" + routeId +",route_ids)");
        queryWrapper.orderByDesc("id");
        return baseMapper.selectPage(planPage,queryWrapper);
    }
}
