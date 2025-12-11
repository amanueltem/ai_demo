package com.aman.ai_demo.chat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService service;
    @PostMapping("/chat")
    public ResponseEntity<String> callPhi(@RequestParam(value= "message",defaultValue="Tell me a joke.") String text) {
        return ResponseEntity.ok(
                service.getResponse(text)
               );
    }
    // Multimodal (image + prompt) endpoint
    @PostMapping(value = "/vision", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze an image with prompt")
    public ResponseEntity<String> analyzeImage(
            @RequestParam(value = "prompt",defaultValue = "Explain what do you see on this picture?") String prompt,
            @RequestPart("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(service.analyzeImage(prompt,image));
    }
}
