package com.zcxd.db.mapper;

import com.zcxd.db.model.AtmTaskCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.AtmTaskCardResult;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 吐卡记录 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface AtmTaskCardMapper extends BaseMapper<AtmTaskCard> {

    /**
     * 分页返回列表数据
     * @param departmentId 部门ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @param terNo ATM设备编号
     * @return
     */
    Long getTotal(@Param("departmentId") Long departmentId,
                  @Param("start") Long start,
                  @Param("end") Long end,
                  @Param("terNo") String terNo);

    /**
     * 分页返回列表数据
     * @param departmentId 部门ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @param offset 起始位置
     * @param limit 限制数量
     * @param terNo ATM设备编号
     * @return
     */
    List<AtmTaskCardResult> getList(@Param("departmentId") Long departmentId,
                                    @Param("start") Long start,
                                    @Param("end") Long end,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit,
                                    @Param("terNo") String terNo);
}
