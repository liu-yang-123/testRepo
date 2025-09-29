package com.zcxd.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * @author songanwei
 * @date 2021/4/29 10:27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

   /**
    * 内容值
    * @return
    */
   String value()  default "";

   /**
    * 操作类型
    * @return
    */
   OperateType type() default OperateType.ADD;
}
