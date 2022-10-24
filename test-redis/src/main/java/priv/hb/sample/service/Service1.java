package priv.hb.sample.service;

import java.util.List;

public interface Service1 {
    Boolean WriteToRedis();

    String GetFromRedis();


    Boolean addToList() throws InterruptedException;


    List<String> getFromRedisList();


}
