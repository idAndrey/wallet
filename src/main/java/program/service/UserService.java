package program.service;

import program.model.Category;
import program.model.User;
import program.model.Wallet;
import program.repository.CategoryRepository;
import program.repository.UserRepository;
import program.repository.WalletRepository;
import program.util.DataValidator;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private User currentUser;

    public UserService(UserRepository userRepository, WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void registerUser(String username, String password) {
        try {
            validateUsername(username);
            validatePassword(password);

            List<User> users = userRepository.loadUsers();

            if (users.stream().anyMatch(user -> user.getUsername().equals(username))) {
                throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
            }

            users.add(new User(username, password));
            userRepository.saveUsers(users);

            System.out.println("Пользователь успешно зарегистрирован.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }


    public boolean loginUser(String username, String password) {
        try {
            validateUsername(username);
            validatePassword(password);

            User user = findUserByUsername(username);

            if (user == null) {
                System.out.println("Ошибка: Пользователь с логином '" + username + "' не найден.");
                return false;
            }

            if (user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Добро пожаловать, " + user.getUsername() + "!\n");
                return true;
            }

            System.out.println("Ошибка: Неверный пароль.");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void validateUsername(String username) {
        if (!DataValidator.isNonEmptyString(username) || !DataValidator.isStringLengthValid(username, 20)
                || !DataValidator.isValidLogin(username)) {
            throw new IllegalArgumentException("Некорректный логин.");
        }
    }

    private void validatePassword(String password) {
        if (!DataValidator.isNonEmptyString(password) || !DataValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Некорректный пароль.");
        }
    }
}