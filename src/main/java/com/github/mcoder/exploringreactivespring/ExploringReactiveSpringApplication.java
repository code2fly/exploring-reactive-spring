package com.github.mcoder.exploringreactivespring;

import com.github.mcoder.exploringreactivespring.model.Reservation;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.function.Consumer;

import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@SpringBootApplication
@EnableTransactionManagement
public class ExploringReactiveSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExploringReactiveSpringApplication.class, args);
	}


	@Bean
	RouterFunction<ServerResponse> routes(ReservationService reservationService) {
		return route()
				.GET("/api/reservations",
						req -> ServerResponse.ok().body(reservationService.findAllReservations(), Reservation.class))
				.GET("/api/reservationbyid/{id}", req -> ServerResponse.ok().body(reservationService.findReservationById(req.pathVariable("id")), Reservation.class))
				.build();
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




