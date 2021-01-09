package com.hb.eventListener.listener;

import com.hb.eventListener.event.HeraJobFailedEvent;
import com.hb.eventListener.event.MvcEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class HeraJobFailListener extends AbstractListener {
    private Executor executor;

    private List<String> alarms = new ArrayList<String>();

    public HeraJobFailListener() {
        alarms.add("hb");
    }

    @Override
    public void beforeDispatch(MvcEvent mvcEvent) {
        executor.execute(() -> {
            HeraJobFailedEvent failedEvent = (HeraJobFailedEvent) mvcEvent.getApplicationEvent();
            for (String alarm : alarms) {
                System.out.println("给" + alarm + "告警");
            }
        });
    }
}

