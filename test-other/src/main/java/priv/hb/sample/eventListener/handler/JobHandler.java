package priv.hb.sample.eventListener.handler;

import priv.hb.sample.eventListener.event.ApplicationEvent;
import priv.hb.sample.eventListener.event.HeraJobSuccessEvent;
import priv.hb.sample.eventListener.event.HeraScheduleTriggerEvent;

public class JobHandler extends AbstractHandler {

    private final String actionId;
    private final static String VOICE_PARAM = "FAULT_CODE:1,APP:任务%s,ERR_MSG:任务执行失败";
    private final static String RERUN_STATUS = "re-running";


    public JobHandler(String actionId) {
        this.actionId = actionId;
        registerEventType("Initialize");
    }

    @Override
    public void handleEvent(ApplicationEvent event) {
        if (event.getType().equals("Initialize")) {
            handleInitialEvent();
            return;
        }

        if (event instanceof HeraJobSuccessEvent) {
            handleSuccessEvent((HeraJobSuccessEvent) event);
        } else if (event instanceof HeraScheduleTriggerEvent) {
            handleTriggerEvent((HeraScheduleTriggerEvent) event);
        }

    }

    /**
     * 自动调度执行逻辑，如果没有版本，说明job被删除了，异常情况
     *
     * @param event
     */
    private void handleTriggerEvent(HeraScheduleTriggerEvent event) {
        String jobId = event.getJobId();
        // runJob(jobId);
    }

    // 收到广播的任务成功事件的处理流程，每次自动调度任务成功执行，会进行一次全局的SuccessEvent广播，使得依赖任务可以更新readyDependent
    private void handleSuccessEvent(HeraJobSuccessEvent event) {

    }


    public void handleInitialEvent() {

    }


    public String getActionId() {
        return actionId;
    }
}
