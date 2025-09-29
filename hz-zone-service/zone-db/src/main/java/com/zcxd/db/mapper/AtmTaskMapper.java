package com.zcxd.db.mapper;


import java.util.List;
import java.util.Map;
import com.zcxd.db.model.result.*;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.AtmTask;

/**
 * <p>
 * ATM清机任务 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-07-22
 */
public interface AtmTaskMapper extends BaseMapper<AtmTask> {

	List<Map<String,Object>> findList(@Param("routeId") Long routeId,@Param("bankId") Long bankId,@Param("routeType") Integer routeType,@Param("deleted") Integer deleted);

	/**
	 * 日期查询，获取清机任务
	 * @param taskDate 任务日期
	 * @param departmentId 部门ID
	 * @return
	 */
	List<AtmTask> getAtmCleanTask(@Param("taskDate") Long taskDate,
								  @Param("departmentId") Long departmentId,
								  @Param("bankId") Long bankId);

    /**
     * 任务日期查询线路ID数据
     * @param departmentId  所属部门ID
     * @param taskDate  任务时间
     * @return
     */
    List<Map> getRouteIds(@Param("departmentId") long departmentId, @Param("taskDate") long taskDate);

    /**
     * 任务ID列表查询券别所对应的金额
     * 任务ID列表
     * @param taskIdList
     * @return
     */
    List<AtmDenomAmountResult> getDenomAmountList(@Param("list") List<Long> taskIdList);

	/**
	 *
	 * @param taskDate 任务日期
	 * @param departmentId 部门ID
	 * @param bankId 银行ID
	 * @return
	 */
	List<BankAtmTaskResult> getBankTaskList(@Param("taskDate") Long taskDate,
											@Param("departmentId") Long departmentId,
											@Param("bankId") Long bankId);

	/**
	 * 根据时间范围查询银行个类型任务数
	 * @param bankId 银行机构ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @return
	 */
	List<CountAmountResult<Integer>> getNumberGroupByType(@Param("bankId") Long bankId,
														  @Param("start") Long start,
														  @Param("end") Long end);

	/**
	 * 根据类型、时间范围查询任务数量
	 * @param bankId  银行机构ID
	 * @param start  开始时间戳
	 * @param end  结束时间戳
	 * @param status  状态
	 * @return
	 */
	Long getStatusNumber(@Param("bankId") Long bankId,
					   @Param("start") Long start,
					   @Param("end") Long end,
					   @Param("status") Integer status);

	/**
	 * 获取时间段范围内的银行券别列表数据
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @return
	 */
	List<AtmTask> getBankDenomList(@Param("departmentId") Long departmentId,
								   @Param("start") Long start,
								   @Param("end") Long end);

	/**
	 * 根据时间范围，查询某一类型、银行券别条件下的任务数量和金额
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param bankId 银行机构ID
	 * @param denomId 券别ID
	 * @param type 任务类型
	 * @return
	 */
	CountAmountResult getTypeCountAmount(@Param("start") Long start,
										 @Param("end") Long end,
										 @Param("bankId") Long bankId,
										 @Param("denomId") Long denomId,
										 @Param("type") Integer type);

	/**
	 * 根据时间范围，查询某一状态、银行券别条件下的任务数量和金额
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param bankId 银行机构ID
	 * @param denomId 券别ID
	 * @param status 任务状态
	 * @return
	 */
	CountAmountResult getStatusCountAmount(@Param("start") Long start,
										 @Param("end") Long end,
										 @Param("bankId") Long bankId,
										 @Param("denomId") Long denomId,
										 @Param("status") Integer status);

	/**
	 * 获取加款密码用户ID
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @return
	 */
	List<IdResult> getOpManList(@Param("departmentId") Long departmentId,
								@Param("start") Long start,
								@Param("end") Long end);

	/**
	 * 获取复核用户ID
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @return
	 */
	List<IdResult> getKeyManList(@Param("departmentId") Long departmentId,
								 @Param("start") Long start,
								 @Param("end") Long end);

	/**
	 * 查询天数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param id 用户ID
	 * @return
	 */
	Long getDaysByUser(@Param("departmentId") Long departmentId,
					   @Param("start") Long start,
					   @Param("end") Long end,
					   @Param("id") Long id);

	/**
	 * 查询密码员金额
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param empIdList 员工ID列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getAmountByUsersOpMan(@Param("departmentId") Long departmentId,
														@Param("start") Long start,
														@Param("end") Long end,
														@Param("empIdList") List<Long> empIdList,
														@Param("taskType") Integer taskType);

	/**
	 * 查询钥匙员金额
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param empIdList 员工ID列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getAmountByUsersKeyMan(@Param("departmentId") Long departmentId,
														@Param("start") Long start,
														@Param("end") Long end,
														@Param("empIdList") List<Long> empIdList,
														@Param("taskType") Integer taskType);

	/**
	 * 获取时间范围内某一用户加款任务笔数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param empIdList 员工ID列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getCountByUsersOpMan(@Param("departmentId") Long departmentId,
													   @Param("start") Long start,
													   @Param("end") Long end,
													   @Param("empIdList") List<Long> empIdList,
													   @Param("taskType") Integer taskType);

	/**
	 * 获取时间范围内某一用户加款任务笔数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param empIdList 员工ID列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getCountByUsersKeyMan(@Param("departmentId") Long departmentId,
													   @Param("start") Long start,
													   @Param("end") Long end,
													   @Param("empIdList") List<Long> empIdList,
													   @Param("taskType") Integer taskType);

	/**
	 * 查询设备的任务数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param atmIdList atmId 列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getCountByAtm(@Param("departmentId") Long departmentId,
					   @Param("start") Long start,
					   @Param("end") Long end,
					   @Param("atmIdList") List<Long> atmIdList,
					   @Param("taskType") Integer taskType);

	/**
	 * 查询设备的任务金额
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param atmIdList ATM id列表
	 * @param taskType 任务类型
	 * @return
	 */
	List<CountAmountResult<Long>> getAmountByAtm(@Param("departmentId") Long departmentId,
												 @Param("start") Long start,
												 @Param("end") Long end,
												 @Param("atmIdList") List<Long> atmIdList,
												 @Param("taskType") Integer taskType);

	/**
	 * 查询线路出勤天数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param routeNoList 线路编号
	 * @return
	 */
	List<CountAmountResult<String>> getDaysByRoute(@Param("departmentId") Long departmentId,
												   @Param("start") Long start,
												   @Param("end") Long end,
												   @Param("routeNoList") List<String> routeNoList);

	/**
	 * 查询任务总数
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param routeNoList 线路编号
	 * @return
	 */
	List<CountAmountResult<String>> getCountByRoute(@Param("departmentId") Long departmentId,
													@Param("start") Long start,
													@Param("end") Long end,
													@Param("routeNoList") List<String> routeNoList);

	/**
	 * 求任务平均时长
	 * @param departmentId 部门ID
	 * @param start 开始时间戳
	 * @param end 结束时间戳
	 * @param routeNoList 线路编号
	 * @return
	 */
	List<CountAmountResult<String>> getAvgTimeByRoute(@Param("departmentId") Long departmentId,
													  @Param("start") Long start,
													  @Param("end") Long end,
													  @Param("routeNoList") List<String> routeNoList);

}
