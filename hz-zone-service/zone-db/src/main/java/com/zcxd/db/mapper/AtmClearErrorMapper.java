package com.zcxd.db.mapper;

import com.zcxd.db.model.AtmClearError;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.result.CountAmountResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * atm清分差错明细 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-07-24
 */
public interface AtmClearErrorMapper extends BaseMapper<AtmClearError> {

    /**
     * 插入数据
     * @param errorList
     * @return
     */
    Integer insertAll(List<AtmClearError> errorList);

    /**
     * 根据类型分组查询数量以及总金额
     * @param departmentId
     * @param date
     * @param bankId
     * @return
     */
    List<CountAmountResult> getCountAmountGroupByType(@Param("departmentId") Long departmentId,
                                                            @Param("date") String date,
                                                            @Param("bankId") Long bankId);

}
