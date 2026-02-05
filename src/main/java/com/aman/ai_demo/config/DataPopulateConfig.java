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
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataPopulateConfig {

    @Value("classpath:data/sample.txt")
    private Resource fileResource;

    private final VectorStore vectorStore;

    @PostConstruct
    public void init() {
        log.info("Checking for data updates in Qdrant Cloud...");

        TextReader textReader = new TextReader(fileResource);
        List<Document> documents = textReader.get();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocs = splitter.apply(documents);

        // Generate Stable IDs based on content hash
        List<Document> dedupedDocs = splitDocs.stream()
                .map(doc -> {
                    // Create a unique ID by hashing the text content
                    String hashId = DigestUtils.md5DigestAsHex(
                            doc.getText().getBytes(StandardCharsets.UTF_8)
                    );
                    // Create a new Document object with the same content/metadata but the new ID
                    return new Document(hashId, doc.getText(), doc.getMetadata());
                })
                .toList();

        // Qdrant performs an UPSERT. If ID exists, nothing new is stored.
        vectorStore.add(dedupedDocs);
        log.info("Sync complete. Current version of sample.txt is active in the cloud.");
    }
}