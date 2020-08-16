package com.github.mcoder.exploringreactivespring.rsocket.spring;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationRsocketController {

    private final ReservationService reservationService;

//    we can also inject the request as a method argument
    @MessageMapping("reservation.{timeInSeconds}")
    public Flux<Reservation> getReservations(@DestinationVariable int timeInSeconds) {
        return this.reservationService.getManyReservations();
    }


}
