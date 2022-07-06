package com.example.rocketmq.domain.order;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 订单
 *
 * @author songbo
 * @version 1.0
 * @date 2022/7/5 21:56
 */
@ToString
@Data
public class TOrder {

    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * sku
     */
    private String sku;

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

    public TOrder ready() {
        this.createByUserId = 0L;
        this.createBy = "system";
        this.createTime = new Date();
        this.updateByUserId = 0L;
        this.updateBy = "system";
        this.updateTime = new Date();
        return this;
    }
}
