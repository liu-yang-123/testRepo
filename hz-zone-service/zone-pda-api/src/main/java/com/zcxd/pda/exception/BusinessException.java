package com.zcxd.pda.exception;

import lombok.Data;

/**
 * 业务异常类
 * @author songanwei
 * @date 2021/4/15 17:30
 */
@Data
public class BusinessException extends BaseException {

//    private int code;
//
//    public BusinessException(int code, String message) {
//        super(SystemErrorType.BUSINESS_PROCESS_ERROR,msg);
//    }

    public BusinessException(String message) {
        super(SystemErrorType.BUSINESS_PROCESS_ERROR,message);
    }
}
