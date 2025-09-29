package com.zcxd.common.util;

import lombok.Data;

/**
 * 键值关系泛型对象
 * @author songanwei
 * @param <T>
 * @param <M>
 */
@Data
public class KeyValue<T,M> {

    /**
     * 泛型键
     */
    private T key;

    /**
     * 泛型值
     */
    private M value;

}
