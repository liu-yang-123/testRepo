package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.DeviceMapper;
import com.zcxd.db.mapper.PdaMapper;
import com.zcxd.db.model.Device;
import com.zcxd.db.model.Pda;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName DeviceService
 * @Description 清分设备管理服务
 * @author shijin
 * @Date 2021年5月18日上午14:50:05
 */
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

    /**
     * 根据设备编号查询
     * @param deviceNo
     * @return
     */
    public Device getByDevNo(String deviceNo) {
        Device where = new Device();
        where.setDeviceNo(deviceNo);
        where.setDeleted(0);
        List<Device> list = baseMapper.selectList(new QueryWrapper<>(where).last("limit 1"));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
