package com.zcxd.pda.exception;

import com.zcxd.common.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 子系统全局异常处理器
 * @author songanwei
 * @date 2021/4/15 17:27
 */
@Slf4j
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常捕获
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleRRException(BusinessException e){
        log.info("业务异常:" + e.getMessage());
        return Result.fail(e.getErrorType().getCode(),e.getMessage());
    }

    /**
     * 参数校验异常类捕获
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validationMethodException(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError.getDefaultMessage();
        return Result.fail(message);
    }

    /**
     * 参数验证异常捕获
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public Result validationBodyException(BindException e){
        BindingResult result = e.getBindingResult();
        //如果有错误，则返回第一个错误信息提示
        String message = "请求参数错误";
        if (result.hasErrors()) {
            Optional<ObjectError> error = result.getAllErrors().stream().findFirst();
            FieldError fieldError = (FieldError) error.get();
            log.warn("参数验证错误: object{"+fieldError.getObjectName()+"},field{"+fieldError.getField()+
                    "},errorMessage{"+fieldError.getDefaultMessage()+"}");
            message = fieldError.getDefaultMessage();
        }
        return Result.fail(message);
    }
    /**
     * 默认异常捕获
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        System.out.println(e.getStackTrace());
        StringBuffer emsg = new StringBuffer();
        if(e!=null){
            StackTraceElement[] st = e.getStackTrace();
            for (StackTraceElement stackTraceElement : st) {
                String exclass = stackTraceElement.getClassName();
                String method = stackTraceElement.getMethodName();
                emsg.append("[类:" + exclass + "]调用"+ method + "时在第" + stackTraceElement.getLineNumber()
                        + "行代码处发生异常!异常类型:" + e.toString()+"\r\n");
                break;
            }
        }
        System.out.println(emsg.toString());
        //TODO 记录异常日志
        return Result.fail(e.getMessage());
    }

}
