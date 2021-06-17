package com.hb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

        for (int i = 1; i < 35; i++) {
            stringRedisTemplate.opsForList().leftPush("hb11" , "value" + i);
            // stringRedisTemplate.opsForList().rightPush("hb11", "value1");
            // stringRedisTemplate.opsForList().rightPush("hb11", "value2");
            Long size = stringRedisTemplate.opsForList().size("hb11");
            if (size > 30) {
                stringRedisTemplate.opsForList().rightPop("hb11");
            }
        }
        return true;
    }
}
