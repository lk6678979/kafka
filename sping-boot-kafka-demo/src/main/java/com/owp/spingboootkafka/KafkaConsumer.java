package com.owp.spingboootkafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics ="testTopic")
    public void listener(ConsumerRecord record){
        System.out.printf("topic = %s, offset = %s, key = %s, value = %s \n", record.topic(), record.offset(),record.key(), record.value());
    }
}
