package com.zcxd.db.mapper;

import com.zcxd.db.model.DeviceMaintain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备维保记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface DeviceMaintainMapper extends BaseMapper<DeviceMaintain> {

    /**
     * 条件查询总数
     * @param deviceNo 设备编号
     * @param mtEngineer 维修工程师
     * @param mtType 维修类型
     * @param mtResult  维修结果
     * @param departmentId 部门ID
     * @return
     */
    int findTotal(@Param("deviceNo") String deviceNo,
                  @Param("mtEngineer") String mtEngineer,
                  @Param("mtType") String mtType,
                  @Param("mtResult") Integer mtResult,
                  @Param("departmentId") Long departmentId);

    /**
     * 条件查询列表数据
     * @param page
     * @param pageSize
     * @param deviceNo
     * @param mtEngineer
     * @param mtType
     * @param mtResult
     * @param departmentId 部门ID
     * @return
     */
    List<Map> findListByCondition(
                                  @Param("page") Integer page,
                                  @Param("pageSize") Integer pageSize,
                                  @Param("deviceNo") String deviceNo,
                                  @Param("mtEngineer") String mtEngineer,
                                  @Param("mtType") String mtType,
                                  @Param("mtResult") Integer mtResult,
                                  @Param("departmentId") Long departmentId);

}
