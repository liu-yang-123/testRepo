package com.zcxd.base.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.zcxd.base.service.BoxpackTaskService;
import com.zcxd.base.util.SpringBeanUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BoxpackTaskJob implements Job {
	 @Override
	 public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		 BoxpackTaskService boxpackTaskService = (BoxpackTaskService) SpringBeanUtils.getBean("BoxpackTaskService");
	     log.info("=============早送晚收下午定时任务执行======");
	     Boolean c = boxpackTaskService.init();
	     log.info("=============早送晚收下午定时任务执行结果："+c);
	 }
}
 