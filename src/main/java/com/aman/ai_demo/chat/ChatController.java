package com.aman.ai_demo.chat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


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
    @PostMapping("/generation")
    public ResponseEntity<String> generation(@RequestParam(value= "message",
            defaultValue="Tell me a joke.") String text) {
        return ResponseEntity.ok(
                service.generation(text)
        );
    }

    @GetMapping("/actor")
    public ResponseEntity<ActorFilms> getActor() {
        return ResponseEntity.ok(
                service.actorFilms()
        );
    }

    @GetMapping("/famous-footballers")
    public ResponseEntity<List<Person>> knownFootballPlayers() {
        return ResponseEntity.ok(
                service.footballers()
        );
    }

    @GetMapping("/native-famous-footballers")
    public ResponseEntity<List<Person>> nativeFamousFootballers() {
        return ResponseEntity.ok(
                service.nativeFootballers()
        );
    }
    @GetMapping("/template-movies")
    public ResponseEntity<String> templateMovies() {
        return ResponseEntity.ok(
                service.templateMovies()
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
