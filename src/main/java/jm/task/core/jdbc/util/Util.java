package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {

    private static final Properties PROPERTIES = new Properties();

    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String DRIVER_KEY = "db.postgresDriver";

    static {
        loadProperties();
        loadDriver();
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    static void loadProperties() {
        try (var inputStream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Properties file not found");
            }
            PROPERTIES.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка данных соединения", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database драйвер не найден", e);
        }
    }

    public static Connection getConnection() {
        try {
            String url = getProperty(URL_KEY);
            String user = getProperty(USER_KEY);
            String password = getProperty(PASSWORD_KEY);
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Соединение прервано", e);
        }
    }
}