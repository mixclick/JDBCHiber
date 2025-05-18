package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {

        UserDao userDao = new UserDaoHibernateImpl();

        UserService userService = new UserServiceImpl(userDao);

        try {
            userService.createUsersTable();
            System.out.println("Таблица users создана\n");

            saveUserWithLog(userService, "Ivan", "Ivanov", (byte) 28);
            saveUserWithLog(userService, "Maria", "Petrova", (byte) 32);
            saveUserWithLog(userService, "Alex", "Sidorov", (byte) 25);
            saveUserWithLog(userService, "Olga", "Smirnova", (byte) 30);

            System.out.println("\nСписок всех пользователей:");
            userService.getAllUsers().forEach(System.out::println);

            userService.cleanUsersTable();
            System.out.println("\nТаблица users очищена");

            userService.dropUsersTable();
            System.out.println("Таблица users удалена");



        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void saveUserWithLog(UserService service, String name, String lastName, byte age) {
        service.saveUser(name, lastName, age);
        System.out.printf("User с именем '%s %s' добавлен в базу данных\n", name, lastName);
    }
}
