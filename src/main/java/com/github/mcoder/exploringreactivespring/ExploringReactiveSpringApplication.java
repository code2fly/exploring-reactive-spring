package com.github.mcoder.exploringreactivespring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DatabaseClientExtensionsKt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@SpringBootApplication
public class ExploringReactiveSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExploringReactiveSpringApplication.class, args);
	}

}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializer {

	private final ReservationRepository reservationRepository;

//	databaseclient is like jdbctemplate for reactive
	private final DatabaseClient databaseClient;

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {

		this.databaseClient
				.select()
				.from("reservation").as(Reservation.class)
				.fetch()
				.all()
				.subscribe(log::info);


		Flux<String> names = Flux.just("lalu", "rabri", "nitish", "tejaswi", "lalten");
		Flux<Reservation> reservations = names.map(name -> new Reservation(null, name))
				.flatMap(reservation -> this.reservationRepository.save(reservation));

		// nothing would happen by running just the above code, since we have not subscribed to above publisher (right now it is cold stream , once we subscribe they become hot stream)
//		reservations.subscribe();

		this.reservationRepository
				.deleteAll()
				.thenMany(reservations)
				.thenMany(this.reservationRepository.findAll())
				.subscribe(log::info);

/*
		there is a feature in mongodb(not exactly like continuous query like in other distributed datastores like gemfire, influx etc.. ) that lets us say that when this data is ready push it to me rather than us pull it down, db should push data to us
		the feature is tailable cursor, very natural thing to do with reactive programming .. for this db has to be started in replicated set mode.
*/
	}

}



interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {
//	@Tailable for tailable query feature we can just use this annotation if db is started in that mode.
	Flux<Reservation> findByName(String name);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	@Id
	private String id;
	private String name;

}

