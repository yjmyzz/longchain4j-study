package com.cnblogs.yjmyzz.langchain4j.study.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * @author junmingyang
 */
public interface ChineseStreamTeacher {
    @SystemMessage("你是一名小学语文老师")
    @UserMessage("请用中文回答我的问题：{{it}}")
    TokenStream chat(String query);
}
