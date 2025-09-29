package com.zcxd.base.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.zcxd.base.service.BoxpackTaskService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.util.SpringBeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 线路定时触发任务
 * @author songanwei
 * @date 2021-06-24
 */
@Slf4j
@Component
public class RouteJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        //无法自动注入，通过应用上下文获取Bean实例对象
        RouteService routeService = (RouteService) SpringBeanUtils.getBean("routeService");
        log.info("=============定时任务执行======");
        Boolean b = routeService.initRoute();
        log.info("=============定时任务执行结果："+b);

		BoxpackTaskService boxpackTaskService = (BoxpackTaskService) SpringBeanUtils.getBean("boxpackTaskService");
        log.info("=============早送晚收下午定时任务执行======");
        Boolean c = boxpackTaskService.init();
        log.info("=============早送晚收下午定时任务执行结果："+c);
    }
}
