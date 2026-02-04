package com.naga.mcp.client.controller;

import com.naga.mcp.client.service.McpClientChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcp/client/chat")
public class McpClientChatController {

    @Autowired
    private McpClientChatService mcpClientChatService;

    @Qualifier(value="MCP_CLIENT")
    private ChatClient chatClient;


    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> chat(@RequestParam String prompt) {
        return mcpClientChatService.chat(prompt);
    }

    @GetMapping("/test")
    public String test() {
        return chatClient.prompt()
                .user("Use MCP tools to list open pull requests")
                .call()
                .content();
    }



}
