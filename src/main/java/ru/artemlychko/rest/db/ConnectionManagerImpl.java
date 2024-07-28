package ru.artemlychko.rest.db;

import ru.artemlychko.rest.exception.DataBaseDriverLoaderException;
import ru.artemlychko.rest.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManagerImpl implements ConnectionManager {
    private static final String DRIVER_CLASS_KEY = "db.driver-class-name";
    private static final String URL_KEY = "db.url";
    private static final String TEST_URL_KEY = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static ConnectionManager instance;

    private ConnectionManagerImpl() {
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManagerImpl();
            loadDriver(PropertiesUtil.getProperties(DRIVER_CLASS_KEY));
        }
        return instance;
    }

    private static void loadDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new DataBaseDriverLoaderException("Database driver not loaded.");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    TEST_URL_KEY,
                    PropertiesUtil.getProperties(USERNAME_KEY),
                    PropertiesUtil.getProperties(PASSWORD_KEY)
            );
        } catch (Exception e) {
            connection = DriverManager.getConnection(
                    PropertiesUtil.getProperties(URL_KEY),
                    PropertiesUtil.getProperties(USERNAME_KEY),
                    PropertiesUtil.getProperties(PASSWORD_KEY)
            );
        }
        return connection;
    }
}
