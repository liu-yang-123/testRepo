package com.zcxd.db.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.TradeClearResultRecord;
import com.zcxd.db.model.result.TradeClearRecordQueryResult;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 清分结果明细 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
public interface TradeClearResultRecordMapper extends BaseMapper<TradeClearResultRecord> {

	/**
     * 批量添加
     *
     * @param recordList 列表数据
     * @return
     */
    Integer insertAll(List<TradeClearResultRecord> recordList);

    /**
     * 根据日期查询已完成任务的清分明细
     * @param departmentId
     * @param beginDate
     * @param endDate
     * @param bankId
     * @return
     */
    List<TradeClearRecordQueryResult> selectFinishRecords(@Param("departmentId") long departmentId,
                                                          @Param("beginDate") long beginDate,
                                                          @Param("endDate") long endDate,
                                                          @Param("bankId") Long bankId);

}
