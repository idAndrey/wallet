package program.service;

import program.model.Category;
import program.model.Transaction;
import program.model.User;
import program.model.Wallet;
import program.repository.CategoryRepository;
import program.repository.WalletRepository;
import program.util.DataValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WalletService {
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public WalletService(WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addWallet(User user, String walletName, double initialBalance) {
        try {
            validateWalletName(walletName);
            validateBalance(initialBalance);

            Wallet newWallet = new Wallet(user.getUsername(), walletName, initialBalance);

            walletRepository.saveWallet(newWallet);
            System.out.println("Кошелёк успешно добавлен.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении кошелька: " + e.getMessage());
        }
    }

    public void transferFunds(User senderUser, String senderWallet, User receiverUser, String receiverWallet, double amount) {
        if (!DataValidator.isPositiveNumber(String.valueOf(amount))) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной.");
        }

        Wallet sender = null, receiver = null;
        List<Wallet> senderWallets = walletRepository.loadWalletsByUser(senderUser.getUsername());
        for (Wallet wallet : senderWallets) {
            if (wallet.getName().equals(senderWallet)) {
                sender = wallet;
                break;
            }
        }

        List<Wallet> receiverWallets = walletRepository.loadWalletsByUser(receiverUser.getUsername());
        for (Wallet wallet : receiverWallets) {
            if (wallet.getName().equals(receiverWallet)) {
                receiver = wallet;
                break;
            }
        }

        if (sender == null) {
            throw new IllegalArgumentException("Кошелек отправителя \"" + senderWallet + "\" не найден.");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("Кошелек получателя \"" + receiverWallet + "\" не найден.");
        }
        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на кошельке отправителя.");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        walletRepository.saveWallet(sender);
        walletRepository.saveWallet(receiver);

        System.out.println("Перевод успешно выполнен: " + amount + " из \"" + senderWallet + "\" в \"" + receiverWallet + "\".");
    }

    private void validateWalletName(String walletName) {
        if (!DataValidator.isNonEmptyString(walletName) || !DataValidator.isStringLengthValid(walletName, 50)) {
            throw new IllegalArgumentException("Некорректное название кошелька.");
        }
    }

    private void validateBalance(double balance) {
        if (!DataValidator.isPositiveNumber(String.valueOf(balance)) || !DataValidator.isNumberInRange(balance, 1, 100_000_000)) {
            throw new IllegalArgumentException("Некорректный баланс.");
        }
    }


    public void calculateFinances(User user) {
        double totalIncome = 0;
        double totalExpenses = 0;

        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        for (Wallet wallet : wallets) {
            for (Transaction transaction : wallet.getTransactions()) {
                if (transaction.getAmount() > 0) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpenses += transaction.getAmount();
                }
            }
        }

        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + Math.abs(totalExpenses));
    }

    public void displayBudgetData(User user) {
        CategoryRepository categoryRepository = new CategoryRepository();
        List<Category> userCategories = categoryRepository.findCategoriesByUserId(user.getUsername());
        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());

        for (Wallet wallet : wallets) {
            System.out.println("Кошелёк: " + wallet.getName());
            System.out.printf("Баланс: %.2f\n", wallet.getBalance());
            System.out.println("Транзакции:");

            for (Transaction transaction : wallet.getTransactions()) {
                String categoryName = transaction.getCategory().getName();
                String transactionCategory = userCategories.stream()
                        .anyMatch(c -> c.getName().equals(categoryName)) ? transaction.getCategory().getName() : "[Категория не найдена]";

                System.out.printf("  - Дата: %s, Сумма: %.2f, Категория: %s\n",
                        transaction.getDate(), transaction.getAmount(), transactionCategory);
            }

            System.out.println();
        }
    }


    public String checkExpenseExceedsIncome(User user) {
        double totalIncome = 0;
        double totalExpenses = 0;

        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        for (Wallet wallet : wallets) {
            for (Transaction transaction : wallet.getTransactions()) {
                if (transaction.getAmount() > 0) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpenses += transaction.getAmount();
                }
            }
        }

        if (Math.abs(totalExpenses) > totalIncome) {
            return "Предупреждение: Общие расходы превышают доходы!";
        }
        return "";
    }

    public void addTransaction(User user, String walletName, double amount, String categoryName, boolean isIncome) {
        try {
            List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());

            Wallet targetWallet = null;
            for (Wallet wallet : wallets) {
                if (wallet.getName().equals(walletName)) {
                    targetWallet = wallet;
                    break;
                }
            }

            if (targetWallet == null) {
                throw new IllegalArgumentException("Кошелёк с названием \"" + walletName + "\" не найден.");
            }

            CategoryRepository categoryRepository = new CategoryRepository();
            Category category = categoryRepository.findCategoryByName(user.getUsername(), categoryName);

            if (category == null) {
                throw new IllegalArgumentException("Категория с названием \"" + categoryName + "\" не найдена.");
            }

            double adjustedAmount = isIncome ? amount : -amount;
            Transaction transaction = new Transaction(adjustedAmount, category, LocalDate.now());
            targetWallet.addTransaction(transaction);

            walletRepository.saveWallet(targetWallet);
            System.out.println("Транзакция успешно добавлена.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении транзакции: " + e.getMessage());
        }
    }

    public void listTransactions(User user, String walletName) {
        try {
            List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
            for (Wallet wallet : wallets) {
                if (wallet.getName().equals(walletName)) {
                    System.out.println("Транзакции для кошелька \"" + walletName + "\":");
                    for (Transaction transaction : wallet.getTransactions()) {
                        System.out.printf("  - Дата: %s, Сумма: %.2f, Категория: %s, ID: %s\n",
                                transaction.getDate(),
                                transaction.getAmount(),
                                transaction.getCategory().getName(),
                                transaction.getId());
                    }
                    return;
                }
            }
            System.out.println("Кошелёк с названием \"" + walletName + "\" не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка при выводе списка транзакций: " + e.getMessage());
        }
    }
}
