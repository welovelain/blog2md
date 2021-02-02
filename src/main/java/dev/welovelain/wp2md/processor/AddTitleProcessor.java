package dev.welovelain.wp2md.processor;

import dev.welovelain.wp2md.domain.Post;

public class AddTitleProcessor extends MdBodyProcessor {

    @Override
    protected String processHere(Post post, String mdContent) {
        return "#" + post.getTitle() + "\r\n\r\n";
    }
}
