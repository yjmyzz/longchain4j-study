package com.cnblogs.yjmyzz.langchain4j.study.controller;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChainController {

    private static final Logger log = LoggerFactory.getLogger(ChainController.class);

    @Autowired
    @Qualifier("ollamaChatModel")
    @Lazy
    OllamaChatModel ollamaChatModel;

    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    OllamaEmbeddingModel ollamaEmbeddingModel;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping(value = "/chat/chain", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> chatChain(@RequestParam String query) {
        try {
            String responseText = ConversationalChain.builder()
                    .chatModel(ollamaChatModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build()
                    .execute(query);
            return ResponseEntity.ok(responseText);
        } catch (Exception e) {
            log.error("chatChain", e);
            return ResponseEntity.ok("{\"error\":\"chatChain error: " + e.getMessage() + "\"}");
        }
    }

    EmbeddingStore<TextSegment> getEmbeddingStore() {
        Resource resource = resourceLoader.getResource("classpath:data.txt");
        File file = null;
        try {
            file = resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String path = file.getAbsolutePath();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        Document document = FileSystemDocumentLoader.loadDocument(path);
        DocumentByLineSplitter splitter = new DocumentByLineSplitter(100, 0);
        List<TextSegment> split = splitter.split(document);
        for (TextSegment textSegment : split) {
            Embedding embedding = ollamaEmbeddingModel.embed(textSegment).content();
            embeddingStore.add(embedding, textSegment);
        }
        return embeddingStore;
    }

    @GetMapping(value = "/rag/chain", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ragChain(@RequestParam String query) {
        try {
            String answer = ConversationalRetrievalChain.builder()
                    .chatModel(ollamaChatModel)
                    .contentRetriever(EmbeddingStoreContentRetriever
                            .builder()
                            .embeddingModel(ollamaEmbeddingModel)
                            .embeddingStore(getEmbeddingStore())
                            .maxResults(1)
                            .build())
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build()
                    .execute(query);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            log.error("argChain", e);
            return ResponseEntity.ok("{\"error\":\"argChain error: " + e.getMessage() + "\"}");
        }
    }


}
