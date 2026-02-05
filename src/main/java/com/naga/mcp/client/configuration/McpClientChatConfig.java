package com.naga.mcp.client.configuration;


import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpClientChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean("MCP_CLIENT")
    public ChatClient chatClient(ChatModel model, ChatMemory chatMemory, SyncMcpToolCallbackProvider mcpToolProvider) {
        return ChatClient.builder(model)
                .defaultToolCallbacks(mcpToolProvider.getToolCallbacks())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
    @Bean
    public CommandLineRunner listMcpTools(List<McpSyncClient> mcpClients) {
        return args -> {
            System.out.println("=== MCP Tool Discovery ===");
            for (McpSyncClient client : mcpClients) {
                var response = client.listTools();

                System.out.println("Server: " + client.getServerInfo().name());
                response.tools().forEach(tool -> {
                    System.out.println(" -> Tool Name: " + tool.name());
                    System.out.println("    Description: " + tool.description());
                    System.out.println("    Input Schema: " + tool.inputSchema());
                });
            }
            System.out.println("==========================");
        };
    }
}
