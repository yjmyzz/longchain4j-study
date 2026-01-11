package com.cnblogs.yjmyzz.langchain4j.study.service;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author junmingyang
 */
public interface PoemExtractor {

    @UserMessage("请从以下内容中提取出诗歌内容：{{query}}")
    Poem extract(@V("query") String query);

}
