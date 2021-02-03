package dev.welovelain.wp2md.domain;

import dev.welovelain.wp2md.domain.processor.MdFileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MainPostProcessor {

    private final PostSupplier postSupplier;
    private final MdFileProcessor mdFileProcessorChain;

    public void run() {
        List<Post> posts = postSupplier.get();
        log.info("Received {} posts", posts.size());

        for (var post: posts) {
            MdFile mdFile = new MdFile(null, post.getHtmlContent());
            MdFile mdFileProcessed = mdFileProcessorChain.process(mdFile, post);
        }

    }




}
