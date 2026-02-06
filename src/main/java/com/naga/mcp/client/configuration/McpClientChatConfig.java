package com.naga.mcp.client.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class McpClientChatConfig {
    private static final Logger logger = LoggerFactory.getLogger(McpClientChatConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public void logMcpClientStatus() {
        logger.info("MCP Client configuration loaded");
        logger.info("Attempting to connect to MCP Server at: http://localhost:8088/mcp/sse");
        logger.info("Tools from MCP server will be automatically available to the AI model");
    }
}