package priv.hb.sample.eventListener.event;

public class HeraJobMaintenanceEvent extends ApplicationEvent {
    private final String id;

    public HeraJobMaintenanceEvent(String type, String id) {
        super(type);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
