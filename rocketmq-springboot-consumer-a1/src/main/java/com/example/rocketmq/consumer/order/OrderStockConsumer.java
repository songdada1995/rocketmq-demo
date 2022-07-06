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
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单库存消费者
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${order.rocketmq.topic}", selectorExpression = "${order.rocketmq.tag-order}",
        consumerGroup = "${order.rocketmq.stock-consumer-group}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING,
        maxReconsumeTimes = 3,
        delayLevelWhenNextConsume = 2)
public class OrderStockConsumer implements RocketMQListener<TOrder> {

    @SneakyThrows
    @Override
    public void onMessage(TOrder tOrder) {
        log.info(">>>>> 订单库存消费者 onMessage received: {} [orderNo : {}] <<<<<", tOrder, tOrder.getOrderNo());
        try {
//            if (true) {
//                throw new RuntimeException("测试下游订单库存服务异常！");
//            }
            Thread.sleep(4000);
            log.info(">>>>> 订单库存消费者 onMessage 消息消费完成！ <<<<<");
        } catch (Exception e) {
            log.warn("订单库存消费者 onMessage consume message failed e:{}", e);
            log.warn("当前时间:" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            throw e;
        }
    }

}
