package dev.welovelain.wp2md.domain;

import dev.welovelain.wp2md.domain.processor.AbstractMdFileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MainPostProcessor {

    private final PostSupplier postSupplier;
    private final AbstractMdFileProcessor mdFileProcessorChain;

    public void run() {
        List<Post> posts = postSupplier.get();
        log.info("Received {} posts", posts.size());

        posts.parallelStream()
                .forEach(post -> {
                    MdFile mdFile = new MdFile(null, post.htmlContent);
                    MdFile mdFileProcessed = mdFileProcessorChain.process(mdFile, post);
                });


    }


}
