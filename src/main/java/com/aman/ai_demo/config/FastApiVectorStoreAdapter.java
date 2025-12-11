package com.aman.ai_demo.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FastApiVectorStoreAdapter implements VectorStore {

    private final WebClient webClient = WebClient.create("http://127.0.0.1:8000");

    @Override
    public void add(List<Document> documents) {
        // Send documents to FastAPI /add_document_batch endpoint
        List<String> contents = documents.stream()
                .map(d -> d.getText() != null ? d.getText() : "")
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("documents", contents);

        webClient.post()
                .uri("/add_document_batch")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    @Override
    public void delete(List<String> idList) {
        if (idList == null || idList.isEmpty()) return;

        for (String id : idList) {
            webClient.delete()
                    .uri("/delete_document/{id}", id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        // Not implemented, optional
        throw new UnsupportedOperationException("Filter-based delete not supported");
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", request.getQuery());
        payload.put("top_k", request.getTopK()); // Make sure your SearchRequest has getTopK()

        // Call FastAPI /query endpoint
        Map<String, Object> response = webClient.post()
                .uri("/query")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> resultsFromAPI = (List<Map<String, Object>>) response.get("results");
        if (resultsFromAPI == null) return Collections.emptyList();

        List<Document> documents = new ArrayList<>();
        for (Map<String, Object> result : resultsFromAPI) {
            String id = (String) result.get("id");
            String content = (String) result.get("content");
            Double distance = result.get("distance") != null ? ((Number) result.get("distance")).doubleValue() : null;

            Document doc = Document.builder()
                    .id(id)
                    .text(content)
                    .metadata(distance != null ? Map.of("distance", distance) : Map.of())
                    .build();
            documents.add(doc);
        }

        return documents;
    }
}
