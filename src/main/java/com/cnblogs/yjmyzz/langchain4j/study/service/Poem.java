package com.cnblogs.yjmyzz.langchain4j.study.service;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author junmingyang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Poem {

    @Description("标题")
    private String title;

    @Description("作者")
    private String author;

    @Description("内容")
    private String content;
}
