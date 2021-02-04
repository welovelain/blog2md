package dev.welovelain.wp2md.domain.pipe;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;

public abstract class AbstractMdFilePipe {

    public AbstractMdFilePipe next;

    public final MdFile process(MdFile file, Post post) {
        MdFile processed = processHere(file, post);
        return (next != null)
                ? next.process(processed, post)
                : processed;
    }

    protected abstract MdFile processHere(MdFile file, Post post);
}
