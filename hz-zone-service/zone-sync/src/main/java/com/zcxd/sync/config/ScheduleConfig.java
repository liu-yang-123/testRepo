package com.zcxd.sync.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Schedule自动配置类
 * @author songanwei
 * @date 2021/4/16 10:40
 */
@Configuration
public class ScheduleConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //覆盖已存在的任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //延时60秒启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        schedulerFactoryBean.setStartupDelay(60);
        return schedulerFactoryBean;
    }

    /**
     *创建scheduler
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}
