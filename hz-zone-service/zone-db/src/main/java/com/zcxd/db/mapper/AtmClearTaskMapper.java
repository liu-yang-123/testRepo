package com.zcxd.db.mapper;

import com.zcxd.db.model.AtmClearTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.CountAmountResult;
import com.zcxd.db.model.result.IdResult;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 清分任务 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface AtmClearTaskMapper extends BaseMapper<AtmClearTask> {

    /**
     * 批量插入数据
     * @param taskList
     * @return
     */
    Integer insertAll(List<AtmClearTask> taskList);

    /**
     * 根据任务日期、atmId查询数据
     * @param taskDate 任务日期
     * @param atmIdList atmId列表
     * @return
     */
    List<AtmClearTask> getByAtmIds(@Param("taskDate") String taskDate,
                                   @Param("list") Collection<Long> atmIdList);

    /**
     * 获取线路ID
     * @param taskDate 任务日期 yyyyMMdd
     * @return
     */
    List<Map> getRouteIds(@Param("taskDate") String taskDate, @Param("departmentId") Long departmentId);

    /**
     * 线路ID查询数据
     * @param routeId
     * @param taskDate 任务日期 格式 yyyyMMdd
     * @return
     */
    List<AtmClearTask> getByRoute(@Param("routeId") Long routeId,@Param("taskDate") String taskDate);

    /**
     * 银行机构ID查询清分数据
     * @param bankId 银行机构ID
     * @param taskDate 任务日期  yyyyMMdd
     * @return
     */
    List<AtmClearTask> getByBank(@Param("bankId") Long bankId, @Param("taskDate") String taskDate);

    /**
     * 按线路统计清分人当日清分总额
     * @param clearMan - 清分人/复核人
     * @param beginTime - 绑定开始日期
     * @param endTime - 绑定结束日期
     * @return
     */
    List<Map<String,Object>> sumClearManTotalAmountByRoute(
            @Param("clearMan") Long clearMan,
            @Param("beginTime") Long beginTime,
            @Param("endTime") Long endTime);

    /**
     * 根据时间范围查询银行计划清分金额和笔数
     * @param bankId 银行机构ID
     * @param date 查询日期 2021-10
     * @return
     */
    CountAmountResult getBankCountAmount(@Param("bankId") Long bankId,
                             @Param("date") String date);

    /**
     * 获取清分用户ID
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @return
     */
    List<IdResult> getClearManList(@Param("departmentId") Long departmentId,
                                   @Param("date") String date);

    /**
     * 获取复核用户ID
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @return
     */
    List<IdResult> getCheckManList(@Param("departmentId") Long departmentId,
                                   @Param("date") String date);

    /**
     * 获取时间范围内某一用户清分总额
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @param id 用户ID
     * @return
     */
    BigDecimal getAmountByUser(@Param("departmentId") Long departmentId,
                               @Param("date") String date,
                               @Param("id") Long id);

    /**
     * 获取时间范围内某一用户清分差错笔数
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @param id 用户ID
     * @return
     */
    Long getErrorCountByUser(@Param("departmentId") Long departmentId,
                             @Param("date") String date,
                             @Param("id") Long id);

    /**
     * 查询时间段内ATM清分金额
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @param atmIdList ATMid 列表
     * @return
     */
   List<CountAmountResult<Long>> getAmountByAtm(@Param("departmentId") Long departmentId,
                              @Param("date") String date,
                              @Param("atmIdList") List<Long> atmIdList);

    /**
     * 查询清分数量以及总金额
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @param bankId 银行机构ID
     * @param errorType 清分差错类型
     * @return
     */
   CountAmountResult getCountAmountByBank(@Param("departmentId") Long departmentId,
                                          @Param("date") String date,
                                          @Param("bankId") Long bankId,
                                          @Param("errorType") Integer errorType);

}
