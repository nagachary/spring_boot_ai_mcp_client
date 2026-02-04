package com.naga.mcp.client.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class McpClientChatService {

    @Autowired
    private final ChatClient chatClient;

    public McpClientChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Flux<String> chat(String prompt) {
        return chatClient.prompt().user(prompt).stream().content();
    }

}
