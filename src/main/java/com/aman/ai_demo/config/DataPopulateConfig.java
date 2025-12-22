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
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataPopulateConfig {

    @Value("classpath:/data/sample.txt")
    private Resource fileResource;

    private final VectorStore vectorStore;
    //@PostConstruct
    public void loadDataInVectorStore() {

        TextReader textReader = new TextReader(fileResource);
        textReader.getCustomMetadata().put("filename", "sample.txt");

        List<Document> documents = textReader.get();
        log.info("Documents loaded: {}", documents.size());

        // Split large texts into smaller chunks
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        log.info("Documents after splitting: {}", splitDocuments.size());

        // Add to vector store
        vectorStore.add(splitDocuments);
    }
}
