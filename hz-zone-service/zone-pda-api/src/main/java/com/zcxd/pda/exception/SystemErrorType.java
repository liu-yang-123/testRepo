package com.zcxd.pda.exception;

import lombok.Getter;

@Getter
public enum SystemErrorType implements com.zcxd.pda.exception.ErrorType {

    SYSTEM_ERROR(500, "系统异常"),
    SYSTEM_BUSY(500100, "系统繁忙,请稍候再试"),

    GATEWAY_NOT_FOUND_SERVICE(404, "服务未找到"),
    GATEWAY_ERROR(504, "网关异常"),

    ARGUMENT_NOT_VALID(20000, "请求参数校验不通过"),
    INVALID_TOKEN(20001, "无效token"),
    UPLOAD_FILE_SIZE_LIMIT(20010, "上传文件大小超过限制"),

    DUPLICATE_PRIMARY_KEY(30000,"唯一键冲突"),

    BUSINESS_PROCESS_ERROR(40000,"业务处理出错"),
    UPLOAD_TASK_NOTEXSIT(40001,"无效任务"),
    UPLOAD_TASK_REPEAT(40002,"重复上传"),
    UPLOAD_TASK_STATUS(40003,"状态不正确");
    /**
     * 错误类型码
     */
    private int code;
    /**
     * 错误类型描述信息
     */
    private String mesg;

    SystemErrorType(int code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
    public String getMesg() {
        return mesg;
    }
}
