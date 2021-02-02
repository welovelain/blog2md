package dev.welovelain.wp2md.converter;

import dev.welovelain.wp2md.domain.Post;

public class PostFileNameConverter {

    public String mapFileName(Post post) {
        return post.getPostDate() + "";
    }
}
