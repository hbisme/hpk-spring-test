package priv.hb.sample.tool.quarz;

import org.junit.jupiter.api.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Date;
import java.util.List;

/**
 * 获取有哪些定时调度.
 */
public class MyScheduler3 {

    @Test
    public void test1() throws SchedulerException, InterruptedException {
        // 1. 创建调度器Scheduler
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2. 创建JobDetail实例, 并与PrintWordsJob类绑定(Job执行内容),并设置JobData, 便于真正运行的job获取到注入的对象
        JobDetail jobDetail = JobBuilder.newJob(PrintWordsJob.class)
                .usingJobData("自定义的jobKey1", "自定义的jobValue1")
                .withIdentity("job2", "group1").build();
        jobDetail.getJobDataMap().put("自定义的jobKey2", new Date());

        // 2. 创建JobDetail实例, 并与PrintWordsJob类绑定(Job执行内容),并设置JobData, 便于真正运行的job获取到注入的对象
        JobDetail jobDetail2 = JobBuilder.newJob(PrintWordsJob.class)
                .usingJobData("自定义的jobKey2", "自定义的jobValue2")
                .withIdentity("job22", "group2").build();
        jobDetail.getJobDataMap().put("自定义的jobKey2", new Date());





        // 3. 构建Trigger实例
        Date startDate = new Date();
        startDate.setTime(startDate.getTime() + 5000);

        Date endDate = new Date();
        endDate.setTime(startDate.getTime() + 5000000);

        // 14点的每分钟执行一次
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 07 19 * * ?");

        Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .startNow()
                // .startAt(startDate)
                // .endAt(endDate)
                .withSchedule(builder).build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
        System.out.println("-----scheduler start!----");



        CronScheduleBuilder builder2 = CronScheduleBuilder.cronSchedule("0 08 19 * * ?");

        Trigger cronTrigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "triggerGroup2")
                .startNow()
                // .startAt(startDate)
                // .endAt(endDate)
                .withSchedule(builder2).build();

        scheduler.scheduleJob(jobDetail2, cronTrigger2);
        System.out.println("-----scheduler start!----");


        scheduler.start();

        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " - " + nextFireTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Thread.sleep(1000000);
        scheduler.shutdown();
        System.out.println("----scheduler shutdown------");
        //


    }


}
