package priv.hb.sample.eventListener.observable;

import priv.hb.sample.eventListener.event.AbstractEvent;
import priv.hb.sample.eventListener.listener.Listener;

public interface Observable {
    void addListener(String eventType, Listener<? extends AbstractEvent> listener);
    boolean fireEvent(String eventType, AbstractEvent abstractEvent);
}
