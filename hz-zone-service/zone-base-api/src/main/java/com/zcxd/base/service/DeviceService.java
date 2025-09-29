package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DeviceMapper;
import com.zcxd.db.model.Device;
import com.zcxd.db.model.DeviceFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author songanwei
 * @date 2021/5/12 16:51
 */
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

    /**
     *
     * @param page
     * @param limit
     * @param device
     * @return
     */
    public Page<Device> findListByPage(Integer page, Integer limit, Device device) {
        Page<Device> ipage = new Page<>(page,limit);
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        if(device != null){
            if(device.getDepartmentId() > 0){
                queryWrapper.eq("department_id", device.getDepartmentId());
            }
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    public Device getByDeviceNo(String deviceNo) {
        Device where = new Device();
        where.setDeviceNo(deviceNo);
        where.setDeleted(0);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }
}
