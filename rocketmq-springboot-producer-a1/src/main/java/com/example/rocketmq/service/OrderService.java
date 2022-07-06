package com.example.rocketmq.service;

import com.example.rocketmq.domain.order.TOrder;
import com.example.rocketmq.domain.order.TransactionLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/7/5 22:56
 */
@Slf4j
@Service
public class OrderService {

    @Transactional(rollbackFor = Exception.class)
    public void createOrder(TOrder order, String transactionId) throws Exception {
        // 1.Order写入数据库
        Thread.sleep(500);
//        int count = tOrderMapper.insert(order);
        int count = 1;

        // 2.写入事务日志
        if (count > 0) {
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setId(transactionId);
            transactionLog.setBusiness("order");
            transactionLog.setBusinessForeignKey(String.valueOf(order.getId()));
//            transactionLogMapper.insert(transactionLog);
            Thread.sleep(500);
        }

        log.info("订单创建完成 {}", order);
    }

}
