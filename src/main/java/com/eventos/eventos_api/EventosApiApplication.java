package com.eventos.eventos_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync //Envio de emails em segundo plano
public class EventosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosApiApplication.class, args);
	}

}
