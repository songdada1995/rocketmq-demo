package com.example.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 批量消息生产者
 *
 * @author songbo
 * @version 1.0
 * @date 2022/5/10 21:58
 */
public class BatchMsgProducer {
    public static void main(String[] args) throws MQClientException {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("simple_batch_group");
        // 设置NameServer的地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 设置消息同步发送失败时的重试次数，默认为 2
        producer.setRetryTimesWhenSendFailed(2);
        // 设置消息发送超时时间，默认3000ms
        producer.setSendMsgTimeout(3000);
        // 启动Producer实例
        producer.start();

        String topic = "BatchTest";
        List<Message> messages = buildMessages(topic);

//        try {
//            producer.send(messages);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        //把大的消息分裂成若干个小的消息
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                producer.send(listItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     * 构建消息
     *
     * @param topic
     * @return
     */
    private static List<Message> buildMessages(String topic) {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add(new Message(topic, "TagA", "OrderID" + String.format("%03d", i), String.format("%s%s", "Hello world ", i).getBytes()));
        }
        return messages;
    }

    /**
     * 模拟分割消息
     */
    public static class ListSplitter implements Iterator<List<Message>> {
        private final int SIZE_LIMIT = 1024 * 1024 * 4;
        private final List<Message> messages;
        private int currIndex;

        public ListSplitter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public boolean hasNext() {
            return currIndex < messages.size();
        }

        @Override
        public List<Message> next() {
            int startIndex = getStartIndex();
            int nextIndex = startIndex;
            int totalSize = 0;
            for (; nextIndex < messages.size(); nextIndex++) {
                Message message = messages.get(nextIndex);
                int tmpSize = calcMessageSize(message);
                if (tmpSize + totalSize > SIZE_LIMIT) {
                    break;
                } else {
                    totalSize += tmpSize;
                }
            }
            List<Message> subList = messages.subList(startIndex, nextIndex);
            currIndex = nextIndex;
            return subList;
        }

        private int getStartIndex() {
            Message currMessage = messages.get(currIndex);
            int tmpSize = calcMessageSize(currMessage);
            while (tmpSize > SIZE_LIMIT) {
                currIndex += 1;
                Message message = messages.get(currIndex);
                tmpSize = calcMessageSize(message);
            }
            return currIndex;
        }

        /**
         * 计算消息大小
         *
         * @param message
         * @return
         */
        private int calcMessageSize(Message message) {
            int tmpSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tmpSize += entry.getKey().length() + entry.getValue().length();
            }
            // 增加日志的开销1000000字节
            tmpSize = tmpSize + 1000000;
            return tmpSize;
        }
    }

}


