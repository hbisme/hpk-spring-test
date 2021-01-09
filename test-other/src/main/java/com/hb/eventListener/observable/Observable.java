package com.hb.eventListener.observable;

import com.hb.eventListener.event.AbstractEvent;
import com.hb.eventListener.listener.Listener;

public interface Observable {
    void addListener(String eventType, Listener<? extends AbstractEvent> listener);
    boolean fireEvent(String eventType, AbstractEvent abstractEvent);
}
