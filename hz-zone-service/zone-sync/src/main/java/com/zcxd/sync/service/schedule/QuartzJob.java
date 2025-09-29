package com.zcxd.sync.service.schedule;

import com.zcxd.sync.util.SpringBeanUtils;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import java.lang.reflect.Method;

/**
 * @author songanwei
 * @description
 * @date 2021/04/16
 */
public class QuartzJob  implements Job {

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext){
         String beanName = "sync";
         String methodName = "upload";
        //获取对应的Bean
        Object object = SpringBeanUtils.getBean(beanName);
        try {
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(methodName);
            method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
