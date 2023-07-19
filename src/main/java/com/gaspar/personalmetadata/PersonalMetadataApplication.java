package com.gaspar.personalmetadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PersonalMetadataApplication {

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(PersonalMetadataApplication.class, args);
	}

	public static void shutdown(int exitCode) {
		SpringApplication.exit(applicationContext, () -> exitCode);
	}

}
