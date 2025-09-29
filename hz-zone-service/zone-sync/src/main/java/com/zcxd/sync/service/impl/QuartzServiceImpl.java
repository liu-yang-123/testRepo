package com.zcxd.sync.service.impl;

import com.zcxd.sync.service.QuartzService;
import com.zcxd.sync.service.schedule.QuartzJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author songanwei
 * @description
 * @date 2021/04/16
 */
@Service
public class QuartzServiceImpl implements QuartzService {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    @Override
    public void init() throws SchedulerException {
        //定时任务具体操作实现
        String taskName = "UploadDataJob";
        //数据库查询数据同步触发时间
        String expression = "00 00 10 * * ?";
        System.out.println("定时任务初始化...");
        //创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(taskName)
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .startNow()
                .build();

        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                .withIdentity(taskName)
                .build();

        //调度作业
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
