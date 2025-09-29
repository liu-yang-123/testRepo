package com.zcxd.db.mapper;

import com.zcxd.db.model.AtmDevice;

import java.util.List;
import java.util.Map;

import com.zcxd.db.model.result.AtmDeviceRouteResult;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * ATM设备信息 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface AtmDeviceMapper extends BaseMapper<AtmDevice> {

    /**
     * 查询总数
     * @param departmentId
     * @param terNo 设备编号
     * @return
     */
   Long getTotal(@Param("departmentId") Long departmentId,
                 @Param("terNo") String terNo);

    /**
     * 列表数据查询
     * @param departmentId
     * @param offset
     * @param limit
     * @param terNo 设备编号
     * @return
     */
   List<AtmDeviceRouteResult> getList(@Param("departmentId") Long departmentId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit,
                                      @Param("terNo") String terNo);
}
