package dev.welovelain.wp2md;

import dev.welovelain.wp2md.domain.BlogProcessor;
import dev.welovelain.wp2md.domain.PostSupplier;
import dev.welovelain.wp2md.domain.pipe.*;
import dev.welovelain.wp2md.infrastructure.DbPostSupplier;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Application {

    private static final String WORDPRESS_JDBC_URL = System.getenv("WORDPRESS_JDBC_URL");
    private static final String MD_DIRECTORY = System.getenv("MD_DIRECTORY");
    private static final String IMAGE404_PATH = System.getenv("IMAGE404_PATH");

    public static void main(String[] args) throws Exception {
        BlogProcessor blogProcessor = mainPostProcessor(
                dbPostSupplier(),
                getMdFileProcessorsChain()
        );
        blogProcessor.run();
    }

    private static PostSupplier dbPostSupplier() throws SQLException {
        Connection connection = DriverManager.getConnection(WORDPRESS_JDBC_URL);
        return new DbPostSupplier(connection);
    }

    private static AbstractMdFilePipe getMdFileProcessorsChain() {
        var p1 = new DateToFileNameMdFilePipe(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"), ".md");
        var p2 = new HtmlToMarkdownMdFilePipe(new CopyDown());
        var p3 = new FrontMatterMdFilePipe();
        var p4 = new ClearUrlImageLinksMdFilePipe();
        var p5 = new ImageMdFilePipe(MD_DIRECTORY, true, IMAGE404_PATH);
        var p6 = new DiskWritingMdFilePipe(MD_DIRECTORY);

        p1.next = p2;
        p1.next.next = p3;
        p1.next.next.next = p4;
        p1.next.next.next.next = p5;
        p1.next.next.next.next.next = p6;

        return p1;
    }

    private static BlogProcessor mainPostProcessor(
            PostSupplier supplier,
            AbstractMdFilePipe mdFileProcessorChain
    ) {
        return new BlogProcessor(
                supplier,
                mdFileProcessorChain
        );
    }
}
