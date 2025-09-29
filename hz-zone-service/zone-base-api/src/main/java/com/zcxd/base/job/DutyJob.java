package com.zcxd.base.job;

import com.zcxd.base.service.SchdResultServiceImpl;
import com.zcxd.base.util.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 排版定时任务
 * @author songanwei
 * @date 2021-08-02
 */
@Slf4j
@Component
public class DutyJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SchdResultServiceImpl resultService = (SchdResultServiceImpl) SpringBeanUtils.getBean("schdResultServiceImpl");
        resultService.init();
        log.info("======排班定时任务结果生成========");
    }
}
