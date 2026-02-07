package com.naga.mcp.client.controller;

import com.naga.mcp.client.service.McpClientChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mcp/client/")
public class McpClientChatController {

    private static final Logger logger = LoggerFactory.getLogger(McpClientChatController.class);

    @Autowired
    private McpClientChatService chatService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        logger.info("chat method : ");
        String message = request.get("message");
        String response = chatService.chat(message);
        return Map.of("response", response);
    }

    @GetMapping("/chat")
    public Map<String, String> chatGet(@RequestParam String message) {
        logger.info("chatGet method : ");
        String response = chatService.chat(message);
        return Map.of("response", response);
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MCP Client Chat Service"
        ));
    }
}
