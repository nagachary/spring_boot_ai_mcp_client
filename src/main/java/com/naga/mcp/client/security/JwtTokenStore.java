package com.naga.mcp.client.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naga.mcp.client.SpringBootAiMcpClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JwtTokenStore {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenStore.class);

    private final String X_API_KEY ="X-API-KEY";
    private String token;

    @Value("${mcp.server.auth.token.endpoint}")
    private String serverUrl;

    @Value("${mcp.server.auth.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public synchronized String getToken() {
        if (token == null) {
            token = fetchToken();
        }
        return token;
    }

    public synchronized void updateToken(String newToken) {
        this.token = newToken;
    }

    private String fetchToken() {
        logger.info("fetchToken : ");
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_API_KEY, apiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch JWT token from MCP server");
        }
    }
}
