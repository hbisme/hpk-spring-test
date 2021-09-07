package com.hb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class Service1Impl implements Service1 {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean WriteToRedis() {
        stringRedisTemplate.opsForValue().set("hb1111", "hb100", 60 * 10, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public String GetFromRedis() {
        String res = stringRedisTemplate.opsForValue().get("hb1111");
        return res;
    }

    @Override
    public Boolean addToList() throws InterruptedException {

        for (int i = 0; i < 30; i++) {
            stringRedisTemplate.opsForList().leftPush("hb12" , "value" + i);
            Long size = stringRedisTemplate.opsForList().size("hb11");
            if (size > 30) {
                stringRedisTemplate.opsForList().rightPop("hb12");
            }
        }
        return true;
    }

    @Override
    public List<String> getFromRedisList() {
        List<String> res = stringRedisTemplate.opsForList().range("hb12", 0, 30);
        System.out.println(res);
        return res;
    }
}
