package program.repository;

import program.model.Category;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository extends FileRepository {
    private static final String FILE_PATH = "data/categories/categories.json";

    public CategoryRepository() {
        ensureDirectoriesExist();
        ensureFileExists();
    }

    private void ensureDirectoriesExist() {
        File directory = new File("data/categories");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists() || file.length() == 0) {
                file.createNewFile();
                saveCategories(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла категорий: " + e.getMessage());
        }
    }

    public void saveCategories(List<Category> categories) {
        try {
            saveDataToFile(FILE_PATH, categories);
            System.out.println("Категории успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении категорий: " + e.getMessage());
        }
    }

    public List<Category> loadCategories() {
        try {
            return loadDataFromFile(FILE_PATH, Category.class);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке категорий: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Category> findCategoriesByUserId(String userId) {
        List<Category> allCategories = loadCategories();
        List<Category> userCategories = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getUserId().equals(userId)) {
                userCategories.add(category);
            }
        }
        return userCategories;
    }

    public Category findCategoryByName(String userId, String name) {
        List<Category> allCategories = loadCategories();
        for (Category category : allCategories) {
            if (category.getUserId().equals(userId) && category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }
}