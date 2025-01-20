package program.service;

import program.model.Category;
import program.model.Transaction;
import program.model.User;
import program.model.Wallet;
import program.repository.CategoryRepository;
import program.repository.WalletRepository;
import program.util.DataValidator;

import java.util.*;

public class BudgetService {
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;


    public BudgetService(WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(User user, String categoryName, double budgetLimit) {
        validateCategoryName(categoryName);
        validateBudgetLimit(budgetLimit);

        Category existingCategory = categoryRepository.findCategoryByName(user.getUsername(), categoryName);

        if (existingCategory != null) {
            throw new IllegalArgumentException("Категория с таким названием уже существует.");
        }

        Category newCategory = new Category(user.getUsername(), categoryName, budgetLimit);
        List<Category> categories = categoryRepository.loadCategories();
        categories.add(newCategory);
        categoryRepository.saveCategories(categories);

        System.out.println("Категория успешно создана.");
    }

    public void listCategories(User user) {
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getUsername());

        if (categories.isEmpty()) {
            System.out.println("Категории отсутствуют.");
            return;
        }

        System.out.println("Ваши категории:");
        for (Category category : categories) {
            System.out.println("- " + category.getName() +
                    (category.getBudgetLimit() > 0 ? " (Лимит: " + category.getBudgetLimit() + ")" : ""));
        }
    }

    public void calculateBudgetState(User user) {
        Map<String, Double> expensesByCategory = new HashMap<>();
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getUsername());

        List<Transaction> transactions = new ArrayList<>();
        for (Wallet wallet : walletRepository.loadWalletsByUser(user.getUsername())) {
            transactions.addAll(wallet.getTransactions());
        }

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            expensesByCategory.put(categoryName,
                    expensesByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
        }

        System.out.println("Состояние бюджета по категориям:");
        for (Category category : categories) {
            double expenses = Math.abs(expensesByCategory.getOrDefault(category.getName(), 0.0));
            double remainingBudget = category.getBudgetLimit() - expenses;

            System.out.println("- " + category.getName() +
                    ": Лимит: " + category.getBudgetLimit() +
                    ", Расходы: " + expenses +
                    ", Остаток: " + remainingBudget);
        }
    }

    private void validateCategoryName(String categoryName) {
        if (!DataValidator.isNonEmptyString(categoryName) ||
                !DataValidator.isStringLengthValid(categoryName, 30)) {
            throw new IllegalArgumentException("Некорректное название категории.");
        }
    }

    private void validateBudgetLimit(double budgetLimit) {
        if (!DataValidator.isNumberInRange(budgetLimit, 0, 100_000_000)) {
            throw new IllegalArgumentException("Некорректный лимит бюджета.");
        }
    }

    public List<String> checkBudgetLimits(User user) {
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getUsername());

        Map<String, Double> expensesByCategory = calculateExpensesByCategory(user);

        List<String> warnings = new ArrayList<>();
        for (Category category : categories) {
            double expenses = Math.abs(expensesByCategory.getOrDefault(category.getName(), 0.0));
            if (expenses > category.getBudgetLimit()) {
                warnings.add("Лимит превышен для категории: " + category.getName());
            }
        }
        return warnings;
    }

    private Map<String, Double> calculateExpensesByCategory(User user) {
        Map<String, Double> expensesByCategory = new HashMap<>();
        List<Transaction> transactions = new ArrayList<>();

        for (Wallet wallet : walletRepository.loadWalletsByUser(user.getUsername())) {
            transactions.addAll(wallet.getTransactions());
        }

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                String categoryName = transaction.getCategory().getName();
                expensesByCategory.put(categoryName,
                        expensesByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }
        return expensesByCategory;
    }
}