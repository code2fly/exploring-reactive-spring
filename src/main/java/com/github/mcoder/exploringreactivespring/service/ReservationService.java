package com.github.mcoder.exploringreactivespring.service;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.repositories.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Stream.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

//     similar to TransactionTemplate
//    private final TransactionalOperator transactionalOperator;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Flux<Reservation> saveAll(String... names) {

        Flux<Reservation> reservations = Flux
                .fromArray(names)
                .map(name -> new Reservation(null, name))
                .flatMap(this.reservationRepository::save)
                .doOnNext(this::assertDateIsValid)
                .doOnEach(reservationSignal -> {
                    reservationSignal.getContext().stream().forEach(entry -> log.info("context has key: {}, value: {}", entry.getKey(), entry.getValue()));
                });

        return reservations;
    }



    public Flux<Reservation> findReservationByName(String name) {
        return this.reservationRepository.findByName(name);
    }

    public Mono<Reservation> findReservationById(String id) {
        return this.reservationRepository.findById(id);
    }

    public Flux<Reservation> getManyReservations() {
        Random random = new Random();
        return Flux.
                fromStream(generate(() -> new Reservation(random.nextInt(), "lalu " + Instant.now())))
                .delayElements(Duration.ofSeconds(1))
//        below is not required in this case ideally since there is nothing blocking as such
                .subscribeOn(Schedulers.elastic());  // this is to change the scheduler used to subscribe, useful if there are blocking io calls
    }

    private void assertDateIsValid(Reservation reservation) {
        log.info("current reservation is : {}", reservation);
        Assert.isTrue(!reservation.getName().equalsIgnoreCase("pappu"), "is not a valid name");
    }

    public Flux<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }
}
