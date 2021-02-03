package dev.welovelain.wp2md.application;

import dev.welovelain.wp2md.domain.PostSupplier;
import dev.welovelain.wp2md.infrastructure.DbPostSupplier;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class Application {

    private static final String WORDPRESS_JDBC_URL = System.getenv("WORDPRESS_JDBC_URL");

    public static void main(String[] args) throws Exception {
        PostSupplier supplier = dbPostSupplier();
    }

    private static PostSupplier dbPostSupplier() throws SQLException {
        Connection connection = DriverManager.getConnection(WORDPRESS_JDBC_URL);
        return new DbPostSupplier(connection);
    }
}
