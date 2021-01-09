package com.hb.eventListener.listener;

import com.hb.eventListener.event.AbstractEvent;

public interface Listener<E extends AbstractEvent> {
    void handleEvent(E event);
}
