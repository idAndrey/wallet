package program.repository;

import program.model.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalletRepository extends FileRepository {
    private static final String FILE_PATH = "data/wallets/wallets.json";


    public WalletRepository() {
        ensureDirectoriesExist();
        ensureFileExists();
    }


    private void ensureDirectoriesExist() {
        File directory = new File("data/wallets");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists() || file.length() == 0) {
                file.createNewFile();
                saveWallets(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла кошельков: " + e.getMessage());
        }
    }

    public void saveWallets(List<Wallet> wallets) {
        try {
            saveDataToFile(FILE_PATH, wallets);
            System.out.println("Данные кошельков успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении кошельков: " + e.getMessage());
        }
    }

    public List<Wallet> loadWallets() {
        try {
            List<Wallet> wallets = loadDataFromFile(FILE_PATH, Wallet.class);
            if (wallets == null) {
                wallets = new ArrayList<>();
            }
            return wallets;
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке кошельков: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Wallet> loadWalletsByUser(String userId) {
        List<Wallet> allWallets = loadWallets();
        List<Wallet> userWallets = new ArrayList<>();
        for (Wallet wallet : allWallets) {
            if (wallet.getUserId().equals(userId)) {
                userWallets.add(wallet);
            }
        }
        return userWallets;
    }

    public void saveWallet(Wallet wallet) {
        try {
            List<Wallet> wallets = loadWallets();

            boolean walletUpdated = false;

            for (int i = 0; i < wallets.size(); i++) {
                Wallet existingWallet = wallets.get(i);
                if (existingWallet.getUserId().equals(wallet.getUserId()) &&
                        existingWallet.getName().equals(wallet.getName())) {
                    wallets.set(i, wallet);
                    walletUpdated = true;
                    break;
                }
            }

            if (!walletUpdated) {
                wallets.add(wallet);
            }

            saveWallets(wallets);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении кошелька: " + e.getMessage());
        }
    }
}