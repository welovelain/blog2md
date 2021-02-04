package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.List;

/*
Example of yml:
---
title: Hello World
date: 2013/7/13 20:46:25
updated: 2013/7/13 20:46:25
tags:
- Injury
- Fight
- Shocking
---
 */
@Slf4j
public class FrontMatterMdFileProcessor extends AbstractMdFileProcessor {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    protected MdFile processHere(MdFile file, Post post) {
//        log.debug("Activated for post {}", post.id);
        String title = post.title;
        String date = dateTimeFormatter.format(post.postDate);
        String updated = dateTimeFormatter.format(post.modifiedDate);
        String tags = formatTags(post.tags);

        String yamlFrontMatter =
                """
                        ---
                        title: %s                                        
                        date: %s
                        updated: %s
                        tags: %s
                        ---
                        """.formatted(title, date, updated, tags);

        String content = yamlFrontMatter + file.content;
        return file.withContent(content);
    }

    private String formatTags(List<String> tags) {
        StringBuilder builder = new StringBuilder();
        tags.forEach(tag -> builder.append("\r\n- ").append(tag));
        return builder.toString();
    }
}
