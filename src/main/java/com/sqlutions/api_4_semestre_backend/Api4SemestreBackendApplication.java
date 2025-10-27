package com.sqlutions.api_4_semestre_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Api4SemestreBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Api4SemestreBackendApplication.class, args);
	}

}
