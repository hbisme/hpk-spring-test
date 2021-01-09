package com.hb.service;

import org.springframework.stereotype.Service;
import com.yt.asd.kit.domain.RpcResult;


@Service
public class MyService1 {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String echo() {
        return "echo myService1";
    }

    public RpcResult<String> testRpcResult() {
        RpcResult res = new RpcResult().data("测试统一返回结果").success();
        return res;
    }


}
