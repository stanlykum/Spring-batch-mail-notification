package com.cts.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableBatchProcessing
@EnableFeignClients
@EnableDiscoveryClient
//@EnableHystrix
public class SpringBatchMailNotification {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchMailNotification.class, args).close();
	}

}

