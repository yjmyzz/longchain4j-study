package com.cnblogs.yjmyzz.langchain4j.study.controller;

import com.cnblogs.yjmyzz.langchain4j.study.listener.CustomAiServiceCompletedListener;
import com.cnblogs.yjmyzz.langchain4j.study.listener.CustomAiServiceStartedListener;
import com.cnblogs.yjmyzz.langchain4j.study.service.ChineseStreamTeacher;
import com.cnblogs.yjmyzz.langchain4j.study.service.ChineseTeacher;
import com.cnblogs.yjmyzz.langchain4j.study.service.Poem;
import com.cnblogs.yjmyzz.langchain4j.study.service.PoemExtractor;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TraceController {

    @Autowired
    @Qualifier("ollamaChatModel")
    @Lazy
    OllamaChatModel ollamaChatModel;

    @Autowired
    @Qualifier("streamingChatModel")
    @Lazy
    StreamingChatModel streamingChatModel;

    /**
     * 演示AIService基本用法
     * by 菩提树下的杨过(yjmyzz.cnblogs.com)
     * @param query
     * @return
     */
    @GetMapping(value = "/aiservice/1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> demo1(@RequestParam(defaultValue = "请问李清照最广为流传的词是哪一首,请给出这首词全文？") String query) {
        try {
            ChineseTeacher teacher = AiServices.builder(ChineseTeacher.class)
                    .chatModel(ollamaChatModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();
            return ResponseEntity.ok(teacher.chat(query));
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\":\"chatChain error: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 演示AIService基本用法+结构化返回
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/aiservice/2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Poem> demo2(@RequestParam(defaultValue = """
            请问李清照最广为流传的词是哪一首,
            请给出这首词全文（以json格式输出，类似{\"author\":\"...\",\"title\":\"...\",\"content\":\"...\"}）？""") String query) {
        try {
            Poem extract = AiServices.builder(PoemExtractor.class)
                    .chatModel(ollamaChatModel).build()
                    .extract(AiServices.builder(ChineseTeacher.class)
                            .chatModel(ollamaChatModel)
                            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                            .build().chat(query));
            return ResponseEntity.ok(extract);
        } catch (Exception e) {
            return ResponseEntity.ok(new Poem("error", "error", e.getMessage()));
        }
    }

    /**
     * 演示AIService基本用法+流式返回
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/aiservice/3", produces = "text/html;charset=utf-8")
    public Flux<String> demo3(@RequestParam(defaultValue = "请问李清照最广为流传的词是哪一首,请给出这首词全文？") String query) {
        ChineseStreamTeacher teacher = AiServices.builder(ChineseStreamTeacher.class)
                .streamingChatModel(streamingChatModel)
                .build();

        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        teacher.chat(query)
                .onPartialResponse((String s) -> sink.tryEmitNext(escapeToHtml(s)))
                .onCompleteResponse((ChatResponse response) -> sink.tryEmitComplete())
                .onError(sink::tryEmitError)
                .start();
        return sink.asFlux();
    }


    /**
     * 演示AIService基本用法+自定义监听器
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/aiservice/4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> demo4(@RequestParam(defaultValue = "请问李清照最广为流传的词是哪一首,请给出这首词全文？") String query) {
        try {
            ChineseTeacher teacher = AiServices.builder(ChineseTeacher.class)
                    .chatModel(ollamaChatModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    //加入监听器
                    .registerListeners(List.of(new CustomAiServiceStartedListener(), new CustomAiServiceCompletedListener()))
                    .build();
            return ResponseEntity.ok(teacher.chat(query));
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\":\"chatChain error: " + e.getMessage() + "\"}");
        }
    }


    public String escapeToHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\n", "<br/>")
                .replace("<think>", "&lt;think&gt;")
                .replace("</think>", "&lt;/think&gt;");
    }

}
