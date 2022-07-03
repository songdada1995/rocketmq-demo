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

package com.example.rocketmq.consumer.dlq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 监听死信队列
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "%DLQ%${demo.rocketmq.message-ext-consumer-group}",
        consumerGroup = "${demo.rocketmq.dlq-message-ext-consumer-group}")
public class DeadLetterMessageExtConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt message) {
        // 进入死信队列，可发送邮件预警通知
        log.info("------- 进入死信队列，可通知相关开发人员!!! -------");
        log.info("------- DeadLetterMessageExtConsumer received message, msgId: {}, body:{}", message.getMsgId(), new String(message.getBody()));
    }
}
