package com.aman.ai_demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;
    public String getResponse(String prompt) {
        // .content() is a shortcut to get the string output directly
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
    public String generation(String userInput){
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }
    public  ActorFilms actorFilms(){
        return chatClient.prompt()
                .user("Generate the filmography for a random actor.")
                .call()
                .entity(ActorFilms.class);
    }
    public List<Person> footballers(){
        return chatClient.prompt()
                .user("Generate 5 famous football players.")
                .call()
                .entity(new ParameterizedTypeReference<List<Person>>() {
                });
    }
    public List<Person> nativeFootballers(){
        return chatClient.prompt()
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .user("Generate 5 famous football players.")
                .call()
                .entity(new ParameterizedTypeReference<List<Person>>() {
                });
    }
    public String templateMovies() {
        return chatClient.prompt()
                .user(u -> u
                        .text("Tell me the names of 5 movies whose soundtrack was composed by {composer}")
                        .param("composer", "John Williams"))
                .call()
                .content();
    }
    public String analyzeImage(String prompt, MultipartFile file) throws IOException {
        return "coming soon";
    }

}
