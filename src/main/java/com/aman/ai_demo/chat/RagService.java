package com.aman.ai_demo.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    @Value("classpath:/data/sample.txt")
    private Resource fileResource;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    /**
     * Load and embed documents into the vector store
     */
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

    public String getChat(String prompt) {
        // Ensure data is loaded
        loadDataInVectorStore();
        // Retrieve top 5 relevant documents
        List<Document> contextDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(prompt)
                        .topK(5)
                        .build()
        );

        String context = contextDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // Send context + question to chat model
        return chatClient.prompt(
                        "Answer the following question using the context below:\n\n" +
                                context +
                                "\n\nQuestion: " + prompt
                ).call()
                .content();
    }
}
