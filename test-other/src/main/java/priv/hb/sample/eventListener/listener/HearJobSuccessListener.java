package priv.hb.sample.eventListener.listener;

import priv.hb.sample.eventListener.event.HeraJobSuccessEvent;
import priv.hb.sample.eventListener.event.MvcEvent;

public class HearJobSuccessListener extends AbstractListener {
    public HearJobSuccessListener() {
    }

    @Override
    public void beforeDispatch(MvcEvent mvcEvent) {
        if(mvcEvent.getApplicationEvent() instanceof HeraJobSuccessEvent) {
            HeraJobSuccessEvent jobSuccessEvent = (HeraJobSuccessEvent) mvcEvent.getApplicationEvent();
            // do something
        }


    }
}
