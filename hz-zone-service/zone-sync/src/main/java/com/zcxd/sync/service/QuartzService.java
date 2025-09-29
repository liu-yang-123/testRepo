package com.zcxd.sync.service;

import org.quartz.SchedulerException;

/**
 * @author songanwei
 * @date 2021/4/16 11:38
 */
public interface QuartzService {

    /**
     * 初始化定时任务
     */
    void init() throws SchedulerException;

}
