package com.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GwMallUnqidApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwMallUnqidApplication.class, args);
	}

}
