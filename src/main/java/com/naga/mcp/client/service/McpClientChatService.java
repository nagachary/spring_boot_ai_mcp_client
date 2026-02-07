package com.naga.mcp.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class McpClientChatService {
    private static final Logger logger = LoggerFactory.getLogger(McpClientChatService.class);

    private final ChatModel chatModel;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public McpClientChatService(OllamaChatModel chatModel,
                                @Qualifier("MCP_AUTH_WEBCLIENT") WebClient webClient) {
        this.chatModel = chatModel;
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
        logger.info("McpClientChatService initialized with secured WebClient");
    }

    /**
     * Main chat method - automatically uses MCP tools when needed
     */
    public String chat(String userMessage) {
        logger.info("User: {}", userMessage);

        try {
            // Step 1: Check if question is about pull requests
            if (isPullRequestQuestion(userMessage)) {
                // Step 2: Get PR data from MCP server
                String prData = getPullRequests(userMessage);

                // Step 3: Ask AI to format the response
                String prompt = String.format(
                        "Based on this pull request data:\n%s\n\n" +
                                "Answer this question: %s",
                        prData, userMessage
                );

                String response = askAI(prompt);
                logger.info("AI: {}", response);
                return response;
            } else {
                // Just use AI for non-PR questions
                String response = askAI(userMessage);
                logger.info("AI: {}", response);
                return response;
            }

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }

    /**
     * Check if question is about pull requests
     */
    private boolean isPullRequestQuestion(String message) {
        String lower = message.toLowerCase();
        return lower.contains("pull request") ||
                lower.contains("pr") ||
                lower.contains("github");
    }

    /**
     * Get pull requests from MCP server
     */
    private String getPullRequests(String message) throws Exception {
        // Determine state from message
        String state = "open";
        if (message.toLowerCase().contains("closed")) {
            state = "closed";
        } else if (message.toLowerCase().contains("all")) {
            state = "all";
        }

        // Call MCP tool
        Map<String, Object> request = new HashMap<>();
        request.put("name", "getAllPullRequests");
        request.put("arguments", Map.of("state", state));

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient
                .post()
                .uri("/mcp/tools/call")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && "success".equals(response.get("status"))) {
            return objectMapper.writeValueAsString(response.get("result"));
        } else {
            throw new RuntimeException("Failed to get PRs: " + response);
        }
    }

    /**
     * Ask AI using Ollama
     */
    private String askAI(String message) {
        Prompt prompt = new Prompt(message);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}