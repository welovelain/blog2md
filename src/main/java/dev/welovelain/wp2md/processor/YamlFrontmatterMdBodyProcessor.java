package dev.welovelain.wp2md.processor;

import dev.welovelain.wp2md.domain.Post;

public class YamlFrontmatterMdBodyProcessor extends MdBodyProcessor {

    @Override
    protected String processHere(Post post, String mdContent) {
        StringBuilder ymlHeaderBuilder = new StringBuilder("---")
                .append("\r\ntitle: ").append('"').append(post.getTitle()).append('"');

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            ymlHeaderBuilder.append("\r\nkeywords:");

            for (String tag: post.getTags()) {
                ymlHeaderBuilder.append("\r\n  - ").append(tag);
            }
        }

        return ymlHeaderBuilder.toString() + "\r\n" + mdContent;
    }
}
