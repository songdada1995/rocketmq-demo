package com.example.rocketmq.config;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 自定义消息监听器
 *
 * @author songbo
 * @version 1.0
 * @date 2022/6/21 23:05
 */
@Slf4j
@Component
public class CustomMessageListenerConcurrently implements MessageListenerConcurrently {

    /**
     * 最大重试次数
     */
    private static final int MAX_RECONSUME_TIMES = 3;

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
        log.info(">>>>> CustomMessageListenerConcurrently received msg: {} <<<<<", msg);
        try {
            String msgBody = new String(msg.getBody(), "utf-8");
            if ("msg_fail".equals(msgBody)) {
                log.info("====失败消息开始=====");
                log.info("msg:" + msg);
                log.info("msgBody:" + msgBody);
                log.info("====失败消息结束=====");
                System.out.println(1 / 0);
            }
            log.info(">>>>> CustomMessageListenerConcurrently 消息消费完成！ <<<<<");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

        } catch (Exception e) {
            log.warn("consume message failed. messageExt:{}", msg, e);
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

}
