package com.gw.gwmall;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)/*(exclude = GlobalTransactionAutoConfiguration.class)*/
@EnableDiscoveryClient
@EnableFeignClients
public class GwMallCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwMallCartApplication.class, args);
	}

}
