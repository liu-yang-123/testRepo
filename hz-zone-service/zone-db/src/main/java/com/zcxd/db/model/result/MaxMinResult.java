package com.zcxd.db.model.result;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 最大值、最小值、均值数据对象
 * @param <T>
 * @author songanwei
 */
@Data
public class MaxMinResult<T> {

    /**
     * 主键值
     */
    private T key;

    /**
     * 最小值
     */
    private BigDecimal min;

    /**
     * 最大值
     */
    private BigDecimal max;

    /**
     * 平均值
     */
    private BigDecimal average;

}
