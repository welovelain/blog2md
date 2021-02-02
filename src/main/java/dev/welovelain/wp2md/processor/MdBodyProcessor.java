package dev.welovelain.wp2md.processor;

import dev.welovelain.wp2md.domain.Post;

public abstract class MdBodyProcessor {

    private MdBodyProcessor next;

    public final String process(Post post, String mdContent) {
        mdContent = processHere(post, mdContent);
        return (next != null)
                ? next.process(post, mdContent)
                : mdContent;
    }

    protected abstract String processHere(Post post, String mdContent);
}
