package program.controller;

import program.model.User;
import program.service.UserService;
import program.service.WalletService;
import program.util.DataValidator;

import java.util.Scanner;

public class WalletController {
    private final WalletService walletService;
    private final UserService userService;
    private final User user;
    private final Scanner scanner;

    public WalletController(WalletService walletService, UserService userService, User user, Scanner scanner) {
        this.walletService = walletService;
        this.userService = userService;
        this.user = user;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("Управление кошельком:");
        while (true) {
            System.out.println("1. Создать кошелёк");
            System.out.println("2. Подсчитать доходы и расходы");
            System.out.println("3. Статистика по кошельку и бюджету");
            System.out.println("4. Перевести средства");
            System.out.println("5. Возврат");

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> newWallet();
                    case "2" -> calculateFinances();
                    case "3" -> displayBudgetData();
                    case "4" -> transferFunds();
                    case "5" -> {
                        System.out.println("Выход в главное меню.");
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void newWallet() {
        try {
            System.out.print("Введите название кошелька: ");
            String walletName = scanner.nextLine();
            System.out.print("Введите начальный баланс: ");
            double initialBalance = Double.parseDouble(scanner.nextLine());

            walletService.addWallet(user, walletName, initialBalance);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Введите корректное число для баланса.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении кошелька: " + e.getMessage());
        }
    }

    private void calculateFinances() {
        try {
            walletService.calculateFinances(user);
        } catch (Exception e) {
            System.out.println("Ошибка при подсчёте финансов: " + e.getMessage());
        }
    }

    private void displayBudgetData() {
        try {
            walletService.displayBudgetData(user);
        } catch (Exception e) {
            System.out.println("Ошибка при отображении данных по бюджету: " + e.getMessage());
        }
    }

    private void transferFunds() {
        try {
            System.out.print("Введите название вашего кошелька: ");
            String senderWallet = scanner.nextLine();
            System.out.print("Введите логин получателя: ");
            String receiverUsername = scanner.nextLine();
            System.out.print("Введите название кошелька получателя: ");
            String receiverWallet = scanner.nextLine();
            System.out.print("Введите сумму перевода: ");
            String amountInput = scanner.nextLine();

            if (!DataValidator.isNumeric(amountInput) || !DataValidator.isPositiveNumber(amountInput)) {
                System.out.println("Ошибка: Введите положительное число для суммы.");
                return;
            }
            double amount = Double.parseDouble(amountInput);

            User receiverUser = userService.findUserByUsername(receiverUsername);
            if (receiverUser == null) {
                System.out.println("Ошибка: Пользователь с логином \"" + receiverUsername + "\" не найден.");
                return;
            }

            walletService.transferFunds(user, senderWallet, receiverUser, receiverWallet, amount);
        } catch (Exception e) {
            System.out.println("Ошибка при переводе средств: " + e.getMessage());
        }
    }
}