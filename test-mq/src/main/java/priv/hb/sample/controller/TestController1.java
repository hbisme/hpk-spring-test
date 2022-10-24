package priv.hb.sample.controller;

import com.alibaba.fastjson.JSON;
import com.yt.asd.common.mq.result.OnsSendResult;
import com.yt.asd.common.mq.send.MQSendClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController1 {
    // @Autowired
    // private AllSendClient mqClient;
    // private MQSendClient allSendClient = null;


    @Autowired
    private MQSendClient mqSendClient;


    // @Autowired
    // private MqAutoConfigure mqAutoConfigure22222;



    @GetMapping("/hello")
    public String get1() {
        System.out.println("int hello");
        return "hello";
    }

    @GetMapping("/hello2")
    public String get2() {
        // System.out.println(mqAutoConfigure22222);
        System.out.println(mqSendClient);
        return "ok";
    }

    @GetMapping("/mq")
    public String testSendMq() {
        try {
            OnsSendResult onsSendResult = mqSendClient.sendMessage("k001-hb", "hello,test", "TAG");
            return JSON.toJSONString(onsSendResult);
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(e);
        }
    }


    @GetMapping("/mqc")
    public String testConsumerMq() {
        try {
            OnsSendResult onsSendResult = mqSendClient.sendMessage("k001-hb", "hello,test", "TAG");
            return JSON.toJSONString(onsSendResult);
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(e);
        }
    }
}
