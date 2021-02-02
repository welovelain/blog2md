package dev.welovelain.wp2md;

import dev.welovelain.wp2md.converter.Html2mdConverter;
import dev.welovelain.wp2md.converter.PostFileNameConverter;
import dev.welovelain.wp2md.processor.MdBodyProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostProcessor {

    private final PostSupplier postSupplier;
    private final Html2mdConverter html2MdConverter;
    private final MdFileWriter mdFileWriter;
    private final PostFileNameConverter postFileNameConverter;

    private final MdBodyProcessor processorChain;

    public void run() {
        var posts = postSupplier.getPosts();
        for (var post: posts) {
            String mdContent = html2MdConverter.convert(post.getHtmlContent());
            mdContent = processorChain.process(post, mdContent);

            String mdFileName = postFileNameConverter.mapFileName(post);
            mdFileWriter.write(mdFileName, mdContent);
        }
    }

}
