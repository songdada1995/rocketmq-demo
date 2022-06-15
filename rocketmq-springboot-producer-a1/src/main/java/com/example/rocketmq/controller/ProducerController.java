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

    @PostMapping(value = "/msg3")
    public Responses msg3(@RequestBody MqMessage message) {
        return providerService.msg3(message);
    }

    @PostMapping(value = "/msg4")
    public Responses msg4(@RequestBody MqMessage message) {
        return providerService.msg4(message);
    }

    @PostMapping(value = "/msg5")
    public Responses msg5(@RequestBody MqMessage message) {
        return providerService.msg5(message);
    }

    @PostMapping(value = "/msg6")
    public Responses msg6(@RequestBody MqMessage message) {
        return providerService.msg6(message);
    }

    @PostMapping(value = "/msg7")
    public Responses msg7(@RequestBody MqMessage message) {
        return providerService.msg7(message);
    }

    @PostMapping(value = "/msg8")
    public Responses msg8(@RequestBody MqMessage message) {
        return providerService.msg8(message);
    }

    @PostMapping(value = "/msg9")
    public Responses msg9(@RequestBody MqMessage message) {
        return providerService.msg9(message);
    }

    @PostMapping(value = "/msg10")
    public Responses msg10(@RequestBody MqMessage message) {
        return providerService.msg10(message);
    }

    @PostMapping(value = "/msg11")
    public Responses msg11(@RequestBody MqMessage message) {
        return providerService.msg11(message);
    }

    @PostMapping(value = "/msg12")
    public Responses msg12(@RequestBody MqMessage message) {
        return providerService.msg12(message);
    }

    @PostMapping(value = "/msg13")
    public Responses msg13(@RequestBody MqMessage message) {
        return providerService.msg13(message);
    }

    @PostMapping(value = "/msg14")
    public Responses msg14(@RequestBody MqMessage message) {
        return providerService.msg14(message);
    }

    @PostMapping(value = "/msg15")
    public Responses msg15(@RequestBody MqMessage message) {
        return providerService.msg15(message);
    }

    @PostMapping(value = "/msg16")
    public Responses msg16(@RequestBody MqMessage message) {
        return providerService.msg16(message);
    }

    @PostMapping(value = "/msg17")
    public Responses msg17(@RequestBody MqMessage message) {
        return providerService.msg17(message);
    }

}
