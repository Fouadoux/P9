package com.glucovision.noteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

//test n°4
public class NoteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}
}
