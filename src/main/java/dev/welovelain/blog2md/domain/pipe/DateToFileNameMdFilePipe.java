package dev.welovelain.blog2md.domain.pipe;

import dev.welovelain.blog2md.domain.MdFile;
import dev.welovelain.blog2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
public class DateToFileNameMdFilePipe extends AbstractMdFilePipe {

    private final DateTimeFormatter dateTimeFormatter;
    private final String extension;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        String name = dateTimeFormatter.format(post.postDate) + extension;
        return file.withFileName(name);
    }
}
