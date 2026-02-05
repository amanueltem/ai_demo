package com.aman.ai_demo.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RagService {

    private final ChatClient chatClient;

    public RagService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultAdvisors(
                    // Use the static builder instead of the constructor
                    QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().build()) 
                        .build()
                )
                .build();
    }
    public String getChatResponse(String userQuery) {
        return chatClient.prompt()
                .user(userQuery)
                .call()
                .content();
    }
}