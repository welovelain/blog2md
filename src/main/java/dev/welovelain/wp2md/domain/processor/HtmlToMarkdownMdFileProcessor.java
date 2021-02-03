package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import io.github.furstenheim.CopyDown;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HtmlToMarkdownMdFileProcessor extends AbstractMdFileProcessor {

    private final CopyDown copyDown;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        return file.withContent(copyDown.convert(file.content));
    }
}
