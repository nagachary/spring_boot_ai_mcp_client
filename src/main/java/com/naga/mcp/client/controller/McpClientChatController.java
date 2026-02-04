package com.naga.mcp.client.controller;

import com.naga.mcp.client.service.McpClientChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/mcp/client/")
public class McpClientChatController {

    @Autowired
    private McpClientChatService mcpClientChatService;


    private final ChatClient chatClient;

    public McpClientChatController(@Qualifier(value="MCP_CLIENT") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/github/prs")
    public Mono<String> chatSingle(@RequestBody String prompt) {
        return mcpClientChatService.chat(prompt)
                .collectList()
                .map(list -> String.join("", list));
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return  Mono.fromCallable(() -> chatClient.prompt()
                .user("Use MCP tools to list open pull requests")
                .call()
                .content()).subscribeOn(Schedulers.boundedElastic());
    }
}
