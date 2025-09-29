package com.zcxd.base.job;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * @author songanwei
 * @date 2021-06-24
 */
@Component
public class CronSchedulerJob {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @PostConstruct
    public void init() throws SchedulerException {
        //获取任务调度器
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //调度器添加任务
        scheduleJob(scheduler);
    }

    private void scheduleJob(Scheduler scheduler) throws SchedulerException {
        //创建线路任务
        JobDetail jobDetail = JobBuilder.newJob(RouteJob.class) .withIdentity("routeJob", "routeGroup").build();
        //创建排班任务
        JobDetail dutyDetail = JobBuilder.newJob(DutyJob.class).withIdentity("dutyJob","dutyGroup").build();
        //创建修改早送晚收任务
        JobDetail boxpackTaskDetail = JobBuilder.newJob(BoxpackTaskJob.class).withIdentity("boxpackTaskJob","boxpackTaskGroup").build();
        
        //创建线路任务触发器【下午10点执行】
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 16 * * ?");
        //创建排班触发器【下午2点执行】
        CronScheduleBuilder scheduleBuilderT = CronScheduleBuilder.cronSchedule("0 0 12 * * ?");
        //创建修改早送晚收任务触发器【下午11点执行】
        CronScheduleBuilder scheduleBuilderC = CronScheduleBuilder.cronSchedule("0 0 23 * * ?");

        //任务、触发器绑定
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("routeTrigger", "routeGroup")
                .withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail,cronTrigger);

        CronTrigger cronTriggerT = TriggerBuilder.newTrigger().withIdentity("dutyTrigger","dutyGroup")
                .withSchedule(scheduleBuilderT).build();
        scheduler.scheduleJob(dutyDetail,cronTriggerT);
        
        CronTrigger cronTriggerC = TriggerBuilder.newTrigger().withIdentity("boxpackTaskTrigger","boxpackTaskGroup")
                .withSchedule(scheduleBuilderC).build();
        scheduler.scheduleJob(boxpackTaskDetail,cronTriggerC);
    }
}
