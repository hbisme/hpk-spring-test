package priv.hb.sample.eventListener;

import priv.hb.sample.eventListener.observable.Dispatcher;
import priv.hb.sample.eventListener.event.HeraScheduleTriggerEvent;

public class QuartzMain {
    public static void main(String[] args) {
        String jobId = "123";
        Dispatcher dispatcher = new Dispatcher();
        HeraScheduleTriggerEvent scheduledEvent = new HeraScheduleTriggerEvent(jobId);
        dispatcher.dispatch(scheduledEvent);
        System.out.println("execute schedule job: " + jobId);
    }
}
