package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.Setter;

public abstract class MdFileProcessor {

    @Setter
    private MdFileProcessor next;

    public final MdFile process(MdFile file, Post post) {
        MdFile processed = processHere(file, post);
        return (next != null)
                ? next.process(processed, post)
                : processed;
    }

    protected abstract MdFile processHere(MdFile file, Post post);
}
