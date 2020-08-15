package com.github.mcoder.exploringreactivespring.repositories;

import com.github.mcoder.exploringreactivespring.model.Reservation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {
    //	@Tailable for tailable query feature we can just use this annotation if db is started in that mode.
    @Query("SELECT * FROM RESERVATION WHERE name = :name")
    Flux<Reservation> findByName(String name);
}
