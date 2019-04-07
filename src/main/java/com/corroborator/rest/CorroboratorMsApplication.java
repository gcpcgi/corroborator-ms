package com.corroborator.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.corroborator.rest"})
public class CorroboratorMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorroboratorMsApplication.class, args);
	}

}
