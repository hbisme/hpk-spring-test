package quarz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;

import java.util.Date;

/**
 * 每隔1秒执行一次任务的例子
 */
public class MyScheduler1 {
    public static void main(String[] args) throws SchedulerException, InterruptedException {

        // 1. 创建调度器Scheduler
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2. 创建JobDetail实例, 并与PrintWordsJob类绑定(Job执行内容), 并设置JobData, 便于真正运行的job获取到注入的对象
        JobDetail jobDetail = JobBuilder.newJob(PrintWordsJob.class)
                .usingJobData("自定义的jobKey1", "自定义的jobValue1")
                .withIdentity("job1", "group1").build();

        jobDetail.getJobDataMap().put("自定义的jobKey2", new Date());


        // 3. 构建Trigger实例,每隔1秒执行一次
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                // 设置间隔执行时间
                .withIntervalInSeconds(1)
                // 设置执行次数
                .repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .startNow()
                .withSchedule(builder).build();

        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println("-----scheduler start!----");
        scheduler.start();

        Thread.sleep(10000);
        scheduler.shutdown();
        System.out.println("----scheduler shutdown------");





    }


}
