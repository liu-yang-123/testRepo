package com.zcxd.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**接口请求返回数据对象
 * @author songanwei
 * @date 2021/4/15 16:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    final static int  SUCCESSFUL_CODE = 0;
    final static String  SUCCESSFUL_MESSAGE = "请求成功";

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 泛型数据对象
     */
    private T data;

    /**
     * 返回系统请求失败结果
     * @param data
     * @param msg
     * @return
     */
    public static Result success(Object data,String msg) {
        return new Result<>(SUCCESSFUL_CODE, msg, data);
    }

    /**
     * 快速创建成功请求结果
     * @param data
     * @return
     */
    public static Result success(Object data) {
        return new Result<>(SUCCESSFUL_CODE, SUCCESSFUL_MESSAGE, data);
    }

    /**
     * 返回成功请求空数据结果
     * @return
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 返回系统请求失败结果
     * @param code
     * @param msg
     * @return
     */
    public static Result fail(Integer code,String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 返回系统请求失败结果
     * @param msg  错误信息提示
     * @return
     */
    public static Result fail(String msg) {
        return new Result<>(-1, msg, null);
    }

    /**
     * 系统异常类并返回结果数据
     * @return
     */
    public static Result fail() {
        return new Result<>(-1, "系统异常", null);
    }
    
    /**
     * Token异常
     * @return
     */
    public static Result Tokenfail() {
        return new Result<>(401, "Token异常", null);
    }

    /**
     * 失败
     * @return
     */
    public boolean isFailed() {
        return this.code != 0;
    }
}
