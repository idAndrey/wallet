package program.repository;

import program.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends FileRepository {
    private static final String FILE_PATH = "data/users/users.json";

    public UserRepository() {
        ensureDirectoriesExist();
        ensureFileExists();
    }

    private void ensureDirectoriesExist() {
        File directory = new File("data/users");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists() || file.length() == 0) {
                file.createNewFile();
                saveUsers(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла пользователей: " + e.getMessage());
        }
    }

    public void saveUsers(List<User> users) {
        try {
            saveDataToFile(FILE_PATH, users);
            System.out.println("Данные пользователей успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении пользователей: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        try {
            List<User> users = loadDataFromFile(FILE_PATH, User.class);
            if (users == null) {
                users = new ArrayList<>();
            }
            return users;
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке пользователей: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public User findUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}