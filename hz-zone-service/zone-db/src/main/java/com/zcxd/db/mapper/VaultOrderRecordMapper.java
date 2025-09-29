package com.zcxd.db.mapper;

import com.zcxd.db.model.VaultOrderRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.AtmDenomAmountResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出入库明细 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface VaultOrderRecordMapper extends BaseMapper<VaultOrderRecord> {

    /**
     * 批量添加
     *
     * @param recordList 列表数据
     * @return
     */
    Integer insertAll(List<VaultOrderRecord> recordList);

    /**
     *
     * @param bankId 银行机构ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @param orderType  0-入库、1-出库类型
     * @param  subType 0-ATM加钞 1-领缴款
     * @return
     */
    List<AtmDenomAmountResult> getDenomAmount(@Param("bankId") Long bankId,
                                              @Param("start") Long start,
                                              @Param("end") Long end,
                                              @Param("orderType") Integer orderType,
                                              @Param("subType") Integer subType);
}
