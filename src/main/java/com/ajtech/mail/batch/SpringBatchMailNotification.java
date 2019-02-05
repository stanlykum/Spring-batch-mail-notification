package com.ajtech.mail.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchMailNotification {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchMailNotification.class, args).close();
	}

}

