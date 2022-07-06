package com.example.rocketmq.domain.order;

import lombok.Data;

import java.util.Date;

/**
 * 库存
 *
 * @author songbo
 * @version 1.0
 * @date 2022/7/5 21:56
 */
@Data
public class TStock {

    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 库存
     */
    private int stockNum;

    /**
     * 备注
     */
    private String remark;

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
