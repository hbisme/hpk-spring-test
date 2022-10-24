package priv.hb.sample.eventListener;

import priv.hb.sample.eventListener.handler.JobHandler;
import priv.hb.sample.eventListener.listener.HearJobSuccessListener;
import priv.hb.sample.eventListener.listener.HeraAddJobListener;
import priv.hb.sample.eventListener.listener.HeraJobFailListener;
import priv.hb.sample.eventListener.observable.Dispatcher;

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
