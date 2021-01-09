package com.hb.eventListener.event;

public class ApplicationEvent extends AbstractEvent {
    private Object data;
    public ApplicationEvent(String type) {
        super(type);
    }

    public ApplicationEvent(String type, Object data) {
        super(type);
        this.data = data;
    }

}
