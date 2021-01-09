package com.hb.eventListener.listener.netty.master;

import com.hb.eventListener.listener.netty.listener.ResponseListener;

import java.util.ArrayList;

public class MasterHandler {

    ArrayList<ResponseListener> listeners = new ArrayList<>();

    public void addListener(ResponseListener listener) {
        listeners.add(listener);
    }
}
