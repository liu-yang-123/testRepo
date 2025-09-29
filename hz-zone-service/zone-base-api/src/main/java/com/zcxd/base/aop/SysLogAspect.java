package com.zcxd.base.aop;

import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.service.SysLogService;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面类
 * @author songanwei
 * @date 2021/4/29 10:51
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    private ThreadLocal<Long> threadLocalSysLogId = new ThreadLocal<>();

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Autowired
    private SysLogService logService;

    @Pointcut("@annotation(com.zcxd.common.annotation.OperateLog)")
    public void writeLog(){

    }

    @Before("writeLog()")
    public void doBefore(JoinPoint joinPoint){
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 添加请求操作日志
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        OperateLog opLog = targetMethod.getAnnotation(OperateLog.class);

        //请求方法全路径
        String action = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        if (opLog != null){
            //TODO 写入日志数据
            SysLog sysLog = new SysLog();
            sysLog.setIp(request.getHeader("x-forwarded-for"));
            sysLog.setUserId(UserContextHolder.getUserId());
            sysLog.setContent(opLog.value());
            sysLog.setTypeT(opLog.type().getVal());
            sysLog.setCreateTime(System.currentTimeMillis());
            sysLog.setAction(action);
            logService.save(sysLog);
            threadLocalSysLogId.set(sysLog.getId());
        }

    }

    @AfterReturning(returning = "ret", pointcut = "writeLog()")
    public void doAfterReturning(Result ret) {
        // 处理完请求，返回内容
        String message = "success";
        if (ret == null || ret.getCode() != 0){
            message = "failed";
        }
        //更新日志状态
        long logId = threadLocalSysLogId.get();
        logService.getBaseMapper().updateResult(logId,message);
        threadLocalSysLogId.remove();
    }

}

