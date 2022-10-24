package priv.hb.sample.eventListener.observable;

import priv.hb.sample.eventListener.event.ApplicationEvent;
import priv.hb.sample.eventListener.handler.AbstractHandler;
import priv.hb.sample.eventListener.handler.JobHandler;
import priv.hb.sample.eventListener.listener.AbstractListener;
import priv.hb.sample.eventListener.event.MvcEvent;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher extends AbstractObservable {
    public static final String beforeDispatch = "beforeDispatch";
    public static final String afterDispatch = "afterDispatch";

    List<AbstractHandler> jobHandlers;

    public Dispatcher() {
        jobHandlers = new ArrayList<>();
    }

    public void addJobHandler(JobHandler jobHandler) {
        jobHandlers.add(jobHandler);
    }

    public void addDispatcherListener(AbstractListener listener) {
        addListener("beforeDispatch", listener);
        addListener("afterDispatch", listener);
    }

    /**
     * 事件广播，每次任务状态变化，触发响应事件，全局广播，自动调度successEvent,触发依赖调度一些依赖更新
     * @param applicationEvent
     */
    public void dispatch(ApplicationEvent applicationEvent) {
        MvcEvent mvcEvent = new MvcEvent(this, applicationEvent);

        if (fireEvent("beforeDispatch", mvcEvent)) {
            for (AbstractHandler jobHandler : jobHandlers) {
                jobHandler.handleEvent(applicationEvent);
            }
            fireEvent("afterDispatch", mvcEvent);

        }
    }

    public List<AbstractHandler> getJobHandlers() {
        return jobHandlers;
    }
}
