package com.zcxd.base.exception;

import lombok.Data;

/**
 * 业务异常类
 * @author songanwei
 * @date 2021/4/15 17:30
 */
@Data
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
