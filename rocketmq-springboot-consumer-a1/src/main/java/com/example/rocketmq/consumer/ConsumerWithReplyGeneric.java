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

import com.example.rocketmq.domain.ProductWithPayload;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Service;

/**
 * The consumer that replying generic type
 * 带返回值（泛型）的消费者
 */
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.genericRequestTopic}", consumerGroup = "${demo.rocketmq.genericRequestConsumer}", selectorExpression = "${demo.rocketmq.tag}")
public class ConsumerWithReplyGeneric implements RocketMQReplyListener<String, ProductWithPayload<String>> {
    @Override
    public ProductWithPayload<String> onMessage(String message) {
        System.out.printf("------- ConsumerWithReplyGeneric received: %s \n", message);
        return new ProductWithPayload<String>("replyProductName", "product payload");
    }
}
