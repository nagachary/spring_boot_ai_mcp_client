package com.naga.mcp.client.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
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

    public Flux<String> chat(String prompt, String customChatId) {
        return chatClient.prompt()
                .system("""
                        You are a helpful assistant with access to real-time server tools. 
                        ALWAYS prioritize using the available tools to fetch data or perform actions.
                        DO NOT rely on your internal training data if a tool can provide the information.
                        If a tool call fails or is unavailable, only then inform the user.
                        """)
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, customChatId))
                .stream()
                .content();
    }

}
