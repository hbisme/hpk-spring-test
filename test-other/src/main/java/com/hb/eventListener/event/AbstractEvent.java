package com.hb.eventListener.event;

public class AbstractEvent {
    private boolean cancelled;
    private Object source;
    private String type;

    public AbstractEvent() {
    }


    public AbstractEvent(String type) {
        this.type = type;
    }

    public AbstractEvent(Object source) {
        this.source = source;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "cancelled=" + cancelled +
                ", source=" + source +
                ", type='" + type + '\'' +
                '}';
    }
}
