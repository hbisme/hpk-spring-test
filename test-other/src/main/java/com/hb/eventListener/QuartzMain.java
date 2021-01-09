package com.hb.eventListener;

import com.hb.eventListener.event.HeraScheduleTriggerEvent;
import com.hb.eventListener.observable.Dispatcher;

public class QuartzMain {
    public static void main(String[] args) {
        String jobId = "123";
        Dispatcher dispatcher = new Dispatcher();
        HeraScheduleTriggerEvent scheduledEvent = new HeraScheduleTriggerEvent(jobId);
        dispatcher.dispatch(scheduledEvent);
        System.out.println("execute schedule job: " + jobId);
    }
}
