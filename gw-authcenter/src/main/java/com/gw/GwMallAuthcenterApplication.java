package com.gw;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class GwMallAuthcenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwMallAuthcenterApplication.class, args);
	}

}
