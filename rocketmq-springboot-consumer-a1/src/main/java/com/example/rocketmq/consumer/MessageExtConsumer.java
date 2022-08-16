/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.rocketmq.consumer;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * MessageExtConsumer, consume listener impl class.
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.msgExtTopic}", consumerGroup = "${demo.rocketmq.message-ext-consumer-group}")
public class MessageExtConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    /**
     * 最大重试次数
     */
    private static final int MAX_RECONSUME_TIMES = 3;

    @Override
    public void onMessage(MessageExt message) {
        System.out.printf("------- MessageExtConsumer received message, msgId: %s, body:%s \n", message.getMsgId(), new String(message.getBody()));
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // set consumer consume message from now
        // 设置从当前时间开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        consumer.setConsumeTimestamp(UtilAll.timeMillisToHumanString3(System.currentTimeMillis()));
        // 设置最大重试次数.默认16次
        consumer.setMaxReconsumeTimes(3);
        // 配置重试消息逻辑,默认是context.setDelayLevelWhenNextConsume(0);
        consumer.setMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                /*
                    1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                    0表示每次按照上面定义的时间依次递增,第一次为10s,第二次为30s...
                    -1表示直接发往死信队列,不经过重试队列.
                    >0表示每次重试的时间间隔,由我们用户自定义,1表示重试间隔为1s,2表示5s,3表示10秒,依次递增,重试次数由配置consumer.setMaxReconsumeTimes(10)决定
                    发送的默认重试队列topic名称为%RETRY%+消费者组名,发送的默认死信队列topic名称为%DLQ%+消费者组名
                */
                // 表示重试间隔为5秒
                context.setDelayLevelWhenNextConsume(2);

                MessageExt msg = msgs.get(0);
                log.info(">>>>> MessageExtConsumer received msg: {} <<<<<", msg);
                try {
                    String msgBody = new String(msg.getBody(), "utf-8");
                    if ("msg_fail".equals(msgBody)) {
                        log.info("====失败消息开始=====");
                        log.info("msg:" + msg);
                        log.info("msgBody:" + msgBody);
                        log.info("====失败消息结束=====");
                        System.out.println(1 / 0);
                    }
                    log.info(">>>>> MessageExtConsumer msgBody:{}消息消费完成！ <<<<<", msgBody);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                } catch (Exception e) {
                    log.warn("MessageExtConsumer consume message failed. messageExt:{}", msg, e);
                    log.warn("当前时间:" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    log.warn("-------已重试次数为:" + msgs.get(0).getReconsumeTimes() + "次!-------");
                    log.warn("-------延迟级别设置:" + context.getDelayLevelWhenNextConsume() + "-------");
                    if (msgs.get(0).getReconsumeTimes() > MAX_RECONSUME_TIMES) {
                        // 重试大于3次直接发往死信队列
                        context.setDelayLevelWhenNextConsume(-1);
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
    }
}
