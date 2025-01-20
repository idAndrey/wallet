package program.app;

import program.controller.BudgetController;
import program.controller.TransactionController;
import program.controller.WalletController;
import program.model.User;
import program.repository.CategoryRepository;
import program.repository.UserRepository;
import program.repository.WalletRepository;
import program.service.BudgetService;
import program.service.UserService;
import program.service.WalletService;

import java.util.Scanner;

public class WalletApp {

    public static void startApp() {
        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepository();
        WalletRepository walletRepository = new WalletRepository();
        CategoryRepository categoryRepository = new CategoryRepository();

        UserService userService = new UserService(userRepository, walletRepository, categoryRepository);
        WalletService walletService = new WalletService(walletRepository, categoryRepository);
        BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);

        //UserController userController = new UserController(userService, scanner);


        mainMenu(scanner, userService, walletService, budgetService);
    }

    private static void mainMenu(Scanner scanner, UserService userService,
                                 WalletService walletService, BudgetService budgetService) {
        while (true) {
            System.out.println("Главное меню:");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("3. Выход");

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> newUser(scanner, userService);
                    case "2" -> {
                        User currentUser = login(scanner, userService);
                        if (currentUser != null) {
                            manageUserSession(scanner, currentUser, walletService, budgetService, userService);
                        } else {
                            System.out.println("Вход не выполнен. Проверьте логин и пароль.");
                        }
                    }
                    case "3" -> {
                        System.out.println("Работа завершена.");
                        return;
                    }
                    default -> System.out.println("Неверная команда.");
                }
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private static User login(Scanner scanner, UserService userService) {
        System.out.print("Ваш логин: ");
        String username = scanner.nextLine();
        System.out.print("Ваш пароль: ");
        String password = scanner.nextLine();

        if (userService.loginUser(username, password)) {
            return userService.getCurrentUser();
        }
        return null;
    }

    private static void newUser(Scanner scanner, UserService userService) {
        try {
            System.out.print("Установите логин (от 4 до 20-ти символов): ");
            String username = scanner.nextLine();
            System.out.print("Установите пароль (не менее 6-ти символов): ");
            String password = scanner.nextLine();

            userService.registerUser(username, password);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void manageUserSession(Scanner scanner, User currentUser, WalletService walletService,
                                          BudgetService budgetService, UserService userService) {
        WalletController walletController = new WalletController(walletService, userService, currentUser, scanner);
        BudgetController budgetController = new BudgetController(budgetService, currentUser, scanner);
        TransactionController transactionController = new TransactionController(walletService, budgetService, currentUser, scanner);

        while (true) {
            System.out.println("Управление финансами:");
            System.out.println("1. Кошелёк");
            System.out.println("2. Категории и бюджет");
            System.out.println("3. Транзакции");
            System.out.println("4. Возврат");

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> walletController.start();
                    case "2" -> budgetController.start();
                    case "3" -> transactionController.start();
                    case "4" -> {
                        System.out.println("Выход из аккаунта...\n");
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }
}