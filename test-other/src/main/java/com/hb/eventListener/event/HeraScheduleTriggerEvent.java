package com.hb.eventListener.event;

public class HeraScheduleTriggerEvent extends ApplicationEvent {
    private final String jobId;

    public HeraScheduleTriggerEvent(String jobId) {
        super("ScheduleTrigger");
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }
}
