package dev.welovelain.blog2md.domain;

import dev.welovelain.blog2md.domain.pipe.AbstractMdFilePipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
public class BlogProcessor {

    private final Supplier<List<Post>> postSupplier;
    private final AbstractMdFilePipe mdFileProcessorChain;

    public void run() {
        List<Post> posts = postSupplier.get();
        log.info("Received {} posts", posts.size());
        posts.sort((o1, o2) -> o1.getModifiedDate().compareTo(o2.modifiedDate));

        int counter = 0;
        // Could use parallelStream for faster processing,
        // but I want file metadata to be in creation order correlating to last update
        for (var post : posts) {
            log.info("Processing {} of {}", ++counter, posts.size());
            MdFile mdFile = new MdFile(null, post.htmlContent);
            mdFileProcessorChain.process(mdFile, post);
        }

    }

}
