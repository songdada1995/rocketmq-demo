package com.example.rocketmq.service;

import com.example.rocketmq.domain.User;
import com.example.rocketmq.domain.common.MqMessage;
import com.example.rocketmq.domain.common.Responses;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/6/7 22:40
 */
@Service
public class ProviderService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${demo.rocketmq.transTopic}")
    private String springTransTopic;
    @Value("${demo.rocketmq.topic}")
    private String springTopic;
    @Value("${demo.rocketmq.topic.user}")
    private String userTopic;
    @Value("${demo.rocketmq.orderTopic}")
    private String orderPaidTopic;
    @Value("${demo.rocketmq.msgExtTopic}")
    private String msgExtTopic;
    @Value("${demo.rocketmq.stringRequestTopic}")
    private String stringRequestTopic;
    @Value("${demo.rocketmq.bytesRequestTopic}")
    private String bytesRequestTopic;
    @Value("${demo.rocketmq.objectRequestTopic}")
    private String objectRequestTopic;
    @Value("${demo.rocketmq.genericRequestTopic}")
    private String genericRequestTopic;

    @Resource(name = "extRocketMQTemplate")
    private RocketMQTemplate extRocketMQTemplate;

    public Responses msg1(MqMessage message) {
        SendResult sendResult = rocketMQTemplate.syncSend(springTopic, "Hello, " + message.getMessage());
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", springTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg2(MqMessage message) {
        SendResult sendResult = rocketMQTemplate.syncSend(userTopic, new User().setUserAge((byte) 18).setUserName("Kitty").setMsg(message.getMessage()));
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

}
