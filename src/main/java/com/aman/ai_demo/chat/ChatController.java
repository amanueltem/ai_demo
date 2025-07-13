package com.aman.ai_demo.chat;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ChatController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/phi")
    public String callPhi(@RequestBody String text) {
        String prompt =text;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
            "model", "phi",
            "prompt", prompt,
            "stream", false
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:11434/api/generate", request, Map.class);

        // Extract the generated text from the response
        return response.getBody().get("response").toString();
    }
}
