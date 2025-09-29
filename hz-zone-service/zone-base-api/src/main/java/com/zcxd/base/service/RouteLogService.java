package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteLogMapper;
import com.zcxd.db.model.RouteLog;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 
 * @ClassName RouteLogService
 * @Description 线路车长日志
 * @author shijin
 * @Date 2021年10月11日上午10:20:05
 */
@Service
public class RouteLogService extends ServiceImpl<RouteLogMapper, RouteLog> {

    public RouteLog getByRouteId(Long routeId) {
        RouteLog where = new RouteLog();
        where.setRouteId(routeId);
        List<RouteLog> routeLogList = baseMapper.selectList(new QueryWrapper<>(where));
        if (routeLogList.size() > 0) {
            return routeLogList.get(0);
        }
        return null;
    }
}
