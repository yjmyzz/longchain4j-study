package com.cnblogs.yjmyzz.langchain4j.study.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author junmingyang
 */
public interface ChineseTeacher {

    @SystemMessage("你是一名小学语文老师")
    @UserMessage("请用中文回答我的问题：{{it}}")
    String chat(String query);

//    @SystemMessage("你是一名小学语文老师")
//    @UserMessage("请用中文回答我的问题：{{query}}")
//    String chat(String query);

//    @SystemMessage("你是一名小学语文老师")
//    @UserMessage("请用中文回答我的问题：{{abc}}")
//    String chat(@V("abc") String query);
}
