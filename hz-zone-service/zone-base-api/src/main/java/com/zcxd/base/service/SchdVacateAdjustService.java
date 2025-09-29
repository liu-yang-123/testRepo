package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.SchdVacateAdjustMapper;
import com.zcxd.db.model.SchdVacateAdjust;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工休息调整 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-20
 */
@Service
public class SchdVacateAdjustService extends ServiceImpl<SchdVacateAdjustMapper, SchdVacateAdjust>  {

    /**
     * 分页查询设置列表
     * @param page
     * @param limit
     * @return
     */
    public IPage<SchdVacateAdjust> listPage(int page,
                                            int limit,
                                            long departmentId,
                                            int planType,
                                            String adjustDate,
                                            Integer adjustType,
                                            String name) {

        Page<SchdVacateAdjust> ipage=new Page<>(page,limit);
        QueryWrapper<SchdVacateAdjust> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId)
                .eq("plan_type", planType)
                .eq("deleted", DeleteFlagEnum.NOT);

        if (adjustDate != null) {
            queryWrapper.eq("adjust_date",adjustDate);
        }
        if (adjustType != null) {
            queryWrapper.eq("adjust_type",adjustType);
        }
        return baseMapper.selectPage(ipage,queryWrapper);
    }

    public SchdVacateAdjust getByDateEmpId(String adjustDate, Long empId) {
        SchdVacateAdjust where = new SchdVacateAdjust();
        where.setAdjustDate(adjustDate);
        where.setEmpId(empId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

}
