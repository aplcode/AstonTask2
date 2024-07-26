package ru.artemlychko.rest.util;

import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.exception.RepositoryException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class InitSqlScheme {
    private static final String SCHEME = "sql/schema.sql";
    private static String schemeSql;

    static {
        loadInitSQL();
    }

    private InitSqlScheme() {
    }

    public static void initSqlScheme(ConnectionManager connectionManager) {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(schemeSql);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private static void loadInitSQL() {
        try (InputStream inFile = InitSqlScheme.class.getClassLoader().getResourceAsStream(SCHEME)) {
            assert inFile != null;
            schemeSql = new String(inFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

}
