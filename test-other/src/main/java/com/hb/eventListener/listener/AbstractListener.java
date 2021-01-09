package com.hb.eventListener.listener;

import com.hb.eventListener.event.MvcEvent;

public class AbstractListener implements Listener<MvcEvent> {
    @Override
    public void handleEvent(MvcEvent event) {
        String type = event.getType();
        if (type.equals("before")) {
            beforeDispatch(event);
        } else if (type.equals("after")) {
            afterDispatch(event);
        }
    }

    public void beforeDispatch(MvcEvent mvcEvent) {

    }

    public void afterDispatch(MvcEvent mvcEvent) {

    }
}


