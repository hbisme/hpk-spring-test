package com.hb.quarz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintWordsJob implements Job {

    private static int id ;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String value1 = context.getJobDetail().getJobDataMap().getString("自定义的jobKey1");
        Date value2 = (Date) context.getJobDetail().getJobDataMap().get("自定义的jobKey2");
        System.out.println("value1: " + value1);
        System.out.println("value2: " + value2);

        String printTime = new SimpleDateFormat("yy-MM-dd HH-mm-ss").format(new Date());
        System.out.println("PrintWordsJob start at:" + printTime + ", prints: Hello Job-" + id++);
    }
}
