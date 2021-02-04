package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
public class DateToFileNameMdFileProcessor extends AbstractMdFileProcessor {

    private final DateTimeFormatter dateTimeFormatter;
    private final String extension;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
//        log.debug("Activated for post {}", post.id);
        String name = dateTimeFormatter.format(post.postDate) + extension;
        return file.withFileName(name);
    }
}
