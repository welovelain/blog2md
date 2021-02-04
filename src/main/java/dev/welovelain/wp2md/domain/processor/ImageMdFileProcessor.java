package dev.welovelain.wp2md.domain.processor;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private static final String IMAGE_DIR = "images";
    private static final String EXTENSION = "jpg";

    private final AtomicInteger imageCounter = new AtomicInteger();

    private final String blogDirectory;
    private final boolean overwriteFiles;
    private final String relativeErrorImagePath;

    @Override
    protected MdFile processHere(MdFile file, Post post) {

        String content = file.content;
        Matcher m = IMAGE_PATTERN.matcher(content);

        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String url = m.group("url");
            String altText = m.group("alttext");

            String filename = imageCounter.incrementAndGet() + "." + EXTENSION;
            String newImageLink = IMAGE_DIR + "/" + filename;

            try {
                downloadFile(url, filename);
                String quoteReplacement = String.format("![%s](%s)", altText, newImageLink);
                m.appendReplacement(sb, quoteReplacement);
            } catch (IOException e) {
                log.warn("Error while processing. PostId: {}, Title: {}, url: {}, filename: {}. Replacing with default image",
                        post.id, post.title, url, filename);
                String quoteReplacement = String.format("![%s](%s)", altText, relativeErrorImagePath);
                m.appendReplacement(sb, quoteReplacement);
            }
        }

        m.appendTail(sb);
        return file.withContent(sb.toString());
    }

    private void downloadFile(String url, String filename) throws IOException {
        String path = blogDirectory + "/" + IMAGE_DIR + "/" + filename;
        log.debug("Downloading image: {}, filename: {}", url, filename);

        try (InputStream in = new URL(url).openStream()) {
            if (overwriteFiles) {
                Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                return;
            }

            try {
                Files.copy(in, Paths.get(path));
            } catch (FileAlreadyExistsException e) {
                log.warn("Didn't download image: " + e.getMessage());
            }
        }
    }
}
