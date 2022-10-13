package com.hb.quarz;

import org.junit.jupiter.api.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * 简单的crontab定时执行任务
 */
public class MyScheduler2 {

    @Test
    public void test1() throws SchedulerException {

        // 1. 创建调度器Scheduler
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2. 创建JobDetail实例, 并与PrintWordsJob类绑定(Job执行内容),并设置JobData, 便于真正运行的job获取到注入的对象
        JobDetail jobDetail = JobBuilder.newJob(PrintWordsJob.class)
                .usingJobData("自定义的jobKey1", "自定义的jobValue1")
                .withIdentity("job2", "group1").build();
        jobDetail.getJobDataMap().put("自定义的jobKey2", new Date());


        // 3. 构建Trigger实例
        Date startDate = new Date();
        // startDate.setTime(startDate.getTime() + 5000);

        Date endDate = new Date();
        endDate.setTime(startDate.getTime() + 500000);

        // 14点的每分钟执行一次
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 * 15 * * ?");

        Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .startNow()
                .startAt(startDate)
                .endAt(endDate)
                .withSchedule(builder).build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
        System.out.println("-----scheduler start!----");
        scheduler.start();

        // Thread.sleep(10000);
        // scheduler.shutdown();
        // System.out.println("----scheduler shutdown------");
        //


    }
}
