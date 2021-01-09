package com.hb.eventListener.observable;

import com.hb.eventListener.event.AbstractEvent;
import com.hb.eventListener.listener.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractObservable implements Observable {
    private boolean fireEvents = true;
    private Map<String, List<Listener<AbstractEvent>>> listMap = new HashMap<>(1024);
    private boolean activeEvent;

    @Override
    public void addListener(String eventType, Listener<? extends AbstractEvent> listener) {
        List<Listener<AbstractEvent>> listeners = listMap.get(eventType);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add((Listener) listener);
            listMap.put(eventType, listeners);
        } else {
            if (!listeners.contains(listener)) {
                listeners.add((Listener) listener);
                // listMap.put(eventType, listeners);

            }
        }


    }

    @Override
    public boolean fireEvent(String eventType, AbstractEvent abstractEvent) {
        if (fireEvents && listMap != null) {
            activeEvent = true;
            abstractEvent.setType(eventType);
            List<Listener<AbstractEvent>> listeners = listMap.get(eventType);
            for (Listener<AbstractEvent> listener : listeners) {
                callListener(listener, abstractEvent);
            }
            activeEvent = false;
            return !abstractEvent.isCancelled();
        }
        return true;
    }


    public void callListener(Listener<AbstractEvent> listener, AbstractEvent event) {
        listener.handleEvent(event);
    }

}
