package com.example.rocketmq.config;

import org.apache.rocketmq.spring.annotation.ExtRocketMQConsumerConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQConsumerConfiguration(
        topic = "${demo.rocketmq.topic}",
        group = "${demo.rocketmq.consumer.group}",
        tlsEnable = "${demo.ext.consumer.tlsEnable}")
public class ExtRocketMQTemplate extends RocketMQTemplate {
}