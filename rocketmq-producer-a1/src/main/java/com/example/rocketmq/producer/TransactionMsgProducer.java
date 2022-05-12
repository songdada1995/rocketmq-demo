package com.example.rocketmq.producer;

import cn.hutool.core.date.DateUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事务消息
 * <p>
 * 可修改/conf/broker.conf
 * <p>
 * #指定最多回查5次，操过后将丢弃消息并记录错误日志，默认15次
 * transactionCheckMax = 5
 * <p>
 * #指定TM在20秒内应该最终确认状态给TC，否者引发消息回查，默认为60秒，单位为毫秒
 * transactionTimeout = 20000
 * <p>
 * #消息回查时间间隔为10秒，默认60秒，单位为毫秒
 * transactionCheckInterval = 10000
 *
 * @author songbo
 * @version 1.0
 * @date 2022/5/11 21:48
 */
public class TransactionMsgProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 创建事务监听器
        TransactionListener transactionListener = new TransactionListenerImpl();
        // 创建事务生产者
        TransactionMQProducer producer = new TransactionMQProducer("simple_transaction_group");
        // 设置NameServer的地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 设置自定义线程池来处理检查事务状态请求
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.start();

        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        String topic = "TopicTestTransaction";
        for (int i = 0; i < 10; i++) {
            try {
                Message msg =
                        new Message(topic, tags[i % tags.length], "KEY" + i,
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s -- %s%n", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"), sendResult);
                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }

    /**
     * 事务监听器
     */
    public static class TransactionListenerImpl implements TransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);
        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

        /**
         * map 存放 key：TransactionId；value (0 / 1 / 2)
         * 返回事务中间状态，需要检查消息队列来确定事务状态
         *
         * @param msg
         * @param arg
         * @return
         */
        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(msg.getTransactionId(), status);
            return LocalTransactionState.UNKNOW;
        }

        /**
         * 返回不同事务状态
         * transactionCheckMax 默认为 15次
         *
         * @param msg
         * @return
         */
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            Integer status = localTrans.get(msg.getTransactionId());
            if (null != status) {
                switch (status) {
                    case 0:
                        System.out.printf("%s -- 消息：%s事务状态未确认！ %n", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"), msg.toString());
                        return LocalTransactionState.UNKNOW;
                    case 1:
                        System.out.printf("%s -- 消息：%s事务确认提交！ %n", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"), msg.toString());
                        return LocalTransactionState.COMMIT_MESSAGE;
                    case 2:
                        System.out.printf("%s -- 消息：%s事务回滚并删除！ %n", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"), msg.toString());
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }

}
