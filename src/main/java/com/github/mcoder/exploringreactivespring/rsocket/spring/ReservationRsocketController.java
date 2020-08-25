package com.github.mcoder.exploringreactivespring.rsocket.spring;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import com.github.mcoder.exploringreactivespring.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
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
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (User) authentication.getPrincipal())
                .map(User::getUsername)
                // todo we can use the username from authentication context in the next step if we want (not doing it in below example)
                .flatMapMany(usrname -> this.reservationService.getManyReservations());
    }


}
