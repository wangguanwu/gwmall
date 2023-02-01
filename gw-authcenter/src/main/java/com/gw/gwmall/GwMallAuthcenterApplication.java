package com.gw.gwmall;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableFeignClients
public class GwMallAuthcenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwMallAuthcenterApplication.class, args);
	}

}
