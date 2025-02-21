package com.banking.util;

import com.banking.db.DatabaseConnection;
import com.banking.exception.AccountNotFoundException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class TransactionLogger extends DatabaseConnection implements QueryConstant {

    private static final Path TRANSACTIONS_PATH = Path.of("transactions.txt");


    //SAVE TRANSACTION TO DB
    public static void saveTransaction(String accountNumber, BigDecimal amount) {

        try {

            stmt = con.prepareStatement(SAVE_TRANSACTION);

            stmt.setString(1, accountNumber);
            stmt.setBigDecimal(2, amount);
            stmt.setObject(3, LocalDateTime.now());
            stmt.executeUpdate();
           // System.out.println("Transaction saved successfully!");

        } catch (SQLException e) {
            System.out.println("Failed to save transaction.");
        }
    }


    //SAVE TRANS TO TXT FILE
    public void saveFileTransaction(String accountNumber, BigDecimal amount){
        try {
            // Create transaction record with timestamp
            String transaction = String.format("%s,%s,%.2f%n",
                    LocalDateTime.now(), accountNumber, amount);

            // Write to file using NIO - create if doesn't exist, append if exists
            Files.writeString(TRANSACTIONS_PATH, transaction,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("Could not save transaction: " + e.getMessage());
        }
    }

    public void showAllTransactions() {
        try {
            // Check if file exists
            if (!Files.exists(TRANSACTIONS_PATH)) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("\nTransaction History:");
            System.out.println("------------------");

            // Read all lines using NIO
            List<String> lines = Files.readAllLines(TRANSACTIONS_PATH);
            for (String line : lines) {
                String[] parts = line.split(",");
                // Format: timestamp, account, amount
                System.out.printf("Time: %s, Account: %s, Amount: $%s%n",
                        parts[0], parts[1], parts[2]);
            }

        } catch (IOException e) {
            System.err.println("Could not read transactions: " + e.getMessage());
        }
    }

    public void clearTransactions() {
        try {
            Files.deleteIfExists(TRANSACTIONS_PATH);
        } catch (IOException e) {
            System.err.println("Could not clear transactions: " + e.getMessage());
        }
    }
}
