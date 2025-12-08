package com.cnblogs.yjmyzz.langchain4j.study.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExtractDataController {

    private static final Logger log = LoggerFactory.getLogger(ExtractDataController.class);

    @Autowired
    @Qualifier("ollamaChatModel")
    @Lazy
    OllamaChatModel ollamaChatModel;

    public static final String TEST_DATA = """
            金庸（1924年3月10日—2018年10月30日），本名查良镛，浙江省海宁市人，祖籍江西省婺源县浙源乡凤山村 [10] [146-147]，1948年移居香港。
            当代武侠小说作家、新闻学家、企业家、政治评论家、社会活动家，被誉为“香港四大才子”之一，与古龙、梁羽生、温瑞安并称为“中国武侠小说四大宗师”。 [1-4]
            1944年，考入重庆中央政治大学外交系。1946年秋，进入上海《大公报》任国际电讯翻译。1948年，毕业于上海东吴大学法学院，并被调往《大公报》香港分社 [5]。
            1952年调入《新晚报》编辑副刊，并写出《绝代佳人》《兰花花》等电影剧本。1959年，金庸等人于香港创办《明报》 [6]。
            1985年起，历任香港特别行政区基本法起草委员会委员、政治体制小组负责人之一，基本法咨询委员会执行委员会委员，以及香港特别行政区筹备委员会委员。
            1994年，受聘北京大学名誉教授 [7]。
            2000年，获得大紫荆勋章。2007年，出任香港中文大学文学院荣誉教授 [5]。
            2009年9月，被聘为中国作协第七届全国委员会名誉副主席 [8]；同年荣获2008影响世界华人终身成就奖 [9]。2010年，获得剑桥大学哲学博士学位 [2]。
            "2018年10月30日，在香港逝世，享年94岁。
            """;


    @GetMapping(value = "/extract", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> extract() {
        try {
            Prompt prompt = PromptTemplate.from("请从以下生平介绍中，提取出该人物的基本信息，以json格式输出，参考格式：{{json_output_sample}} \n{{test_data}}")
                    .apply(Map.of("json_output_sample", "{\"name\":\"张三\",\"age\":10,\"birthDay\":\"2000-01-01\",\"isAlive\":false,\"deathDate\":\"2040-02-01\",\"degree\":\"本科\"}",
                            "test_data", TEST_DATA));
            String text = ollamaChatModel.chat(prompt.toUserMessage()).aiMessage().text();
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            log.error("extract", e);
            return ResponseEntity.ok("{\"error\":\"extract error: " + e.getMessage() + "\"}");
        }
    }

    record Person(String name, int age, Date birthDay, boolean isAlive, Date deathDate, String degree) {
    }

    interface PersonExtractor {
        @SystemMessage("""
                你的任务是从生平介绍中，提取出该人的主要信息：
                name[姓名],age[年龄], birthDay[出生日期], isAlive[是否健在], deathDate[死亡日期(如果已逝世)], degree[最高学历]
                """)
        Person extractPerson(String biography);
    }

    @GetMapping(value = "/extract2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> extract2() {
        try {
            PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, ollamaChatModel);
            Person person = personExtractor.extractPerson(TEST_DATA);
            return ResponseEntity.ok(person);
        } catch (Exception e) {
            log.error("extract2", e);
            return ResponseEntity.ok(new Person("", -1, null, false, null, ""));
        }
    }
}
