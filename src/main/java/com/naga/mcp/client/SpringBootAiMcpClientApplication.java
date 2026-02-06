package com.naga.mcp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan(basePackages = {"com.naga.*", "org.spring.*"})
public class SpringBootAiMcpClientApplication {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootAiMcpClientApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Spring Boot AI MCP Client Application");
		SpringApplication.run(SpringBootAiMcpClientApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("========================================");
		logger.info("✓ MCP Client started successfully");
		logger.info("✓ Chat endpoint: http://localhost:8089/api/chat");
		logger.info("✓ Test endpoint: http://localhost:8089/api/test");
		logger.info("✓ Health: http://localhost:8089/actuator/health");
		logger.info("✓ Connected to MCP Server: http://localhost:8088/mcp/sse");
		logger.info("✓ Using Ollama model: mistral");
		logger.info("========================================");
	}
}