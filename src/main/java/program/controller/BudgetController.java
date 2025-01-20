package program.controller;

import program.model.User;
import program.service.BudgetService;

import java.util.Scanner;

public class BudgetController {
    private final BudgetService budgetService;
    private final User user;
    private final Scanner scanner;

    public BudgetController(BudgetService budgetService, User user, Scanner scanner) {
        this.budgetService = budgetService;
        this.user = user;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("Категории и бюджет:");
        while (true) {
            System.out.println("1. Добавить категорию");
            System.out.println("2. Просмотреть список категорий");
            System.out.println("3. Подсчитать состояние бюджета по категориям");
            System.out.println("4. Возврат");

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> addCategory();
                    case "2" -> listCategories();
                    case "3" -> calculateBudgetState();
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

    private void addCategory() {
        try {
            System.out.print("Введите название категории: ");
            String categoryName = scanner.nextLine();
            System.out.print("Введите лимит бюджета (от 0 до 100_000_000): ");
            double budgetLimit = Double.parseDouble(scanner.nextLine());

            budgetService.addCategory(user, categoryName, budgetLimit);
            System.out.println("Категория \"" + categoryName + "\" успешно добавлена.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Введите корректное число для лимита бюджета.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении категории: " + e.getMessage());
        }
    }

    private void listCategories() {
        try {
            budgetService.listCategories(user);
        } catch (Exception e) {
            System.out.println("Ошибка при выводе списка категорий: " + e.getMessage());
        }
    }

    private void calculateBudgetState() {
        try {
            budgetService.calculateBudgetState(user);
        } catch (Exception e) {
            System.out.println("Ошибка при подсчёте состояния бюджета: " + e.getMessage());
        }
    }
}
