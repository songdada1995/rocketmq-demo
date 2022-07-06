package com.example.rocketmq.domain.order;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/7/5 22:09
 */
@ToString
@Data
public class TransactionLog {

    /**
     * 事务ID
     */
    private String id;

    /**
     * 业务标识
     */
    private String business;

    /**
     * 业务表主键
     */
    private String businessForeignKey;

    /**
     * 创建人ID
     */
    private Long createByUserId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人ID
     */
    private Long updateByUserId;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;
}
