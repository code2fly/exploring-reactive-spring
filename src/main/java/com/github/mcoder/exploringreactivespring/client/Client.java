package com.github.mcoder.exploringreactivespring.client;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class Client {

    private final WebClient client;

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void readyToCallReservationStreamingApi() {
        log.info("running client logic to  trigger STREAMING api, should be run after server logic ");
        client.get()
                .uri("/reservations/random")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(Reservation.class)
                .subscribe(reservation -> log.info("reservation stream : {}", reservation));
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void readyToCallReservationApi() {
        var name = "lalu";
        log.info("running client logic to trigger NORMAL api, should be run after server logic ");

            client
                .get()
                .uri("/reservation/{name}", Map.of("name", name) )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Reservation.class)
                .subscribe(reservation -> log.info("reservation mono : {}", reservation));
    }
}
