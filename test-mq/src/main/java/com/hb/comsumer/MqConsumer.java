package com.hb.comsumer;






import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;

import org.apache.rocketmq.common.message.MessageExt;

public class MqConsumer implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        // 消费方根据自己的情况,消费之后返回CommitMessage(完成消费),还是ReconsumeLater(后续还会推送过来)
        System.err.println("GetMsg:#:: " + new String(message.getBody()) + " ,MSG-ID: " + message.getMsgID());

        return Action.CommitMessage;
    }

    public static void main(String[] args) {

    }
}
