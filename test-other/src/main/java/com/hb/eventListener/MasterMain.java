package com.hb.eventListener;

import com.hb.eventListener.handler.JobHandler;
import com.hb.eventListener.listener.HeraAddJobListener;
import com.hb.eventListener.listener.HeraJobFailListener;
import com.hb.eventListener.listener.HearJobSuccessListener;
import com.hb.eventListener.observable.Dispatcher;

import java.util.ArrayList;
import java.util.List;

public class MasterMain {

    public static Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {

        List<String> allJobList = new ArrayList<>();

        allJobList.forEach(actionId -> {
                    MasterMain.dispatcher.addJobHandler(new JobHandler(actionId));
                }
        );

        MasterMain.dispatcher.addDispatcherListener(new HeraAddJobListener());

        MasterMain.dispatcher.addDispatcherListener(new HearJobSuccessListener());

        MasterMain.dispatcher.addDispatcherListener(new HeraJobFailListener());


    }
}
