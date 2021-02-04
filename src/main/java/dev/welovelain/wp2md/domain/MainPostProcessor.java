package dev.welovelain.wp2md.domain;

import dev.welovelain.wp2md.domain.pipe.AbstractMdFilePipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MainPostProcessor {

    private final PostSupplier postSupplier;
    private final AbstractMdFilePipe mdFileProcessorChain;

    public void run() {
        List<Post> posts = postSupplier.get();
        log.info("Received {} posts", posts.size());
        posts.sort((o1, o2) -> o1.getModifiedDate().compareTo(o2.modifiedDate));

        int counter = 0;
        for (var post : posts) {
            log.info("Processing {} of {}", ++counter, posts.size());
            MdFile mdFile = new MdFile(null, post.htmlContent);
            mdFileProcessorChain.process(mdFile, post);
        }

    }

}
