package dev.welovelain.blog2md;

import dev.welovelain.blog2md.domain.BlogProcessor;
import dev.welovelain.blog2md.domain.Post;
import dev.welovelain.blog2md.domain.pipe.*;
import dev.welovelain.blog2md.domain.postsupplier.BloggerXmlSupplier;
import dev.welovelain.blog2md.domain.postsupplier.WordpressDbPostSupplier;
import io.github.furstenheim.CopyDown;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

public class Application {

    private static final String WORDPRESS_JDBC_URL = System.getenv("WORDPRESS_JDBC_URL");
    private static final String MD_DIRECTORY = System.getenv("MD_DIRECTORY");
    private static final String IMAGE404_PATH = System.getenv("IMAGE404_PATH");
    private static final Duration IMAGE_DOWNLOAD_TIMEOUT = Duration.ofSeconds(5);
    private static final String BLOGGER_XML_LOCATION = System.getenv("BLOGGER_XML_LOCATION");

    public static void main(String[] args) throws Exception {
//        var postSupplier = wordpressDBPostSupplier(); // process wordpress from DB
        var postSupplier = bloggerXmlPostSupplier(); // process blogger from xml

        new BlogProcessor(
                postSupplier,
                getMdFileProcessorsChain()
        ).run();
    }

    private static Supplier<List<Post>> bloggerXmlPostSupplier() {
        return new BloggerXmlSupplier(BLOGGER_XML_LOCATION);
    }

    private static Supplier<List<Post>> wordpressDBPostSupplier() throws SQLException {
        Connection connection = DriverManager.getConnection(WORDPRESS_JDBC_URL);
        return new WordpressDbPostSupplier(connection);
    }

    private static AbstractMdFilePipe getMdFileProcessorsChain() {
        var p1 = new DateToFileNameMdFilePipe(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"), ".md");
        var p2 = new HtmlToMarkdownMdFilePipe(new CopyDown());
        var p3 = new FrontMatterMdFilePipe();
        var p4 = new ClearUrlImageLinksMdFilePipe();
        var p5 = new ImageMdFilePipe(MD_DIRECTORY, IMAGE404_PATH, IMAGE_DOWNLOAD_TIMEOUT);
        var p6 = new DiskWritingMdFilePipe(MD_DIRECTORY);

        p1.next = p2;
        p1.next.next = p3;
        p1.next.next.next = p4;
        p1.next.next.next.next = p5;
        p1.next.next.next.next.next = p6;

        return p1;
    }

}
