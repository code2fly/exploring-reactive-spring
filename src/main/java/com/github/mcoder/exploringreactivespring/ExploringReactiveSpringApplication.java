package com.github.mcoder.exploringreactivespring;

import com.github.mcoder.exploringreactivespring.repositories.ReservationRepository;
import com.github.mcoder.exploringreactivespring.service.ReservationService;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DatabaseClientExtensionsKt;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import java.util.function.Consumer;

// TODO replace mongo docker-compose file to prostgres , containerize both app and postgres to connect with each other
@SpringBootApplication
public class ExploringReactiveSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExploringReactiveSpringApplication.class, args);
	}


	@Bean
	ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory cf) {
		return new R2dbcTransactionManager(cf);
	}

}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializer {

	private final ReservationRepository reservationRepository;
	private final ReservationService reservationService;

//	databaseclient is like jdbctemplate for reactive
//	private final DatabaseClient databaseClient;

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {

/*

		this.databaseClient
				.execute("CREATE TABLE RESERVATION (id SERIAL PRIMARY KEY, name TEXT NOT NULL)")
				.then()
				.thenMany(Flux.just("lalu", "rabri", "nitish", "tejaswi", "lalten"))
				.map(name -> new Reservation(null, name))
				.subscriberContext(Context.of("name", "lalu", "partner","rabri" ))
				.flatMap(reservation -> this.reservationRepository.save(reservation))
				.doOnEach(signal -> {
					log.info("checking context somewhere in the pipeline : {}", signal.getContext().size());
					signal.getContext().stream().forEach(entry -> {
						log.info("inside context details, with key : {} , value: {}", entry.getKey(), entry.getValue());
					});
				});
*/






		// nothing would happen by running just the above code, since we have not subscribed to above publisher (right now it is cold stream , once we subscribe they become hot stream)
//		reservations.subscribe();

		this.reservationRepository
				.deleteAll()
				.thenMany(reservationService.saveAll("lalu", "rabri", "nitish", "tejaswi", "lalten"))
				.thenMany(this.reservationRepository.findAll())
				.subscribe(log::info);

/*
		there is a feature in mongodb(not exactly like continuous query like in other distributed datastores like gemfire, influx etc.. ) that lets us say that when this data is ready push it to me rather than us pull it down, db should push data to us
		the feature is tailable cursor, very natural thing to do with reactive programming .. for this db has to be started in replicated set mode.
*/
	}

}




