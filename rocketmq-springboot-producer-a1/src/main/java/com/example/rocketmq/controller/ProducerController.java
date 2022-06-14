package com.example.rocketmq.controller;

import com.example.rocketmq.domain.common.MqMessage;
import com.example.rocketmq.domain.common.Responses;
import com.example.rocketmq.service.ProviderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author songbo
 * @version 1.0
 * @date 2022/6/7 22:38
 */
@RestController
@RequestMapping("/provider")
public class ProducerController {

    @Resource
    private ProviderService providerService;

    @PostMapping(value = "/msg1")
    public Responses msg1(@RequestBody MqMessage message) {
        return providerService.msg1(message);
    }

    @PostMapping(value = "/msg2")
    public Responses msg2(@RequestBody MqMessage message) {
        return providerService.msg2(message);
    }

}
