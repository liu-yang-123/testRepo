package com.zcxd.base.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteEmpChangeMapper;
import com.zcxd.db.model.RouteEmpChange;

import lombok.AllArgsConstructor;
/**
 * 
 * @ClassName RouteEmpChangeService
 * @Description 线路人员调整服务类
 * @author 秦江南
 * @Date 2021年9月2日下午3:23:21
 */
@AllArgsConstructor
@Service
public class RouteEmpChangeService extends ServiceImpl<RouteEmpChangeMapper, RouteEmpChange> {

}
