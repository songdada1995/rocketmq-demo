package com.example.rocketmq.domain.common;

import lombok.Data;

import java.util.Date;

@Data
public class MqMessage {

    private String message;

    private String senderName;

    private Date sendDate;

    private String sendIp;

}
