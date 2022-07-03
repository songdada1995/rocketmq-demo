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


    /* =======================================同步消息 Start======================================== */
    /**
     * 发送同步消息，传递字符串参数
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg1")
    public Responses msg1(@RequestBody MqMessage message) {
        return providerService.msg1(message);
    }

    /**
     * 发送同步消息，传递实体类参数
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg2")
    public Responses msg2(@RequestBody MqMessage message) {
        return providerService.msg2(message);
    }

    /**
     * 发送同步消息，构建消息体
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg3")
    public Responses msg3(@RequestBody MqMessage message) {
        return providerService.msg3(message);
    }

    /**
     * 发送同步消息，使用extRocketMQTemplate，string-topic
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg4")
    public Responses msg4(@RequestBody MqMessage message) {
        return providerService.msg4(message);
    }

    /**
     * 发送同步消息，使用rocketMQTemplate，string-topic
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg5")
    public Responses msg5(@RequestBody MqMessage message) {
        return providerService.msg5(message);
    }
    /* =======================================同步消息 End======================================== */


    /* =======================================异步消息 Start======================================== */
    /**
     * 发送异步消息，有回调
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg6")
    public Responses msg6(@RequestBody MqMessage message) {
        return providerService.msg6(message);
    }
    /* =======================================异步消息 End======================================== */

    /**
     * 发送消息，自动把参数转换为消息体
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg7")
    public Responses msg7(@RequestBody MqMessage message) {
        return providerService.msg7(message);
    }

    /**
     * 发送批量消息
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg8")
    public Responses msg8(@RequestBody MqMessage message) {
        return providerService.msg8(message);
    }

    /**
     * 发送批量顺序消息
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg9")
    public Responses msg9(@RequestBody MqMessage message) {
        return providerService.msg9(message);
    }



    /* =======================================事务消息 Start======================================== */

    /**
     * 发送事务消息 using rocketMQTemplate
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg10")
    public Responses msg10(@RequestBody MqMessage message) {
        return providerService.msg10(message);
    }

    /**
     * 发送事务消息 using extRocketMQTemplate
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg11")
    public Responses msg11(@RequestBody MqMessage message) {
        return providerService.msg11(message);
    }

    /* =========================================事务消息 End========================================== */



    /* =======================================带返回值的消息 Start======================================== */

    /**
     * 同步发送request并且等待String类型的返回值
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg12")
    public Responses msg12(@RequestBody MqMessage message) {
        return providerService.msg12(message);
    }

    /**
     * 同步发送请求，设置超时时间，并等待字节数组类型的返回值
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg13")
    public Responses msg13(@RequestBody MqMessage message) {
        return providerService.msg13(message);
    }

    /**
     * 同步发送请求，携带hashKey，指明返回值类型User，并等待User类型返回值
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg14")
    public Responses msg14(@RequestBody MqMessage message) {
        return providerService.msg14(message);
    }

    /**
     * 同步发送请求，设置超时时间，设置延迟级别，并接受泛型返回值
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg15")
    public Responses msg15(@RequestBody MqMessage message) {
        return providerService.msg15(message);
    }

    /**
     * 发送异步请求，并接受返回值。异步发送需要在回调的接口中指明返回值类型
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg16")
    public Responses msg16(@RequestBody MqMessage message) {
        return providerService.msg16(message);
    }

    /**
     * 发送异步请求，并接受返回User类型。异步发送需要在回调的接口中指明返回值类型
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg17")
    public Responses msg17(@RequestBody MqMessage message) {
        return providerService.msg17(message);
    }

    /* =======================================带返回值的消息 End======================================== */


    /**
     * 发送同步消息，使用rocketMQTemplate，string-topic
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/msg18")
    public Responses msg18(@RequestBody MqMessage message) {
        return providerService.msg18(message);
    }

}
