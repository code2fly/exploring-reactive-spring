package com.github.mcoder.exploringreactivespring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.service.ReservationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

// todo check why browser is not able to connect to the websocket
@Configuration
public class WebSocketConfiguration {

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/reservations", wsh));
    }


    @Bean
    public WebSocketHandler webSocketHandler(ReservationService reservationService) {
        return new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {
                Flux<WebSocketMessage> inputMsg = session.receive();
                Flux<String> inputMsgAsString = inputMsg.map(webSocketMessage -> webSocketMessage.getPayloadAsText());
                Flux<Reservation> reservations = inputMsgAsString.flatMap(msg -> reservationService.getManyReservations());
                Flux<String> reservationAsString = reservations.map(reservation -> reservation.toString());
                Flux<WebSocketMessage> responseMsg = reservationAsString.map(session::textMessage);
                return session.send(responseMsg);
            }
        };
    }


    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
