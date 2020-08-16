package com.github.mcoder.exploringreactivespring.client;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
//@AllArgsConstructor
public class Client {

    private final WebClient client;
//    this is expensive and reusable so creating it as a part of bean creation and then reusing it
    private final ReactiveCircuitBreaker circuitBreaker;

    public Client(WebClient client, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.client = client;
        this.circuitBreaker = circuitBreakerFactory.create("reservation");
    }

//    @EventListener(ApplicationReadyEvent.class)
//    @Order(2)
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

        Mono<Reservation> httpCall = client
                .get()
                .uri("/reservation/{name}", Map.of("name", name))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Reservation.class);
//                .retryWhen(Retry.max(3))
//                .onErrorResume(ex -> {
//                    log.info("exception is : {}", ex);
//                    return Mono.just(new Reservation(10, "babu"));
//                })


        this.circuitBreaker
                .run(httpCall, ex -> Mono.just(new Reservation(12, "lallu")) )
                .subscribe(reservation -> log.info("reservation mono : {}", reservation));

    }
}
