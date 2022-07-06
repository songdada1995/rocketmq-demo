package com.example.rocketmq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/7/5 23:33
 */
@Slf4j
@Service
public class TransactionLogService {

    /**
     * 根据事务ID查询数据条数
     *
     * @param transactionId
     * @return
     */
    public int checkCount(String transactionId) {
        int count = 0;
        try {
//            count = transactionLogMapper.selectCountByTransactionId(transactionId);
            Thread.sleep(500);
        } catch (Exception e) {
            log.error(e.getMessage());
            count = 0;
        }
        return count;
    }

}
