package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [![alt_text](image_url)](link_url)
 * ![alt_text](image_url)
 *
 * First, this processor clears all links to just images.
 * Second, downloads all images in the necessary directory and replaces urls with local files.
 */
@Slf4j
@RequiredArgsConstructor
public class ImageMdFileProcessor extends AbstractMdFileProcessor {

    private static final String IMAGE_REGEX = "!\\[(?<alttext>[^]\\[]*)\\[?[^]\\[]*]?[^]\\[]*]\\((?<url>[^\\s]+?)(?:\\s+([\"'])(?<title>.*?)\\4)?\\)";
    private static final Pattern IMAGE_PATTERN = Pattern.compile(IMAGE_REGEX);

    private static final String URL_IMAGE_REGEX = "\\[!\\[(?<alttext>[^]\\[]*)\\[?[^]\\[]*]?[^]\\[]*]\\((?<url>[^\\s]+?)(?:\\s+([\"'])(?<title>.*?)\\4)?\\)";
    private static final Pattern URL_IMAGE_PATTERN = Pattern.compile(URL_IMAGE_REGEX);

    private final AtomicInteger imageCounter = new AtomicInteger();

    @Override
    protected MdFile processHere(MdFile file, Post post) {

        String content = file.content;
        content = clearUrlLinks(content);
        Matcher m = IMAGE_PATTERN.matcher(content);

        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String url = m.group("url");
            String altText = m.group("alttext");

            log.info("Downloading image: {}", url);

            String[] split = url.split("\\.");
            var extension = split[split.length - 1];

            // todo - save image and create file link. If image not available, use non-available image
            // url should be absolute, for example: /home/username/images/0.jpg
            String newImageLink = "file:///" + imageCounter.incrementAndGet() + "." + extension;


            String quoteReplacement = String.format("![%s](%s)", altText, newImageLink);
            m.appendReplacement(sb, quoteReplacement);
        }

        m.appendTail(sb);
        return file.withContent(sb.toString());
    }

    private String clearUrlLinks(String content) {
        Matcher m = URL_IMAGE_PATTERN.matcher(content);

        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            String quoteReplacement = String.format("![%s](%s)", m.group("alttext"), m.group("url"));
            m.appendReplacement(builder, Matcher.quoteReplacement(quoteReplacement));
        }
        m.appendTail(builder);

        return builder.toString();
    }
}
