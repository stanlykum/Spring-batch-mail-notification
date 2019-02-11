package com.cts.batch.config;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import feign.RequestLine;

@Configuration
public class FeignConfiguration {

	@FeignClient(name = "token-detoken-service",url="http://localhost:9999")
	public interface TokenFeignClient {
		@GetMapping("api/v1/token/{cardNo}")
		//@RequestLine("GET /token/{cardNo}")
		public String retrieveTokenByCardNo(@Valid @PathVariable("cardNo") String cardNo);
	}

}
