package dev.welovelain.wp2md.domain.pipe;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import io.github.furstenheim.CopyDown;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class HtmlToMarkdownMdFilePipe extends AbstractMdFilePipe {

    private final CopyDown copyDown;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        return file.withContent(copyDown.convert(file.content));
    }
}
