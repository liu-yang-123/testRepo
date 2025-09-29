package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.RouteMapper;
import com.zcxd.db.model.Route;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName RouteService
 * @Description 线路管理服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class RouteService extends ServiceImpl<RouteMapper, Route> {

    /**
     * 查询当日线路任务
     * @param dateTime - 查询日期
     * @param routeStatusEnum - 线路状态过滤
     * @return
     */
    public List<Route> listRoute(Long departmentId,Long dateTime, RouteStatusEnum routeStatusEnum) {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId);
        if (dateTime != null) {
            long beginTime = DateTimeUtil.getDailyStartTimeMs(dateTime);
            long endTime = DateTimeUtil.getDailyEndTimeMs(dateTime);
            queryWrapper.ge("route_date",beginTime);
            queryWrapper.le("route_date",endTime);
        }
        queryWrapper.eq("status_t",routeStatusEnum.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 查询押运业务员关联任务
     * @param dateTime - 查询日期
     * @param userId - 业务员id
     * @return
     */
    public List<Route> listRouteByMan(Long dateTime, Long userId) {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        assert(dateTime != null);
        long beginTime = DateTimeUtil.getDailyStartTimeMs(dateTime);
        long endTime = DateTimeUtil.getDailyEndTimeMs(dateTime);
        queryWrapper.ge("route_date",beginTime).le("route_date",endTime)
                .ge("status_t",RouteStatusEnum.CHECKED.getValue()).eq("deleted",0);
        queryWrapper.and(wrapper -> wrapper.eq("route_key_man",userId).or().eq("route_oper_man",userId));

        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据线路编号和日期查询线路
     * @param dateTime - 查询日期
     * @param routeNo - 线路编号
     * @return
     */
    public Route getByRouteNo(Long dateTime, String routeNo) {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        if (dateTime != null) {
            long beginTime = DateTimeUtil.getDailyStartTimeMs(dateTime);
            long endTime = DateTimeUtil.getDailyEndTimeMs(dateTime);
            queryWrapper.ge("route_date",beginTime);
            queryWrapper.le("route_date",endTime);
        }
        queryWrapper.eq("route_no",routeNo).eq("deleted",0);
        List<Route> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
