package com.hb.eventListener.event;

public class MvcEvent extends AbstractEvent {
    private ApplicationEvent applicationEvent;
    private String name;

    public MvcEvent(String type) {
        super(type);
    }

    public MvcEvent(Object dispatcher, ApplicationEvent applicationEvent) {
        super(dispatcher);
        this.applicationEvent = applicationEvent;
    }

    public ApplicationEvent getApplicationEvent() {
        return applicationEvent;
    }

    public void setApplicationEvent(ApplicationEvent applicationEvent) {
        this.applicationEvent = applicationEvent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


