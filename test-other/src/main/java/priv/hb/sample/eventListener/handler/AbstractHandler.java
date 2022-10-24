package priv.hb.sample.eventListener.handler;

import priv.hb.sample.eventListener.event.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHandler {
    protected List<AbstractHandler> children;
    protected boolean initialized;
    protected AbstractHandler parent;

    private List<String> supportedEvents;

    public boolean canHandle(ApplicationEvent event) {
        return canHandle(event, true);
    }

    public boolean canHandle(ApplicationEvent event, boolean bubbleDown) {
        if (supportedEvents != null && supportedEvents.contains(event.getType())) {
            return true;
        }
        if (children != null && bubbleDown) {
            for (AbstractHandler child : children) {
                if (child.canHandle(event, bubbleDown)) {
                    return true;
                }
            }

        }
        return false;
    }

    public abstract void handleEvent(ApplicationEvent event);

    protected void registerEventType(String type) {
        if (supportedEvents == null) {
            supportedEvents = new ArrayList<>();
        }

        if (type != null) {
            if (!supportedEvents.contains(type)) {
                supportedEvents.add(type);
            }
        }
    }


}
