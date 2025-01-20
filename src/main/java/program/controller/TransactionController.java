package program.controller;

import program.model.User;
import program.service.BudgetService;
import program.service.WalletService;

import java.util.List;
import java.util.Scanner;

public class TransactionController {
    private final WalletService walletService;
    private final BudgetService budgetService;
    private final User user;
    private final Scanner scanner;

    public TransactionController(WalletService walletService, BudgetService budgetService, User user, Scanner scanner) {
        this.walletService = walletService;
        this.budgetService = budgetService;
        this.user = user;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("Транзакции:");
        while (true) {
            System.out.println("1. Ввести доход");
            System.out.println("2. Ввести расход");
            System.out.println("3. Список транзакций");
            System.out.println("4. Возврат");

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> addTransaction(true);
                    case "2" -> addTransaction(false);
                    case "3" -> listTransactions();
                    case "4" -> {
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

    private void addTransaction(boolean isIncome) {
        try {
            System.out.print("Введите название кошелька: ");
            String walletName = scanner.nextLine();
            System.out.print("Введите сумму: ");
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("Введите категорию: ");
            String categoryName = scanner.nextLine();

            walletService.addTransaction(user, walletName, amount, categoryName, isIncome);

            List<String> warnings = budgetService.checkBudgetLimits(user);
            if (!warnings.isEmpty()) {
                System.out.println("Предупреждения:");
                warnings.forEach(System.out::println);
            }

            String expenseWarning = walletService.checkExpenseExceedsIncome(user);
            if (!expenseWarning.isEmpty()) {
                System.out.println(expenseWarning);
            }

            System.out.println("Транзакция успешно добавлена.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Введите корректное число для суммы.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении транзакции: " + e.getMessage());
        }
    }

    private void listTransactions() {
        try {
            System.out.print("Введите название кошелька: ");
            String walletName = scanner.nextLine();

            walletService.listTransactions(user, walletName);
        } catch (Exception e) {
            System.out.println("Ошибка при отображении транзакций: " + e.getMessage());
        }
    }
}