package dev.welovelain.blog2md.domain.pipe;

import dev.welovelain.blog2md.domain.MdFile;
import dev.welovelain.blog2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [![alt_text](image_url)](link_url)
 * ![alt_text](image_url)
 * <p>
 * First, this processor clears all links to just images.
 * Second, downloads all images in the necessary directory and replaces urls with local files.
 */
@Slf4j
@RequiredArgsConstructor
public class ImageMdFilePipe extends AbstractMdFilePipe {

    private static final String IMAGE_REGEX = "!\\[(?<alttext>[^]\\[]*)\\[?[^]\\[]*]?[^]\\[]*]\\((?<url>[^\\s]+?)(?:\\s+([\"'])(?<title>.*?)\\4)?\\)";
    private static final Pattern IMAGE_PATTERN = Pattern.compile(IMAGE_REGEX);
    private static final String IMAGE_DIR = "images";
    private static final String EXTENSION = "jpg";

    private final AtomicInteger imageCounter = new AtomicInteger();
    private final String blogDirectory;
    private final String relativeErrorImagePath;
    private final Duration downloadTimeout;

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
        log.info("Downloading image: {}, filename: {}", url, filename);

        URL urly = new URL(url);
        URL temp = null;

        int redirectCount = 0;
        while (urly != temp) {
            if (redirectCount++ > 3) {
                throw new IOException("Too many redirects");
            }
            temp = urly;
            urly = checkForRedirect(urly);
        }

        var conn = urly.openConnection();
        conn.setReadTimeout((int)(downloadTimeout.get(ChronoUnit.SECONDS) * 1000));

        try (var is = conn.getInputStream();
             var readable = Channels.newChannel(is);
             var os = new FileOutputStream(path);
             var fc = os.getChannel()) {
            fc.transferFrom(readable, 0, Long.MAX_VALUE);
        }

    }

    private URL checkForRedirect(URL url) throws IOException {
        HttpURLConnection huc = (HttpURLConnection)url.openConnection();
        huc.setConnectTimeout((int)(downloadTimeout.get(ChronoUnit.SECONDS) * 1000));

        int statusCode = huc.getResponseCode(); //get response code
        if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                || statusCode == HttpURLConnection.HTTP_MOVED_PERM){ // if file is moved, then pick new URL
            return new URL(huc.getHeaderField("Location"));
        }

        return url;
    }
}
