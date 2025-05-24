package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Util.logger;
    public static void main(String[] args) {

        UserDao userDao = new UserDaoHibernateImpl();

        UserService userService = new UserServiceImpl(userDao);

        try {
            userService.createUsersTable();
            logger.info("Таблица users создана\n");

            saveUserWithLog(userService, "Ivan", "Ivanov", (byte) 28);
            saveUserWithLog(userService, "Maria", "Petrova", (byte) 32);
            saveUserWithLog(userService, "Alex", "Sidorov", (byte) 25);
            saveUserWithLog(userService, "Olga", "Smirnova", (byte) 30);

            userService.getAllUsers().forEach(System.out::println);
            logger.info("\nСписок всех пользователей:");

            userService.cleanUsersTable();
            logger.info("\nТаблица users очищена");

            userService.dropUsersTable();
            logger.info("Таблица users удалена");

        } catch (Exception e) {
            logger.warning(e.getMessage());
            System.err.println("Произошла ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void saveUserWithLog(UserService service, String name, String lastName, byte age) {
        service.saveUser(name, lastName, age);
        logger.info("User с именем '%s %s' добавлен в базу данных\n" + name + lastName);
    }
}
