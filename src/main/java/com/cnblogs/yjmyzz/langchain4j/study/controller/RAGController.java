package com.cnblogs.yjmyzz.langchain4j.study.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RAGController {

    private static final Logger log = LoggerFactory.getLogger(RAGController.class);

    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    @Autowired
    @Qualifier("ollamaChatModel")
    @Lazy
    OllamaChatModel ollamaChatModel;

    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    OllamaEmbeddingModel ollamaEmbeddingModel;

    private interface Assistant {
        String chat(String userMessage);
    }

    @GetMapping(value = "/embed/memory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> embedMemory() {
        try {
            TextSegment segment1 = TextSegment.from("我喜欢打乒乓球");
            Embedding embedding1 = ollamaEmbeddingModel.embed(segment1).content();
            embeddingStore.add(embedding1, segment1);

            TextSegment segment2 = TextSegment.from("张三是个程序员");
            Embedding embedding2 = ollamaEmbeddingModel.embed(segment2).content();
            embeddingStore.add(embedding2, segment2);

            return ResponseEntity.ok("{\"code\":\"success\"}");
        } catch (Exception e) {
            log.error("embed-in-memory", e);
            return ResponseEntity.ok("{\"error\":\"embed in-memory error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/query/memory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> queryInMemory(@RequestParam(required = false) String query) {
        try {
            if (!StringUtils.hasText(query)) {
                query = "我最喜欢的运动是什么?";
            }

            Embedding queryEmbedding = ollamaEmbeddingModel.embed(query).content();
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(1)
                    .build();
            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
            EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);
            return ResponseEntity.ok("{\"score\":" + embeddingMatch.score() + "\",\"text\":\"" + embeddingMatch.embedded().text() + "\"}");
        } catch (Exception e) {
            log.error("query-in-memory", e);
            return ResponseEntity.ok("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    /**
     * 基于RAG的AI聊天
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/query/bot", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bot(@RequestParam(required = false) String query) {
        try {
            if (!StringUtils.hasText(query)) {
                query = "张三的职业是什么？";
            }

            ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(ollamaEmbeddingModel)
                    .maxResults(3)
                    .minScore(0.6)
                    .build();

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatModel(ollamaChatModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .contentRetriever(retriever)
                    .build();

            return ResponseEntity.ok(assistant.chat(query));
        } catch (Exception e) {
            log.error("bot", e);
            return ResponseEntity.ok("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

}
