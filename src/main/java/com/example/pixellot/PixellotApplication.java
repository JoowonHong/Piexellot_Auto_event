package com.example.pixellot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PixellotApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixellotApplication.class, args);
	}

}
