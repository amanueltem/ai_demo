
package com.aman.ai_demo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataPopulateConfig {

    @Value("classpath:data/sample.txt")
    private Resource fileResource;

    private final VectorStore vectorStore;

    @PostConstruct
    public void init() {
        log.info("Starting data ingestion with rate-limit protection...");

        try {
            TextReader textReader = new TextReader(fileResource);
            List<Document> documents = textReader.get();

            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> splitDocs = splitter.apply(documents);

            // 1. Generate Stable IDs (UUID format for Qdrant)
            List<Document> dedupedDocs = splitDocs.stream()
                    .map(doc -> {
                        String stableUuidId = UUID.nameUUIDFromBytes(
                                doc.getText().getBytes(StandardCharsets.UTF_8)
                        ).toString();
                        return new Document(stableUuidId, doc.getText(), doc.getMetadata());
                    })
                    .toList();

            // 2. Batch and Throttle (Fix for 429 Error)
            int batchSize = 2; // Small batches for the free tier
            for (int i = 0; i < dedupedDocs.size(); i += batchSize) {
                int end = Math.min(i + batchSize, dedupedDocs.size());
                List<Document> batch = dedupedDocs.subList(i, end);

                log.info("Sending batch {} to {}/{}", i, end, dedupedDocs.size());
                
                vectorStore.add(batch);

                // Pause for 2 seconds between requests to stay within Free Tier limits
                if (end < dedupedDocs.size()) {
                    Thread.sleep(2000); 
                }
            }

            log.info("Sync complete! Data is now searchable in Qdrant.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ingestion interrupted", e);
        } catch (Exception e) {
            log.error("Failed to populate data. Check if your Mistral usage has been exceeded.", e);
        }
    }
}