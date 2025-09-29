package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmAdditionCashMapper;
import com.zcxd.db.model.AtmAdditionCash;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName AtmAdditionalCashService
 * @Description 线路备用金服务
 * @author shijin
 * @Date 2021年8月11日上午11:53:17
 */
@Service
public class AtmAdditionCashService extends ServiceImpl<AtmAdditionCashMapper, AtmAdditionCash>{

    /**
     *
     * @param page
     * @param limit
     * @param where
     * @return
     */
    public Page<AtmAdditionCash> findListByPage(Integer page, Integer limit, AtmAdditionCash where) {
        Page<AtmAdditionCash> ipage = new Page<>(page,limit);
        where.setDeleted(0);
        QueryWrapper<AtmAdditionCash> queryWrapper = new QueryWrapper<>(where);
//        queryWrapper.orderBy(true,false,"create_time");
        queryWrapper.orderBy(true,true,"id");

        return baseMapper.selectPage(ipage, queryWrapper);
    }


}
