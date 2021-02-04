package dev.welovelain.wp2md;

import dev.welovelain.wp2md.domain.MainPostProcessor;
import dev.welovelain.wp2md.domain.PostSupplier;
import dev.welovelain.wp2md.domain.processor.*;
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
        MainPostProcessor mainPostProcessor = mainPostProcessor(
                dbPostSupplier(),
                getMdFileProcessorsChain()
        );
        mainPostProcessor.run();
    }

    private static PostSupplier dbPostSupplier() throws SQLException {
        Connection connection = DriverManager.getConnection(WORDPRESS_JDBC_URL);
        return new DbPostSupplier(connection);
    }

    private static AbstractMdFileProcessor getMdFileProcessorsChain() {
        // example: 20210203213437.md
        var p1 = new DateToFileNameMdFileProcessor(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"), ".md");
        var p2 = new HtmlToMarkdownMdFileProcessor(new CopyDown());
        var p3 = new FrontMatterMdFileProcessor();
        var p4 = new ClearUrlImageLinksMdFileProcessor();
        var p5 = new ImageMdFileProcessor(MD_DIRECTORY, true, IMAGE404_PATH);

        p1.next = p2;
        p1.next.next = p3;
        p1.next.next.next = p4;
        p1.next.next.next.next = p5;

        return p1;
    }

    private static MainPostProcessor mainPostProcessor(
            PostSupplier supplier,
            AbstractMdFileProcessor mdFileProcessorChain
    ) {
        return new MainPostProcessor(
                supplier,
                mdFileProcessorChain
        );
    }
}
