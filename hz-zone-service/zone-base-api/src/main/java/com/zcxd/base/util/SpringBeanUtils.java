package com.zcxd.base.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author songanwei
 * @description
 * @date 2020/04/16
 */
@Component
public class SpringBeanUtils implements ApplicationContextAware {

    /**
     * Spring应用上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.applicationContext = applicationContext;
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Bean Id获取Bean实例
     * @param clz
     * @return
     * @throws BeansException
     */
    public static Object getBean(String clz) throws BeansException {
        return applicationContext.getBean(clz);
    }
}
