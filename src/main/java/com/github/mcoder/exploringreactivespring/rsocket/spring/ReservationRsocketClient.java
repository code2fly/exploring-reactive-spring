package com.github.mcoder.exploringreactivespring.rsocket.spring;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.model.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

// lazy is added just to make sure application is up before client tries to connect to rsocket of same application

@Component
@Slf4j
@RequiredArgsConstructor
@Lazy
public class ReservationRsocketClient {

    private final RSocketRequester rSocketRequester;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        log.info("inside spring rsocket consumer ....");
        rSocketRequester
                .route("reservation.{timeseconds}", 1)
                .data(new ReservationRequest("lalu"))
                .retrieveFlux(Reservation.class)
                .subscribe(reservation -> log.info("Reservation from spring consumer: {}", reservation));

    }

}

@Configuration
class ClientConfig {

    @Bean
    @Lazy
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .connectTcp("localhost", 7001)
                .block();
    }
}
