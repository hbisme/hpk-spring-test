package priv.hb.sample.eventListener.event;

public class HeraJobFailedEvent extends ApplicationEvent{
    private final String actionId;
    private final String triggerType;

    public HeraJobFailedEvent(String actionId, String triggerType) {
        super("JobFailed");
        this.actionId = actionId;
        this.triggerType = triggerType;
    }
}
