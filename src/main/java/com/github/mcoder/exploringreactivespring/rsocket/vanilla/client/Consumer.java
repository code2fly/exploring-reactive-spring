package com.github.mcoder.exploringreactivespring.rsocket.vanilla.client;

import com.github.mcoder.exploringreactivespring.rsocket.vanilla.JsonHelper;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Consumer {

    private final JsonHelper jsonHelper;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void ready() {
        log.info("starting consumer ....");

        ClientTransport clientTransport = TcpClientTransport.create("localhost", 7000);
        RSocketConnector
                .create()
                .connect(clientTransport)
                .flatMapMany(sender -> sender.requestStream(DefaultPayload.create("hi")))
                .map(payload -> payload.getDataUtf8())
                .subscribe(data -> log.info("Consumed data : {} ", data));

    }

}
