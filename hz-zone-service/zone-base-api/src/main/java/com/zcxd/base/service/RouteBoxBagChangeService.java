package com.zcxd.base.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteBoxBagChangeMapper;
import com.zcxd.db.model.RouteBoxbagChange;

import lombok.AllArgsConstructor;
/**
 * 
 * @ClassName RouteBoxBagChangeService
 * @Description 线路交接信息调整记录
 * @author 秦江南
 * @Date 2022年11月21日上午11:00:28
 */
@AllArgsConstructor
@Service
public class RouteBoxBagChangeService extends ServiceImpl<RouteBoxBagChangeMapper, RouteBoxbagChange> {

}
