package com.corroborator.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.corroborator.rest.bean.FileStorageBean;

@SpringBootApplication(scanBasePackages = {"com.corroborator.rest"})
@EnableConfigurationProperties({
	FileStorageBean.class
})
public class CorroboratorMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorroboratorMsApplication.class, args);
	}

}