package com.zcxd.db.mapper;

import com.zcxd.db.model.VaultCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.BankAmountResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 金库盘点 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface VaultCheckMapper extends BaseMapper<VaultCheck> {

    /**
     * 在时间范围内查询银行每日库存金额
     * @param departmentId 部门ID
     * @param bankId 银行机构ID
     * @param start 开始时间戳
     * @param end 结束时间戳
     * @return
     */
    List<BankAmountResult> getBankAmount(@Param("departmentId") Long departmentId,
                                         @Param("bankId") Long bankId,
                                         @Param("start") Long start,
                                         @Param("end") Long end);

}
