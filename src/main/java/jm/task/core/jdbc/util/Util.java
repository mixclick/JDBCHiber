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
import java.util.logging.Logger;

public class Util {

    private static final Properties PROPERTIES = new Properties();
    private static final Properties QUERIES = new Properties();

    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String DRIVER_KEY = "db.postgresDriver";

    public static final String CREATE = "sq.CreateUsersTable";
    public static final String DROP = "sq.DropUsersTable";
    public static final String GET = "sq.GetAllUsersTable";
    public static final String CLEAN = "sq.CleanUsersTable";

    public static final String INSERT = "jdbc.Insert";
    public static final String DELETE = "jdbc.RemoveUsersById";
    public static final String GETALLUSERS = "jdbc.GetAllUsers";

    static {
        loadProperties();
        loadDriver();
        loadSQL();
    }

    public static Logger logger = Logger.getLogger(Util.class.getName());

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    static void loadProperties() {
        try (var inputStream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                logger.warning("Could not find application.properties");
                throw new RuntimeException("Properties file not found");
            }
            PROPERTIES.load(inputStream);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            throw new RuntimeException("Ошибка данных соединения", e);
        }
    }

    public static String getSQL(String key) {
        return QUERIES.getProperty(key);
    }

    static void loadSQL() {
        try (var inputStream = Util.class.getClassLoader().getResourceAsStream("queries.sql")) {
            if (inputStream == null) {
                logger.warning("Queries file not found");
                throw new RuntimeException("Queries file not found");
            }
            QUERIES.load(inputStream);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            throw new RuntimeException("Ошибка данных соединения", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.warning(e.getMessage());
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
            logger.warning(e.getMessage());
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
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                logger.warning(e.getMessage());
                throw new RuntimeException("Hibernate initialization failed", e);
            }
        }
        return sessionFactory;
    }
}