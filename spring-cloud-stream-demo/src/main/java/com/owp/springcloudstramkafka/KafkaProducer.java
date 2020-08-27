package com.owp.springcloudstramkafka;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("kafkaProducer")
public class KafkaProducer {

    @Resource(name = DemoChannel.TESTTOPIC_OUT)
    private MessageChannel messageChannel;

    @RequestMapping("product/{key}/{message}")
    public String send(@PathVariable String key,@PathVariable String message){
        boolean success = messageChannel.send(MessageBuilder.withPayload(message).setHeader("key",key).build());
        if(!success){
            System.out.println("数据发送失败");
        }
        return "kafka消息提交成功:" + message;
    }
}
