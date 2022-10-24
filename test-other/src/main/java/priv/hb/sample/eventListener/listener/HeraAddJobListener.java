package priv.hb.sample.eventListener.listener;

import priv.hb.sample.eventListener.event.ApplicationEvent;
import priv.hb.sample.eventListener.MasterMain;
import priv.hb.sample.eventListener.event.HeraJobMaintenanceEvent;
import priv.hb.sample.eventListener.event.MvcEvent;
import priv.hb.sample.eventListener.handler.JobHandler;

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
