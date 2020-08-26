package com.owp.spingboootkafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafkaProducer")
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping("product/{topic}/{key}/{message}")
    public String send(@PathVariable String topic,@PathVariable String key,@PathVariable String message){
        ListenableFuture<SendResult<String, String>> sendResult =  kafkaTemplate.send(topic,key,message);
        final boolean[] flag = {false};
        sendResult.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                System.out.println("数据发送失败");
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                System.out.println("数据发送成功");
            }
        });
        return "kafka消息提交成功:" + message;
    }
}
