package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

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
    private static SessionFactory sessionFactory;

    public static SessionFactory getHibernateSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();
                settings.put(Environment.DRIVER, getProperty(DRIVER_KEY));
                settings.put(Environment.URL, getProperty(URL_KEY));
                settings.put(Environment.USER, getProperty(USER_KEY));
                settings.put(Environment.PASS, getProperty(PASSWORD_KEY));
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                // settings.put(Environment.HBM2DDL_AUTO, "create-drop");


                configuration.setProperties(settings);
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                throw new RuntimeException("Hibernate initialization failed", e);
            }
        }
        return sessionFactory;
    }
}