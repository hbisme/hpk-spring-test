package priv.hb.sample.eventListener.listener;

import priv.hb.sample.eventListener.event.AbstractEvent;

public interface Listener<E extends AbstractEvent> {
    void handleEvent(E event);
}
