package com.example.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 过滤消息生产者
 *
 * @author songbo
 * @version 1.0
 * @date 2022/5/10 22:49
 */
public class FilterMsgProducer {
    public static void main(String[] args) throws MQClientException {

        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("simple_filter_group");
        // 设置NameServer的地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 设置消息同步发送失败时的重试次数，默认为 2
        producer.setRetryTimesWhenSendFailed(2);
        // 设置消息发送超时时间，默认3000ms
        producer.setSendMsgTimeout(3000);
        // 启动Producer实例
        producer.start();

        try {
            for (int i = 0; i < 5; i++) {
                Message msg = new Message("TopicFilterTest",
                        "TagA",
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                // 设置一些属性
                msg.putUserProperty("a", String.valueOf(i));
                SendResult sendResult = producer.send(msg);
                // 通过sendResult返回消息是否成功送达
                System.out.printf("%s%n", sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        producer.shutdown();
    }
}
