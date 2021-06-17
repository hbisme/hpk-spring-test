package com.hb.service;

public interface Service1 {
    Boolean WriteToRedis();

    String GetFromRedis();


    Boolean addToList() throws InterruptedException;


}
