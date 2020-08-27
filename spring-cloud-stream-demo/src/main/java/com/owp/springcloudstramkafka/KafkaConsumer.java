package com.owp.springcloudstramkafka;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @StreamListener(DemoChannel.TESTTOPIC_IN)
    public void receive1s(Message<String> message){
        String payload = message.getPayload();
        System.out.println("数据："+payload);
        System.out.println("Header信息："+message.getHeaders());

    }
}
