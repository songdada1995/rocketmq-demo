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

package com.example.rocketmq.consumer.order;

import cn.hutool.core.date.DateUtil;
import com.example.rocketmq.domain.order.TOrder;
import lombok.SneakyThrows;
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
 * 订单积分消费者
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${order.rocketmq.topic}", selectorExpression = "${order.rocketmq.tag-order}",
        consumerGroup = "${order.rocketmq.points-consumer-group}")
public class OrderPointsConsumer implements RocketMQListener<TOrder>, RocketMQPushConsumerLifecycleListener {

    @SneakyThrows
    @Override
    public void onMessage(TOrder tOrder) {
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
                // 表示重试间隔为5秒
                context.setDelayLevelWhenNextConsume(2);

                MessageExt msg = msgs.get(0);
                log.info(">>>>> 订单积分消费者 MessageListenerConcurrently received msg: {} <<<<<", msg);
                try {
                    String msgBody = new String(msg.getBody(), "utf-8");
                    log.info(">>>>> 订单积分消费者 received msgBody: {} <<<<<", msgBody);
                    if (true) {
                        throw new RuntimeException("测试下游订单积分服务异常！");
                    }
                    log.info(">>>>> 订单积分消费者 MessageListenerConcurrently 消息消费完成！ <<<<<");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                } catch (Exception e) {
                    log.warn("订单积分消费者 consume message failed. messageExt:{}", msg, e);
                    log.warn("当前时间:" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    log.warn("-------已重试次数为:" + msgs.get(0).getReconsumeTimes() + "次!-------");
                    log.warn("-------延迟级别设置:" + context.getDelayLevelWhenNextConsume() + "-------");
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
    }
}
