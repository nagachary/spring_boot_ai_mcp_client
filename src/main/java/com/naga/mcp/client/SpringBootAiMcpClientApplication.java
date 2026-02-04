package com.naga.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.naga.*", "com.spring.*"})
public class SpringBootAiMcpClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAiMcpClientApplication.class, args);
	}

}
