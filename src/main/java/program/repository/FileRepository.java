package program.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class FileRepository {
    protected final ObjectMapper objectMapper;

    public FileRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    }

    protected <T> void saveDataToFile(String filePath, List<T> data) throws IOException {
        try {
            objectMapper.writeValue(new File(filePath), data);
            System.out.println("Данные успешно сохранены в " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных в " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    protected <T> List<T> loadDataFromFile(String filePath, Class<T> type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }
}