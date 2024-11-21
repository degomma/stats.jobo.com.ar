package com.jobo.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class StatsApplication {
	public static void main(String[] args) {
		SpringApplication.run(StatsApplication.class, args);
	}
}