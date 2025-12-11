package com.aman.ai_demo.chat;

import com.aman.ai_demo.config.FastApiVectorStoreAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OllamaChatModel chatModel;
    private final FastApiVectorStoreAdapter vectorStore;
    public String getResponse(String prompt){
        ChatResponse response = ChatClient.builder(chatModel)
                .build().prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .user(prompt)
                .call()
                .chatResponse();
        assert response != null;
        return response.getResult().getOutput().getText();
    }
    public String getResponseContext(String prompt){
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
                                .model(OllamaModel.PHI)// phi is not capable of parsing images please use another models
                                .temperature(0.2)
                                .build()
                )
        );
        return response.getResult().getOutput().getText();
    }
}
