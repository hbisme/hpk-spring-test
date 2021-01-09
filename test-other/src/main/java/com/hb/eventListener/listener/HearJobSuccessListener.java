package com.hb.eventListener.listener;

import com.hb.eventListener.event.HeraJobSuccessEvent;
import com.hb.eventListener.event.MvcEvent;

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
