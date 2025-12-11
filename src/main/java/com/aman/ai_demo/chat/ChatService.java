package com.aman.ai_demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;
    public String getResponse(String prompt){
        ChatResponse chatResponse=chatClient.prompt(prompt).call().chatResponse();
        assert chatResponse != null;
        return chatResponse.getResult().getOutput().getText();
    }
    public String analyzeImage(String prompt, MultipartFile file) throws IOException {
        return "coming soon";
    }

}
