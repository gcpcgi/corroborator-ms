package com.corroborator.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;

import com.corroborator.rest.bean.FileStorageBean;

@SpringBootApplication(scanBasePackages = { "com.corroborator.rest" })
@EnableConfigurationProperties({ FileStorageBean.class })
public class CorroboratorMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorroboratorMsApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "Hello World! The Corroborator microservices are up and running!";
	}

	/**
	 * (Optional) App Engine health check endpoint mapping.
	 * 
	 * @see <a href=
	 *      "https://cloud.google.com/appengine/docs/flexible/java/how-instances-are-managed#health_checking"></a>
	 *      If your app does not handle health checks, a HTTP 404 response is
	 *      interpreted as a successful reply.
	 */
	@RequestMapping("/_ah/health")
	public String healthy() {
		// Message body required though ignored
		return "Still surviving.";
	}
}