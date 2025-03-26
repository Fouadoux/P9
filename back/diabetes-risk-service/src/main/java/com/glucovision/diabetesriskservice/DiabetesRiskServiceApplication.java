package com.glucovision.diabetesriskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DiabetesRiskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiabetesRiskServiceApplication.class, args);
	}

}
