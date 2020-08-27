package com.owp.springcloudstramkafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DemoChannel {
    /**
     * 接收信号
     */
    String TESTTOPIC_IN = "testTopic_in";

    @Input(TESTTOPIC_IN)
    SubscribableChannel recieve();

    String TESTTOPIC_OUT = "testTopic_out";

    @Output(TESTTOPIC_OUT)
    MessageChannel sendMessage();
}
