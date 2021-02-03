package dev.welovelain.wp2md.application;

import dev.welovelain.wp2md.domain.MainPostProcessor;
import dev.welovelain.wp2md.domain.PostSupplier;
import dev.welovelain.wp2md.domain.processor.DateToFileNameMdFileProcessor;
import dev.welovelain.wp2md.domain.processor.MdFileProcessor;
import dev.welovelain.wp2md.infrastructure.DbPostSupplier;
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

    private static MdFileProcessor getMdFileProcessorsChain() {
        // example: 20210203213437.md
        var processor1 = new DateToFileNameMdFileProcessor(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return processor1;
    }

    private static MainPostProcessor mainPostProcessor(
            PostSupplier supplier,
            MdFileProcessor mdFileProcessorChain
    ) {
        return new MainPostProcessor(
                supplier,
                mdFileProcessorChain
        );
    }
}
