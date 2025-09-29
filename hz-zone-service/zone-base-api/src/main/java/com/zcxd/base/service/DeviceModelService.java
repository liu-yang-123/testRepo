package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DeviceModelMapper;
import com.zcxd.db.model.DeviceModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Service
public class DeviceModelService extends ServiceImpl<DeviceModelMapper, DeviceModel> {

    /**
     *
     * @param page
     * @param limit
     * @param deviceModel
     * @return
     */
    public Page<DeviceModel> findListByPage(Integer page, Integer limit, DeviceModel deviceModel) {
        Page<DeviceModel> ipage = new Page<>(page,limit);
        QueryWrapper<DeviceModel> queryWrapper = new QueryWrapper<>();
        if(deviceModel != null){
            if(!StringUtils.isBlank(deviceModel.getModelName())){
                queryWrapper.like("model_name", deviceModel.getModelName());
            }
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    /**
     * 根据名称查找数据
     * @param deviceModel
     * @return
     */
    public DeviceModel getDeviceModelByCondition(DeviceModel deviceModel) {
        QueryWrapper<DeviceModel> queryWrapper = new QueryWrapper<>();
        if(deviceModel != null){
            if(!StringUtils.isBlank(deviceModel.getModelName())){
                queryWrapper.eq("model_name", deviceModel.getModelName());
            }
            if (!StringUtils.isBlank(deviceModel.getDeviceType())){
                queryWrapper.eq("device_type", deviceModel.getDeviceType());
            }
            if (deviceModel.getFactoryId() > 0){
                queryWrapper.eq("factory_id", deviceModel.getFactoryId());
            }
        }
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectOne(queryWrapper);
    }

}
