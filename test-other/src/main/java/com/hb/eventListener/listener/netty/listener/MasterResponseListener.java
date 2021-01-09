package com.hb.eventListener.listener.netty.listener;

import java.util.concurrent.CountDownLatch;

public class MasterResponseListener extends ResponseListenerAdapter {

    private String request;
    private volatile Boolean receiveResult;
    private CountDownLatch latch;
    private String response;

    @Override
    public void onResponse(String response) {

        System.out.println(response);
        try {
            this.response = response;
            receiveResult = true;
        } catch (Exception e) {
            System.out.println("release lock exception {}");
        } finally {
            latch.countDown();
        }
    }

    public MasterResponseListener(String request, Boolean receiveResult, CountDownLatch latch, String response) {
        this.request = request;
        this.receiveResult = receiveResult;
        this.latch = latch;
        this.response = response;
    }

    public MasterResponseListener() {
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Boolean getReceiveResult() {
        return receiveResult;
    }

    public void setReceiveResult(Boolean receiveResult) {
        this.receiveResult = receiveResult;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}