package com.github.mcoder.exploringreactivespring.controller;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

//    this is MVC style creation of REST API, it is useful if there are other api's exposed in this class and they would like to reuse reservationesrvice in some way
    @GetMapping("/api/reservation/{name}")
    public Mono<Reservation> getReservationByPersonName(@PathVariable("name") String name) {
        return Mono.from( reservationService.
                findReservationByName(name));
    }

}
