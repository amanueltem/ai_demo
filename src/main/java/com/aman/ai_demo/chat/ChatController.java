package com.aman.ai_demo.chat;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
	 private final OllamaChatModel chatModel;

	    @Autowired
	    public ChatController(OllamaChatModel chatModel) {
	        this.chatModel = chatModel;
	    }
    @PostMapping("/phi")
    public ChatResponse callPhi(@RequestParam(value= "message",defaultValue="Tell me a joke.") String text) {
    	ChatResponse response = chatModel.call(
    		    new Prompt(
    		        text,
    		        OllamaOptions.builder()
    		            .model(OllamaModel.PHI)
    		            .temperature(0.4)
    		            .build()
    		    ));
    	return response;
    }
    
    
    
}
