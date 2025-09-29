package com.zcxd.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VehicleMapper;
import com.zcxd.db.model.Vehicle;
import org.springframework.stereotype.Service;

/**
 * @author lilanglang
 * @date 2021-10-18 10:35
 */
@Service
public class VehicleService extends ServiceImpl<VehicleMapper, Vehicle> {
}