
package com.aman.ai_demo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataPopulateConfig {

    @Value("classpath:data/sample.txt")
    private Resource fileResource;

    private final VectorStore vectorStore;

    @PostConstruct
    public void init() {
            TextReader textReader = new TextReader(fileResource);
            List<Document> documents = textReader.get();
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> splitDocs = splitter.apply(documents);

            if (splitDocs.isEmpty()) return;

            log.info("Checking if data already exists in Qdrant...");

            // Check the first chunk of text
            String firstChunkText = splitDocs.get(0).getText();
            
            // Search for the exact content of the first chunk
            List<Document> existing = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(firstChunkText)
                            .topK(1)
                            .similarityThreshold(0.95) // If it's 95%+ similar, it's already there
                            .build()
            );

            if (!existing.isEmpty()) {
                log.info("Match found in Qdrant. Skipping ingestion to save Mistral credits.");
                return;
            }

            log.info("No matching data found. Starting ingestion...");

    }
  
}