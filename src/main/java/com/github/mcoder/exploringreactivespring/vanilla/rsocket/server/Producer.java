package com.github.mcoder.exploringreactivespring.vanilla.rsocket.server;

import com.github.mcoder.exploringreactivespring.service.ReservationService;
import com.github.mcoder.exploringreactivespring.vanilla.rsocket.JsonHelper;
import io.rsocket.*;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 -  this is to make sure when app is ready we can start serving rsocket service
 - for this we also need to serialize and deserialize data sent over the wire
*/
@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final JsonHelper jsonHelper;
    private final ReservationService reservationService;


    /*
    with application startup we need to create RSocket SocketAcceptor - it is the callback we get when somebody connects to us
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(0)
    public void ready() {
        log.info("starting producer");

        SocketAcceptor socketAcceptor = (connectionSetupPayload, senderRSocket) -> {
            RSocket responseRSocket = new RSocket() {
                @Override
                public Flux<Payload> requestStream(Payload payload) {
//                        String jsonRequest = payload.getDataUtf8();
                    return reservationService
                            .getManyReservations()
                            .map(reservation -> jsonHelper.writeAsString(reservation))
                            .map(jsonString -> DefaultPayload.create(jsonString));
                }
            };

            return Mono.just(responseRSocket);
        };

        TcpServerTransport tcpServerTransport = TcpServerTransport.create(7000);

        RSocketServer.create(socketAcceptor)
                .bind(tcpServerTransport)
                .block();
    }
}

