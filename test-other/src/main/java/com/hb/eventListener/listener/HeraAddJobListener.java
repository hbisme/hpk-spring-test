package com.hb.eventListener.listener;

import com.hb.eventListener.event.ApplicationEvent;
import com.hb.eventListener.MasterMain;
import com.hb.eventListener.event.HeraJobMaintenanceEvent;
import com.hb.eventListener.event.MvcEvent;
import com.hb.eventListener.handler.JobHandler;

public class HeraAddJobListener extends AbstractListener {

    public HeraAddJobListener() {
    }

    @Override
    public void beforeDispatch(MvcEvent mvcEvent) {
        HeraJobMaintenanceEvent maintenanceEvent = (HeraJobMaintenanceEvent) mvcEvent.getApplicationEvent();
        String actionId = maintenanceEvent.getId();
        JobHandler handler = new JobHandler(actionId);
        MasterMain.dispatcher.addJobHandler(handler);
        handler.handleEvent(new ApplicationEvent("Initialize"));
        mvcEvent.setCancelled(true);
        System.out.println("schedule add job with actionId: " + actionId);
    }


}
