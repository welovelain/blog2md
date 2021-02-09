package dev.welovelain.blog2md.domain.pipe;

import dev.welovelain.blog2md.domain.MdFile;
import dev.welovelain.blog2md.domain.Post;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ClearUrlImageLinksMdFilePipe extends AbstractMdFilePipe {

    private static final String URL_IMAGE_REGEX = "\\[!\\[(?<alttext>[^]\\[]*)\\[?[^]\\[]*]?[^]\\[]*]\\((?<url>[^\\s]+?)(?:\\s+([\"'])(?<title>.*?)\\4)?\\)";
    private static final Pattern URL_IMAGE_PATTERN = Pattern.compile(URL_IMAGE_REGEX);

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        Matcher m = URL_IMAGE_PATTERN.matcher(file.content);

        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            String quoteReplacement = String.format("![%s](%s)", m.group("alttext"), m.group("url"));
            m.appendReplacement(builder, Matcher.quoteReplacement(quoteReplacement));
        }
        m.appendTail(builder);

        return file.withContent(builder.toString());
    }
}
