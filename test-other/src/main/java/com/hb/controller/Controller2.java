package com.hb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hb.service.MyService1;
import com.yt.asd.kit.domain.RpcResult;

import javax.servlet.http.HttpServletResponse;


@RestController
public class Controller2 {

    @Autowired
    MyService1 myService1;

    /**
     * 测试统一返回结果
     * @return
     */
    @GetMapping("RpcResultTest")
    public RpcResult<String> testRpcResult() {
        return myService1.testRpcResult();
    }

    @GetMapping(value="/metrics", produces="text/plain; version=0.0.4; charset=utf-8")
    public String getMetric(HttpServletResponse response) {
        // String res =
        //         "# TYPE go_info gauge\n" +
        //         "go_info{version=\"go1.12.5\"} 1\n" +
        //         "# TYPE waiting_job_inQueue2 gauge\n" +
        //         "waiting_job_inQueue2 {queue=\"a\"} 1\n" +
        //         "waiting_job_inQueue2 {queue=\"b\"} 0\n" +
        //         "waiting_job_inQueue2{queue=\"c\"} 0\n" +
        //         "# TYPE running_job2 gauge\n" +
        //         "running_job2 {queue=\"准实时\"} 2\n" +
        //         "running_job2 {queue=\"离线\"} 0\n" +
        //         "running_job2 {queue=\"手动\"} 0\n";

        // response.addHeader("version", "0.0.4");

        String res =
                             "# HELP waiting_job_inQueue help\n" +
                             "# TYPE waiting_job_inQueue gauge\n" +
                             "waiting_job_inQueue{queue=\"准实时\",} 0\n" +
                             "waiting_job_inQueue{queue=\"离线\",} 0\n" +
                             "waiting_job_inQueue{queue=\"手动\",} 0\n" +
                             "# HELP running_job help\n" +
                             "# TYPE running_job gauge\n" +
                             "running_job{queue=\"准实时\",} 0\n" +
                             "running_job{queue=\"离线\",} 0\n" +
                             "running_job{queue=\"手动\",} 0\n";
        return res;

    }


    // @GetMapping("/http-servlet-response")
    // public String usingHttpServletResponse(HttpServletResponse response) {
    //     response.addHeader("Baeldung-Example-Header", "Value-HttpServletResponse");
    //     return "Response with header using HttpServletResponse";
    // }

}
