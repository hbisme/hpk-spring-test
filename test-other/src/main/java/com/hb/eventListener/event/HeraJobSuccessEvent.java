package com.hb.eventListener.event;

public class HeraJobSuccessEvent extends ApplicationEvent {
    private String historyId;
    private String jobId;
    private String staticEndTime;
    private String triggerType;

    public HeraJobSuccessEvent(String historyId, String jobId, String triggerType) {
        super("JobSucceed");
        this.historyId = historyId;
        this.jobId = jobId;
        this.triggerType = triggerType;
    }
}
