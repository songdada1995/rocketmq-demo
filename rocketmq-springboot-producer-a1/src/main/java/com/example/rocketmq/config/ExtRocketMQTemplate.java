package com.example.rocketmq.config;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 扩展RocketMQTemplate
 * <p>
 * 一个事务流程和一个RocketMQTemplate需要一一对应
 * 可以通过 @ExtRocketMQTemplateConfiguration(注意该注解有@Component注解) 来扩展多个 RocketMQTemplate
 * 注意: 不同事务流程的RocketMQTemplate的producerGroup不能相同
 * 因为MQBroker会反向调用同一个producerGroup下的某个checkLocalTransactionState方法, 不同流程使用相同的producerGroup的话, 方法可能会调用错
 * <p>
 * accessKey为空，则默认采用rocketmq.producer.accessKey:的配置
 * secretKey为空，则默认采用rocketmq.producer.secretKey:的配置
 */
@ExtRocketMQTemplateConfiguration(
        nameServer = "${demo.rocketmq.ext.nameServer}",
        tlsEnable = "${demo.rocketmq.ext.useTLS}",
        group = "${demo.rocketmq.producer-group}")
public class ExtRocketMQTemplate extends RocketMQTemplate {
}