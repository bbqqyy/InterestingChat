package com.bqy.mallchat.transaction.service;

import com.bqy.mallchat.transaction.annotation.SecureInvoke;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


public class MQProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic,Object body){
        Message<Object> message = MessageBuilder.withPayload(body).build();
        rocketMQTemplate.send(topic,message);
    }
    @SecureInvoke
    public void sendSecureMsg(String topic,Object body,Object key){
        Message<Object> message = MessageBuilder.withPayload(body)
                .setHeader("KEYS",key)
                .build();
        rocketMQTemplate.send(topic,message);
    }
}
