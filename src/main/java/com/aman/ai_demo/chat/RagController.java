package com.aman.ai_demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rags")
@RequiredArgsConstructor
@RestController
public class RagController {
    
    private final RagService service;

    @PostMapping("/chat")
    public ResponseEntity<String> chatRag(@RequestParam(value= "message", defaultValue="Who is Amanuel Temesgen") String text) {
        // 1. Call the service
        String response = service.getChatResponse(text);
        
        // 2. Return the string directly in the body
        return ResponseEntity.ok(response); 
    }
}