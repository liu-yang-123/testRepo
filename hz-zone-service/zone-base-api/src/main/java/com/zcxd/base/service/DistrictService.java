package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DistrictMapper;
import com.zcxd.db.model.District;
import org.springframework.stereotype.Service;

/**
 * @ClassName  DistrictService
 * @Description 区域管理服务类
 * @author liuyang
 * @since 20250928
 */
@Service
public class DistrictService extends ServiceImpl<DistrictMapper, District> {

    /**
     *
     * @Title findListByPage
     * @Description 分页查询区域列表
     * @param page
     * @param limit
     * @return
     * @return 返回类型 IPage<District>
     */
    public IPage<District> findListByPage(Integer page, Integer limit){
        Page<District> ipage = new Page<District>(page, limit);
        QueryWrapper<District> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true, true,"id");

        return baseMapper.selectPage(ipage,queryWrapper);
    }
}
