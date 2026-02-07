package com.naga.mcp.client.configuration;

import com.naga.mcp.client.security.JwtTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class McpClientChatConfig {
    private static final Logger logger = LoggerFactory.getLogger(McpClientChatConfig.class);
    private static final String MCP_SERVER_BASE_URL = "http://localhost:8088";

    @EventListener(ApplicationReadyEvent.class)
    public void logMcpClientStatus() {
        logger.info("MCP Client configuration loaded");
        logger.info("Attempting to connect to MCP Server at: "+MCP_SERVER_BASE_URL);
        logger.info("Tools from MCP server will be automatically available to the AI model");
    }

    @Bean("MCP_AUTH_WEBCLIENT")
    public WebClient mcpWebClient(JwtTokenStore tokenStore) {
        logger.info("mcpWebClient method : ");
        return WebClient.builder()
                .baseUrl(MCP_SERVER_BASE_URL)
                .filter((request, next) -> {
                    String token = getString(tokenStore);

                    // Debug logging
                    System.out.println("Extracted token (first 20 chars): " + token.substring(0, Math.min(20, token.length())));
                    System.out.println("Token length: " + token.length());

                    ClientRequest securedRequest = ClientRequest.from(request)
                            .header("Authorization", "Bearer " + token)
                            .build();

                    return next.exchange(securedRequest)
                            .doOnNext(response -> {
                                System.out.println("Response status: " + response.statusCode());

                                response.headers()
                                        .header("X-Refresh-Token")
                                        .stream()
                                        .findFirst()
                                        .ifPresent(t -> {
                                            System.out.println("Received refresh token");
                                            String cleanedToken = t;
                                            if (t.startsWith("{")) {
                                                int start = t.indexOf("\"accessToken\":\"") + 15;
                                                int end = t.indexOf("\"", start);
                                                cleanedToken = t.substring(start, end);
                                            } else {
                                                cleanedToken = t.replaceAll("^\"|\"$", "");
                                            }
                                            tokenStore.updateToken(cleanedToken);
                                            System.out.println("Updated token in store");
                                        });
                            })
                            .doOnError(error -> {
                                System.err.println("Request failed: " + error.getMessage());
                            });
                })
                .build();
    }

    private static String getString(JwtTokenStore tokenStore) {
        String rawToken = tokenStore.getToken();

        // Extract token from JSON if needed
        String token = rawToken;
        if (rawToken.startsWith("{")) {
            int start = rawToken.indexOf("\"accessToken\":\"") + 15;
            int end = rawToken.indexOf("\"", start);
            token = rawToken.substring(start, end);
        } else {
            token = rawToken.replaceAll("^\"|\"$", "");
        }
        return token;
    }

}