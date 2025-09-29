package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DeviceFactoryMapper;
import com.zcxd.db.model.DeviceFactory;
import com.zcxd.db.model.EmployeeJob;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author songanwei
 * @date 2021-05-14
 */
@Service
public class DeviceFactoryService extends ServiceImpl<DeviceFactoryMapper, DeviceFactory> {

    /**
     *
     * @param page
     * @param limit
     * @param deviceFactory
     * @return
     */
    public Page<DeviceFactory> findListByPage(Integer page, Integer limit, DeviceFactory deviceFactory) {
        Page<DeviceFactory> ipage = new Page<>(page,limit);
        QueryWrapper<DeviceFactory> queryWrapper = new QueryWrapper<>();
        if(deviceFactory != null){
            if(!StringUtils.isBlank(deviceFactory.getName())){
                queryWrapper.like("name", deviceFactory.getName());
            }
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    /**
     * 根据名称查找数据
     * @param deviceFactory
     * @return
     */
    public DeviceFactory getDeviceFactoryByCondition(DeviceFactory deviceFactory) {
        QueryWrapper<DeviceFactory> queryWrapper = new QueryWrapper<>();
        if(deviceFactory != null){
            if(!StringUtils.isBlank(deviceFactory.getName())){
                queryWrapper.eq("name", deviceFactory.getName());
            }
        }
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectOne(queryWrapper);
    }
}
