package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class DateToFileNameMdFileProcessor extends AbstractMdFileProcessor {

    private static final String EXTENSION = ".md";

    private final DateTimeFormatter dateTimeFormatter;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        String name = dateTimeFormatter.format(post.getPostDate()) + EXTENSION;
        return new MdFile(name, post.getHtmlContent());
    }
}
