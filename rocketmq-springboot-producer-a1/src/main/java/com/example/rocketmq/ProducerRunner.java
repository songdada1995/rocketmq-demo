//package com.example.rocketmq;
//
//import com.alibaba.fastjson.TypeReference;
//import com.example.rocketmq.domain.OrderPaidEvent;
//import com.example.rocketmq.domain.ProductWithPayload;
//import com.example.rocketmq.domain.User;
//import org.apache.rocketmq.client.producer.SendCallback;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalRequestCallback;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.apache.rocketmq.spring.support.RocketMQHeaders;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.MessagingException;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MimeTypeUtils;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author songbo
// * @version 1.0
// * @date 2022/6/6 21:25
// */
//@Component
//public class ProducerRunner implements CommandLineRunner {
//
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
//    @Value("${demo.rocketmq.transTopic}")
//    private String springTransTopic;
//    @Value("${demo.rocketmq.topic}")
//    private String springTopic;
//    @Value("${demo.rocketmq.topic.user}")
//    private String userTopic;
//
//    @Value("${demo.rocketmq.orderTopic}")
//    private String orderPaidTopic;
//    @Value("${demo.rocketmq.msgExtTopic}")
//    private String msgExtTopic;
//    @Value("${demo.rocketmq.stringRequestTopic}")
//    private String stringRequestTopic;
//    @Value("${demo.rocketmq.bytesRequestTopic}")
//    private String bytesRequestTopic;
//    @Value("${demo.rocketmq.objectRequestTopic}")
//    private String objectRequestTopic;
//    @Value("${demo.rocketmq.genericRequestTopic}")
//    private String genericRequestTopic;
//
//    @Resource(name = "extRocketMQTemplate")
//    private RocketMQTemplate extRocketMQTemplate;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 发送同步消息，传递字符串参数
//        SendResult sendResult = rocketMQTemplate.syncSend(springTopic, "Hello, World!");
//        System.out.printf("syncSend1 to topic %s sendResult=%s %n", springTopic, sendResult);
//
//        // 发送同步消息，传递实体类参数
//        sendResult = rocketMQTemplate.syncSend(userTopic, new User().setUserAge((byte) 18).setUserName("Kitty"));
//        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);
//
//        // 发送同步消息，构建消息体
//        sendResult = rocketMQTemplate.syncSend(userTopic, MessageBuilder.withPayload(
//                new User().setUserAge((byte) 21).setUserName("Lester")).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE).build());
//        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);
//
//        // Use the extRocketMQTemplate
//        sendResult = extRocketMQTemplate.syncSend(springTopic, MessageBuilder.withPayload("Hello, World!2222".getBytes()).build());
//        System.out.printf("extRocketMQTemplate.syncSend1 to topic %s sendResult=%s %n", springTopic, sendResult);
//
//        // Send string with spring Message
//        sendResult = rocketMQTemplate.syncSend(springTopic, MessageBuilder.withPayload("Hello, World! I'm from spring message").build());
//        System.out.printf("syncSend2 to topic %s sendResult=%s %n", springTopic, sendResult);
//
//        // 发送异步消息
//        rocketMQTemplate.asyncSend(orderPaidTopic, new OrderPaidEvent("T_001", new BigDecimal("88.00")), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult var1) {
//                System.out.printf("async onSucess SendResult=%s %n", var1);
//            }
//
//            @Override
//            public void onException(Throwable var1) {
//                System.out.printf("async onException Throwable=%s %n", var1);
//            }
//
//        });
//
//        // 发送消息，自动把参数转换为消息体
//        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag0", "I'm from tag0");  // tag0 will not be consumer-selected
//        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag0");
//        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag1", "I'm from tag1");
//        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag1");
//
//        // 发送批量消息
//        testBatchMessages();
//
//        // 发送批量顺序消息
//        testSendBatchMessageOrderly();
//
//        // 发送事务消息 using rocketMQTemplate
//        testRocketMQTemplateTransaction();
//
//        // 发送事务消息 using extRocketMQTemplate
//        testExtRocketMQTemplateTransaction();
//
//        // 同步发送request并且等待String类型的返回值
//        String replyString = rocketMQTemplate.sendAndReceive(stringRequestTopic, "request string", String.class);
//        System.out.printf("send %s and receive %s %n", "request string", replyString);
//
//        // 同步发送请求，设置超时时间，并等待字节数组类型的返回值
//        byte[] replyBytes = rocketMQTemplate.sendAndReceive(bytesRequestTopic, MessageBuilder.withPayload("request byte[]").build(), byte[].class, 3000);
//        System.out.printf("send %s and receive %s %n", "request byte[]", new String(replyBytes));
//
//        // 同步发送请求，携带hashKey，指明返回值类型User，并等待User类型返回值
//        User requestUser = new User().setUserAge((byte) 9).setUserName("requestUserName");
//        User replyUser = rocketMQTemplate.sendAndReceive(objectRequestTopic, requestUser, User.class, "order-id");
//        System.out.printf("send %s and receive %s %n", requestUser, replyUser);
//
//        // 同步发送请求，设置超时时间，设置延迟级别，并接受泛型返回值
//        ProductWithPayload<String> replyGenericObject = rocketMQTemplate.sendAndReceive(genericRequestTopic, "request generic",
//                new TypeReference<ProductWithPayload<String>>() {
//                }.getType(), 30000, 2);
//        System.out.printf("send %s and receive %s %n", "request generic", replyGenericObject);
//
//        // 发送异步请求，并接受返回值。异步发送需要在回调的接口中指明返回值类型
//        rocketMQTemplate.sendAndReceive(stringRequestTopic, "request string", new RocketMQLocalRequestCallback<String>() {
//            @Override
//            public void onSuccess(String message) {
//                System.out.printf("send %s and receive %s %n", "request string", message);
//            }
//
//            @Override
//            public void onException(Throwable e) {
//                e.printStackTrace();
//            }
//        });
//
//        // 发送异步请求，并接受返回User类型。异步发送需要在回调的接口中指明返回值类型
//        rocketMQTemplate.sendAndReceive(objectRequestTopic, new User().setUserAge((byte) 9).setUserName("requestUserName"), new RocketMQLocalRequestCallback<User>() {
//            @Override
//            public void onSuccess(User message) {
//                System.out.printf("send user object and receive %s %n", message.toString());
//            }
//
//            @Override
//            public void onException(Throwable e) {
//                e.printStackTrace();
//            }
//        }, 5000);
//    }
//
//    /**
//     * 发送批量消息
//     */
//    private void testBatchMessages() {
//        List<Message> msgs = new ArrayList<Message>();
//        for (int i = 0; i < 10; i++) {
//            msgs.add(MessageBuilder.withPayload("Hello RocketMQ Batch Msg#" + i).
//                    setHeader(RocketMQHeaders.KEYS, "KEY_" + i).build());
//        }
//
//        SendResult sr = rocketMQTemplate.syncSend(springTopic, msgs, 60000);
//
//        System.out.printf("--- Batch messages send result :" + sr);
//    }
//
//    /**
//     * 发送批量顺序消息
//     */
//    private void testSendBatchMessageOrderly() {
//        for (int q = 0; q < 4; q++) {
//            // send to 4 queues
//            List<Message> msgs = new ArrayList<Message>();
//            for (int i = 0; i < 10; i++) {
//                int msgIndex = q * 10 + i;
//                String msg = String.format("Hello RocketMQ Batch Msg#%d to queue: %d", msgIndex, q);
//                msgs.add(MessageBuilder.withPayload(msg).
//                        setHeader(RocketMQHeaders.KEYS, "KEY_" + msgIndex).build());
//            }
//            // 相同hashKey，固定消息发送到同一个队列
//            SendResult sr = rocketMQTemplate.syncSendOrderly(springTopic, msgs, String.valueOf(q), 60000);
//            System.out.println("--- Batch messages orderly to queue :" + sr.getMessageQueue().getQueueId() + " send result :" + sr);
//        }
//    }
//
//    /**
//     * 发送事务消息
//     *
//     * @throws MessagingException
//     */
//    private void testRocketMQTemplateTransaction() throws MessagingException {
//        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
//        for (int i = 0; i < 10; i++) {
//            try {
//
//                Message msg = MessageBuilder.withPayload("rocketMQTemplate transactional message " + i).
//                        setHeader(RocketMQHeaders.TRANSACTION_ID, "KEY_" + i).build();
//                SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
//                        springTransTopic + ":" + tags[i % tags.length], msg, null);
//                System.out.printf("------rocketMQTemplate send Transactional msg body = %s , sendResult=%s %n",
//                        msg.getPayload(), sendResult.getSendStatus());
//
//                Thread.sleep(10);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 发送事务消息
//     *
//     * @throws MessagingException
//     */
//    private void testExtRocketMQTemplateTransaction() throws MessagingException {
//        for (int i = 0; i < 10; i++) {
//            try {
//                Message msg = MessageBuilder.withPayload("extRocketMQTemplate transactional message " + i).
//                        setHeader(RocketMQHeaders.TRANSACTION_ID, "KEY_" + i).build();
//                SendResult sendResult = extRocketMQTemplate.sendMessageInTransaction(
//                        springTransTopic, msg, null);
//                System.out.printf("------ExtRocketMQTemplate send Transactional msg body = %s , sendResult=%s %n",
//                        msg.getPayload(), sendResult.getSendStatus());
//
//                Thread.sleep(10);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 事务监听器
//     * RocketMQTemplate
//     */
//    @RocketMQTransactionListener
//    class TransactionListenerImpl implements RocketMQLocalTransactionListener {
//        private AtomicInteger transactionIndex = new AtomicInteger(0);
//
//        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();
//
//        @Override
//        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
//            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
//            System.out.printf("#### executeLocalTransaction is executed, msgTransactionId=%s %n",
//                    transId);
//            int value = transactionIndex.getAndIncrement();
//            int status = value % 3;
//            localTrans.put(transId, status);
//            if (status == 0) {
//                // Return local transaction with success(commit), in this case,
//                // this message will not be checked in checkLocalTransaction()
//                System.out.printf("    # COMMIT # Simulating msg %s related local transaction exec succeeded! ### %n", msg.getPayload());
//                return RocketMQLocalTransactionState.COMMIT;
//            }
//
//            if (status == 1) {
//                // Return local transaction with failure(rollback) , in this case,
//                // this message will not be checked in checkLocalTransaction()
//                System.out.printf("    # ROLLBACK # Simulating %s related local transaction exec failed! %n", msg.getPayload());
//                return RocketMQLocalTransactionState.ROLLBACK;
//            }
//
//            System.out.printf("    # UNKNOW # Simulating %s related local transaction exec UNKNOWN! \n");
//            return RocketMQLocalTransactionState.UNKNOWN;
//        }
//
//        /**
//         * 事务状态UNKNOWN，则执行回调，校验事务状态
//         * transactionCheckMax 默认为 15次
//         *
//         * @param msg
//         * @return
//         */
//        @Override
//        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
//            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
//            RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
//            Integer status = localTrans.get(transId);
//            if (null != status) {
//                switch (status) {
//                    case 0:
//                        retState = RocketMQLocalTransactionState.COMMIT;
//                        break;
//                    case 1:
//                        retState = RocketMQLocalTransactionState.ROLLBACK;
//                        break;
//                    case 2:
//                        retState = RocketMQLocalTransactionState.UNKNOWN;
//                        break;
//                }
//            }
//            System.out.printf("------ !!! checkLocalTransaction is executed once," +
//                            " msgTransactionId=%s, TransactionState=%s status=%s %n",
//                    transId, retState, status);
//            return retState;
//        }
//    }
//
//    /**
//     * 事务监听器
//     * extRocketMQTemplate
//     */
//    @RocketMQTransactionListener(rocketMQTemplateBeanName = "extRocketMQTemplate")
//    class ExtTransactionListenerImpl implements RocketMQLocalTransactionListener {
//        @Override
//        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
//            System.out.printf("ExtTransactionListenerImpl executeLocalTransaction and return UNKNOWN. \n");
//            return RocketMQLocalTransactionState.UNKNOWN;
//        }
//
//        @Override
//        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
//            System.out.printf("ExtTransactionListenerImpl checkLocalTransaction and return COMMIT. \n");
//            return RocketMQLocalTransactionState.COMMIT;
//        }
//    }
//}
