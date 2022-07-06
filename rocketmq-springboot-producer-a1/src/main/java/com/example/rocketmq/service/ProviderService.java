package com.example.rocketmq.service;

import com.alibaba.fastjson.TypeReference;
import com.example.rocketmq.domain.OrderPaidEvent;
import com.example.rocketmq.domain.ProductWithPayload;
import com.example.rocketmq.domain.User;
import com.example.rocketmq.domain.common.MqMessage;
import com.example.rocketmq.domain.common.Responses;
import com.example.rocketmq.domain.order.TOrder;
import com.example.rocketmq.exception.BasicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalRequestCallback;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/6/7 22:40
 */
@Slf4j
@Service
public class ProviderService {

    @Value("${demo.rocketmq.topic}")
    private String stringTopic;
    @Value("${demo.rocketmq.user-topic}")
    private String userTopic;
    @Value("${demo.rocketmq.orderTopic}")
    private String orderPaidTopic;
    @Value("${demo.rocketmq.msgExtTopic}")
    private String msgExtTopic;
    @Value("${demo.rocketmq.transTopic}")
    private String springTransTopic;
    @Value("${demo.rocketmq.bytesRequestTopic}")
    private String bytesRequestTopic;
    @Value("${demo.rocketmq.stringRequestTopic}")
    private String stringRequestTopic;
    @Value("${demo.rocketmq.objectRequestTopic}")
    private String objectRequestTopic;
    @Value("${demo.rocketmq.genericRequestTopic}")
    private String genericRequestTopic;

    @Value("${demo.rocketmq.denyTopic}")
    private String demoDenyTopic;
    @Value("${demo.rocketmq.onlySubTopic}")
    private String demoOnlySubTopic;
    @Value("${demo.rocketmq.defaultTestTopic}")
    private String demoDefaultTestTopic;

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource(name = "extRocketMQTemplate")
    private RocketMQTemplate extRocketMQTemplate;

    @Resource(name = "extOrderRocketMQTemplate")
    private RocketMQTemplate extOrderRocketMQTemplate;

    @Value("${order.rocketmq.topic}")
    private String transOrderTopic;
    @Value("${order.rocketmq.tag-order}")
    private String transOrderTagOrder;
    @Resource(name = "orderService")
    private OrderService orderService;
    @Resource(name = "transactionLogService")
    private TransactionLogService transactionLogService;
    @Resource
    private RocketMQMessageConverter rocketMQMessageConverter;


    public Responses msg1(MqMessage message) {
        // 发送同步消息，传递字符串参数
        SendResult sendResult = rocketMQTemplate.syncSend(stringTopic, "Hello, " + message.getMessage());
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", stringTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg2(MqMessage message) {
        // 发送同步消息，传递实体类参数
        SendResult sendResult = rocketMQTemplate.syncSend(userTopic,
                new User().setUserAge((byte) 18).setUserName("Kitty").setMsg(message.getMessage()));
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg3(MqMessage message) {
        // 发送同步消息，构建消息体，携带自定义的header
        SendResult sendResult = rocketMQTemplate.syncSend(userTopic,
                MessageBuilder.withPayload(new User().setUserAge((byte) 21).setUserName("Lester").setMsg(message.getMessage()))
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE).build());
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg4(MqMessage message) {
        // 发送同步消息，构建消息体，使用extRocketMQTemplate，string-topic
        SendResult sendResult = extRocketMQTemplate.syncSend(stringTopic,
                MessageBuilder.withPayload(("Hello, " + message.getMessage()).getBytes()).build());
        System.out.printf("extRocketMQTemplate.syncSend1 to topic %s sendResult=%s %n", stringTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg5(MqMessage message) {
        // 发送同步消息，构建消息体，使用rocketMQTemplate，string-topic
        SendResult sendResult = rocketMQTemplate.syncSend(stringTopic,
                MessageBuilder.withPayload("Hello, " + message.getMessage() + "! I'm from spring message").build());
        System.out.printf("syncSend2 to topic %s sendResult=%s %n", stringTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    /**
     * 发送异步消息，有回调
     *
     * @param message
     * @return
     */
    public Responses msg6(MqMessage message) {

        rocketMQTemplate.asyncSend(orderPaidTopic, new OrderPaidEvent("T_001", new BigDecimal("88.00"), message.getMessage()), new SendCallback() {
            @Override
            public void onSuccess(SendResult var1) {
                System.out.printf("async onSucess SendResult=%s %n", var1);
            }

            @Override
            public void onException(Throwable var1) {
                System.out.printf("async onException Throwable=%s %n", var1);
            }
        });

        return Responses.newInstance().succeed("执行成功！");
    }

    /**
     * 发送消息，自动把参数转换为消息体
     *
     * @param message
     * @return
     */
    public Responses msg7(MqMessage message) {
        // tag0 will not be consumer-selected
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag0", "I'm from tag0，" + message.getMessage());
        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag0");
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag1", "I'm from tag1，" + message.getMessage());
        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag1");

        return Responses.newInstance().succeed("执行成功！");
    }

    /**
     * 发送批量消息
     *
     * @param message
     * @return
     */
    public Responses msg8(MqMessage message) {
        List<Message> msgs = new ArrayList<Message>();
        for (int i = 0; i < 10; i++) {
            msgs.add(MessageBuilder.withPayload("Hello " + message.getMessage() + ", RocketMQ Batch Msg#" + i).
                    setHeader(RocketMQHeaders.KEYS, "KEY_" + i).build());
        }

        SendResult sr = rocketMQTemplate.syncSend(stringTopic, msgs, 60000);
        System.out.printf("--- Batch messages send result :" + sr);

        return Responses.newInstance().succeed("执行成功！");
    }

    /**
     * 发送批量顺序消息
     *
     * @param message
     * @return
     */
    public Responses msg9(MqMessage message) {
        for (int q = 0; q < 4; q++) {
            // send to 4 queues
            List<Message> msgs = new ArrayList<Message>();
            for (int i = 0; i < 10; i++) {
                int msgIndex = q * 10 + i;
                String msg = String.format("Hello " + message.getMessage() + ", RocketMQ Batch Msg#%d to queue: %d", msgIndex, q);
                msgs.add(MessageBuilder.withPayload(msg).
                        setHeader(RocketMQHeaders.KEYS, "KEY_" + msgIndex).build());
            }
            // 相同hashKey，固定消息发送到同一个队列
            SendResult sr = rocketMQTemplate.syncSendOrderly(stringTopic, msgs, String.valueOf(q), 60000);
            //rocketMQTemplate.asyncSendOrderly();
            System.out.println("--- Batch messages orderly to queue :" + sr.getMessageQueue().getQueueId() + " send result :" + sr);
        }

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg10(MqMessage message) {
        // 发送事务消息 using rocketMQTemplate
        testRocketMQTemplateTransaction(message.getMessage());

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg11(MqMessage message) {
        // 发送事务消息 using extRocketMQTemplate
        testExtRocketMQTemplateTransaction(message.getMessage());

        return Responses.newInstance().succeed("执行成功！");
    }

    /* =======================================带返回值的消息 Start======================================== */

    public Responses msg12(MqMessage message) {
        // 同步发送request并且等待String类型的返回值
        String replyString = rocketMQTemplate.sendAndReceive(stringRequestTopic, "request string, " + message.getMessage(), String.class);
        System.out.printf("send %s and receive %s %n", "request string", replyString);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg13(MqMessage message) {
        // 同步发送请求，设置超时时间，并等待字节数组类型的返回值
        byte[] replyBytes = rocketMQTemplate.sendAndReceive(bytesRequestTopic, MessageBuilder.withPayload(message.getMessage() + ", request byte[]").build(), byte[].class, 3000);
        System.out.printf("send %s and receive %s %n", "request byte[]", new String(replyBytes));

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg14(MqMessage message) {
        // 同步发送请求，携带hashKey，指明返回值类型User，并等待User类型返回值
        User requestUser = new User().setUserAge((byte) 9).setUserName("requestUserName").setMsg(message.getMessage());
        User replyUser = rocketMQTemplate.sendAndReceive(objectRequestTopic, requestUser, User.class, "order-id");
        System.out.printf("send %s and receive %s %n", requestUser, replyUser);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg15(MqMessage message) {
        // 同步发送请求，设置超时时间，设置延迟级别，并接受泛型返回值
        ProductWithPayload<String> replyGenericObject = rocketMQTemplate.sendAndReceive(genericRequestTopic, message.getMessage() + ", request generic",
                new TypeReference<ProductWithPayload<String>>() {
                }.getType(), 30000, 2);
        System.out.printf("send %s and receive %s %n", "request generic", replyGenericObject);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg16(MqMessage message) {
        // 发送异步请求，并接受返回值。异步发送需要在回调的接口中指明返回值类型
        rocketMQTemplate.sendAndReceive(stringRequestTopic, message.getMessage() + ", request string",
                new RocketMQLocalRequestCallback<String>() {
                    @Override
                    public void onSuccess(String message) {
                        System.out.printf("send %s and receive %s %n", "request string", message);
                    }

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                    }
                });

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg17(MqMessage message) {
        // 发送异步请求，并接受返回User类型。异步发送需要在回调的接口中指明返回值类型
        rocketMQTemplate.sendAndReceive(objectRequestTopic, new User().setUserAge((byte) 9).setUserName("requestUserName").setMsg(message.getMessage()),
                new RocketMQLocalRequestCallback<User>() {
                    @Override
                    public void onSuccess(User message) {
                        System.out.printf("send user object and receive %s %n", message.toString());
                    }

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                    }
                }, 5000);

        return Responses.newInstance().succeed("执行成功！");
    }

    /* =======================================带返回值的消息 End======================================== */


    public Responses msg18(MqMessage message) {

        try {
            // 同步发送
            SendResult sendResult = rocketMQTemplate.syncSend(msgExtTopic, MessageBuilder.withPayload(message.getMessage()).build());
            log.info("method msg18 syncSend to topic {} sendResult={}", msgExtTopic, sendResult);

            // 异步发送
//            rocketMQTemplate.asyncSend(msgExtTopic, MessageBuilder.withPayload(message.getMessage()).build(), new SendCallback() {
//                @Override
//                public void onSuccess(SendResult var1) {
//                    log.info("method msg18 async onSucess SendResult={}", var1);
//                }
//
//                @Override
//                public void onException(Throwable var1) {
//                    // 记录异常，处理消息发送失败
//                    log.error("method msg18 async onException Throwable={}", var1);
//                }
//            });

        } catch (Throwable e) {
            // 记录异常，处理消息发送失败
            log.error(BasicException.exceptionTrace(e));
            throw e;
        }

        return Responses.newInstance().succeed("执行成功！");
    }

    @Transactional
    public Responses msg19(MqMessage message) {

        // 构建订单数据
        TOrder order = new TOrder().ready();
        order.setId(123456789L);
        order.setOrderNo("TR20220701");
        order.setSku("TSK222222");
        order.setRemark(message.getMessage());
        String transactionId = UUID.randomUUID().toString().replace("-", "");
        Message msg = MessageBuilder.withPayload(order).setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId).build();

        // 发送订单事务消息
        SendResult sendOrderResult = extOrderRocketMQTemplate.sendMessageInTransaction(
                transOrderTopic + ":" + transOrderTagOrder, msg, null);
        log.info("------ 发送订单事务消息返回数据 msg body = {} , sendOrderResult sendStatus = {} ------", msg.getPayload(), sendOrderResult.getSendStatus());

        return Responses.newInstance().succeed("执行成功！");
    }

    /**
     * 发送事务消息
     *
     * @throws MessagingException
     */
    private void testRocketMQTemplateTransaction(String message) throws MessagingException {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            try {

                Message msg = MessageBuilder.withPayload(message + ", rocketMQTemplate transactional message " + i).
                        setHeader(RocketMQHeaders.TRANSACTION_ID, "KEY_" + i).build();
                SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
                        springTransTopic + ":" + tags[i % tags.length], msg, null);
                System.out.printf("------rocketMQTemplate send Transactional msg body = %s , sendResult=%s %n",
                        msg.getPayload(), sendResult.getSendStatus());

                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送事务消息
     *
     * @throws MessagingException
     */
    private void testExtRocketMQTemplateTransaction(String message) throws MessagingException {
        for (int i = 0; i < 10; i++) {
            try {
                Message msg = MessageBuilder.withPayload(message + ", extRocketMQTemplate transactional message " + i).
                        setHeader(RocketMQHeaders.TRANSACTION_ID, "KEY_" + i).build();
                SendResult sendResult = extRocketMQTemplate.sendMessageInTransaction(
                        springTransTopic, msg, null);
                System.out.printf("------ExtRocketMQTemplate send Transactional msg body = %s , sendResult=%s %n",
                        msg.getPayload(), sendResult.getSendStatus());

                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 事务监听器
     * RocketMQTemplate
     */
    @RocketMQTransactionListener
    class TransactionListenerImpl implements RocketMQLocalTransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);

        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            System.out.printf("#### executeLocalTransaction is executed, msgTransactionId=%s %n",
                    transId);
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(transId, status);
            if (status == 0) {
                // Return local transaction with success(commit), in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # COMMIT # Simulating msg %s related local transaction exec succeeded! ### %n", msg.getPayload());
                return RocketMQLocalTransactionState.COMMIT;
            }

            if (status == 1) {
                // Return local transaction with failure(rollback) , in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # ROLLBACK # Simulating %s related local transaction exec failed! %n", msg.getPayload());
                return RocketMQLocalTransactionState.ROLLBACK;
            }

            System.out.printf("    # UNKNOW # Simulating %s related local transaction exec UNKNOWN! \n");
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        /**
         * 事务状态UNKNOWN，则执行回调，校验事务状态
         * transactionCheckMax 默认为 15次
         *
         * @param msg
         * @return
         */
        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
            Integer status = localTrans.get(transId);
            if (null != status) {
                switch (status) {
                    case 0:
                        retState = RocketMQLocalTransactionState.COMMIT;
                        break;
                    case 1:
                        retState = RocketMQLocalTransactionState.ROLLBACK;
                        break;
                    case 2:
                        retState = RocketMQLocalTransactionState.UNKNOWN;
                        break;
                }
            }
            System.out.printf("------ !!! checkLocalTransaction is executed once," +
                            " msgTransactionId=%s, TransactionState=%s status=%s %n",
                    transId, retState, status);
            return retState;
        }
    }

    /**
     * 事务监听器
     * extRocketMQTemplate
     */
    @RocketMQTransactionListener(rocketMQTemplateBeanName = "extRocketMQTemplate")
    class ExtTransactionListenerImpl implements RocketMQLocalTransactionListener {
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            System.out.printf("ExtTransactionListenerImpl executeLocalTransaction and return UNKNOWN. \n");
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            System.out.printf("ExtTransactionListenerImpl checkLocalTransaction and return COMMIT. \n");
            return RocketMQLocalTransactionState.COMMIT;
        }
    }

    /**
     * 订单事务监听器
     */
    @RocketMQTransactionListener(rocketMQTemplateBeanName = "extOrderRocketMQTemplate")
    class ExtOrderTransactionListenerImpl implements RocketMQLocalTransactionListener {
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            log.info(">>> 开始执行订单本地事务 <<<");
            log.info(">>> msg: {}, arg: {} <<<", msg, arg);
            RocketMQLocalTransactionState state;
            try {
                // 创建Order
                TOrder order = (TOrder) rocketMQMessageConverter.getMessageConverter().fromMessage(msg, TOrder.class);
                String transactionId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
                orderService.createOrder(order, transactionId);

                state = RocketMQLocalTransactionState.COMMIT;
                log.info(">>> 订单本地事务已提交，TransactionId：{} <<<", transactionId);
            } catch (Exception e) {
                log.error("订单本地事务执行失败，e：{}", e);
                state = RocketMQLocalTransactionState.ROLLBACK;
            }
            return state;
        }

        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            String transactionId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            log.info(">>> 开始回查订单本地事务状态，transactionId：{} <<<", transactionId);
            RocketMQLocalTransactionState state;
            // 校验订单是否写入数据库
            if (transactionLogService.checkCount(transactionId) > 0) {
                state = RocketMQLocalTransactionState.COMMIT;
            } else {
                state = RocketMQLocalTransactionState.UNKNOWN;
            }
            log.info(">>> 结束回查订单本地事务状态，state：{} <<<", state);
            return state;
        }
    }

}
