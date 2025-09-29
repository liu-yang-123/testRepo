package com.zcxd.db.mapper;

import com.zcxd.db.model.Route;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.MaxMinResult;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 线路记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface RouteMapper extends BaseMapper<Route> {

    void updateIncFinishTotal(@Param("routeId") Long routeId, @Param("inc") Integer inc);

    /**
     * 批量添加
     * @param routeList
     * @return
     */
    Integer batchInsert(@Param("list") List<Route> routeList);

	List<Map> getTaskRoute(@Param("departmentId") Long departmentId,@Param("routeDate") Long routeDate,@Param("bankId") Long bankId,@Param("routeType") Integer routeType, @Param("deleted") Integer deleted);

    /**
     * 查询线路编号
     * @param departmentId 部门ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @return
     */
    List<Route> getRouteNoList(@Param("departmentId") Long departmentId,
                               @Param("start") Long start,
                               @Param("end") Long end);

    /**
     * 求最大、最小时间差
     * @param departmentId 部门ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @param routeNoList 线路编号列表
     * @return
     */
    List<MaxMinResult<String>> getMaxMinTime(@Param("departmentId") Long departmentId,
                                             @Param("start") Long start,
                                             @Param("end") Long end,
                                             @Param("routeNoList") List<String> routeNoList);
}
