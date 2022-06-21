package com.example.rocketmq.service;

import com.alibaba.fastjson.TypeReference;
import com.example.rocketmq.domain.OrderPaidEvent;
import com.example.rocketmq.domain.ProductWithPayload;
import com.example.rocketmq.domain.User;
import com.example.rocketmq.domain.common.MqMessage;
import com.example.rocketmq.domain.common.Responses;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalRequestCallback;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/6/7 22:40
 */
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
    @Value("${demo.rocketmq.exception-topic}")
    private String exceptionTopic;

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource(name = "extRocketMQTemplate")
    private RocketMQTemplate extRocketMQTemplate;

    public Responses msg1(MqMessage message) {
        // 发送同步消息，传递字符串参数
        SendResult sendResult = rocketMQTemplate.syncSend(stringTopic, "Hello, " + message.getMessage());
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", stringTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg2(MqMessage message) {
        // 发送同步消息，传递实体类参数
        SendResult sendResult = rocketMQTemplate.syncSend(userTopic, new User().setUserAge((byte) 18).setUserName("Kitty").setMsg(message.getMessage()));
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);
        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg3(MqMessage message) {
        // 发送同步消息，构建消息体
        SendResult sendResult = rocketMQTemplate.syncSend(userTopic, MessageBuilder.withPayload(
                new User().setUserAge((byte) 21).setUserName("Lester").setMsg(message.getMessage()))
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE).build());
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", userTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg4(MqMessage message) {
        // 发送同步消息，使用extRocketMQTemplate，string-topic
        SendResult sendResult = extRocketMQTemplate.syncSend(stringTopic, MessageBuilder.withPayload(("Hello, " + message.getMessage()).getBytes()).build());
        System.out.printf("extRocketMQTemplate.syncSend1 to topic %s sendResult=%s %n", stringTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg5(MqMessage message) {
        // 发送同步消息，使用rocketMQTemplate，string-topic
        SendResult sendResult = rocketMQTemplate.syncSend(stringTopic, MessageBuilder.withPayload("Hello, " + message.getMessage() + "! I'm from spring message").build());
        System.out.printf("syncSend2 to topic %s sendResult=%s %n", stringTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg6(MqMessage message) {
        // 发送异步消息
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

    public Responses msg7(MqMessage message) {
        // 发送消息，自动把参数转换为消息体
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag0", "I'm from tag0，" + message.getMessage());  // tag0 will not be consumer-selected
        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag0");
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag1", "I'm from tag1，" + message.getMessage());
        System.out.printf("syncSend topic %s tag %s %n", msgExtTopic, "tag1");

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg8(MqMessage message) {
        // 发送批量消息
        List<Message> msgs = new ArrayList<Message>();
        for (int i = 0; i < 10; i++) {
            msgs.add(MessageBuilder.withPayload("Hello " + message.getMessage() + ", RocketMQ Batch Msg#" + i).
                    setHeader(RocketMQHeaders.KEYS, "KEY_" + i).build());
        }

        SendResult sr = rocketMQTemplate.syncSend(stringTopic, msgs, 60000);
        System.out.printf("--- Batch messages send result :" + sr);

        return Responses.newInstance().succeed("执行成功！");
    }

    public Responses msg9(MqMessage message) {
        // 发送批量顺序消息
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
        rocketMQTemplate.sendAndReceive(stringRequestTopic, message.getMessage() + ", request string", new RocketMQLocalRequestCallback<String>() {
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
        rocketMQTemplate.sendAndReceive(objectRequestTopic, new User().setUserAge((byte) 9).setUserName("requestUserName").setMsg(message.getMessage()), new RocketMQLocalRequestCallback<User>() {
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

    public Responses msg18(MqMessage message) {
        // 发送同步消息，使用rocketMQTemplate，string-topic
        SendResult sendResult = rocketMQTemplate.syncSend(exceptionTopic, MessageBuilder.withPayload("Hello, " + message.getMessage() + "! I'm from spring message").build());
        System.out.printf("syncSend2 to topic %s sendResult=%s %n", exceptionTopic, sendResult);

        return Responses.newInstance().succeed("执行成功！");
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


}
