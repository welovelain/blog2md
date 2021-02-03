package dev.welovelain.wp2md;

import dev.welovelain.wp2md.domain.MainPostProcessor;
import dev.welovelain.wp2md.domain.PostSupplier;
import dev.welovelain.wp2md.domain.processor.AbstractMdFileProcessor;
import dev.welovelain.wp2md.domain.processor.DateToFileNameMdFileProcessor;
import dev.welovelain.wp2md.domain.processor.FrontMatterMdFileProcessor;
import dev.welovelain.wp2md.domain.processor.HtmlToMarkdownMdFileProcessor;
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
        var p = new DateToFileNameMdFileProcessor(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"), ".md");
        var p2 = new HtmlToMarkdownMdFileProcessor(new CopyDown());
        var p3 = new FrontMatterMdFileProcessor();

        p.next = p2;
        p.next.next = p3;

        return p;
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
