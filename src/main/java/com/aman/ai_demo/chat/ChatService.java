package com.aman.ai_demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OllamaChatModel chatModel;
    public String getResponse(String prompt){
        ChatResponse chatResponse=chatModel.call(
                new Prompt(
                        prompt,
                        OllamaChatOptions.builder()
                                .model(OllamaModel.PHI)
                                .temperature(0.4)
                                .build()
                ));
       return chatResponse.getResult().getOutput().getText();
    }
    public String analyzeImage(String prompt, MultipartFile file) throws IOException {
        ByteArrayResource imageResource= new ByteArrayResource(file.getBytes());
        Media media = new Media(MimeTypeUtils.IMAGE_PNG, imageResource);
        UserMessage userMessage = UserMessage.builder()
                .text(prompt)
                .media(media)
                .build();
        ChatResponse response = chatModel.call(
                new Prompt(
                        userMessage,
                        OllamaChatOptions.builder()
                                .model(OllamaModel.PHI)
                                .temperature(0.2)
                                .build()
                )
        );
        return response.getResult().getOutput().getText();
    }
}
