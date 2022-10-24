package priv.hb.sample.eventListener.listener.netty.master;

import priv.hb.sample.eventListener.listener.netty.listener.ResponseListener;

import java.util.ArrayList;

public class MasterHandler {

    ArrayList<ResponseListener> listeners = new ArrayList<>();

    public void addListener(ResponseListener listener) {
        listeners.add(listener);
    }
}
