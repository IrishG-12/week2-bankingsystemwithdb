package com.banking.service;

import com.banking.BankingSystem;
import com.banking.db.DatabaseConnection;
import com.banking.exception.*;
import com.banking.model.Account;
import com.banking.model.CheckingAccount;
import com.banking.model.SavingsAccount;
import com.banking.util.QueryConstant;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static com.banking.util.TransactionLogger.saveTransaction;


public class AccountService extends DatabaseConnection implements QueryConstant {

    private final BankingSystem bankingSystem;

    public AccountService(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    private static final String SAVINGS_INTEREST_RATE = "2.50%";


    //SAVE CREATED ACCOUNT TO DB
    public static void createAccount(String accountNumber, String accountType, BigDecimal balance) {
        try {

            stmt = con.prepareStatement(CREATE_ACCOUNT);

            stmt.setString(1, accountNumber);
            stmt.setString(2, accountType);
            stmt.setBigDecimal(3, balance);

            stmt.executeUpdate();

//trial
            if ("Savings".equalsIgnoreCase(accountType)) {
                new SavingsAccount(accountNumber, balance);
            } else if ("Checking".equalsIgnoreCase(accountType)) {
                new CheckingAccount(accountNumber, balance);
            } else {
                System.err.println("Unknown account type: " + accountType);
            }

        } catch (SQLException e) {
            System.err.println("Could not save account: " + e.getMessage());
        }

    }

    //VIEW SPECIFIC ACCOUNT FROM DB
    public static String viewAccountInDatabase(String accountNumber) {


        try {
            stmt = con.prepareStatement(FIND_ACCOUNT);
            stmt.setString(1, accountNumber);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String accId = rs.getString("account_id");
                BigDecimal balance = rs.getBigDecimal("balance");
                String type = rs.getString("account_type");

                balance = Objects.requireNonNullElse(balance, BigDecimal.ZERO);

                if ("SAVINGS".equalsIgnoreCase(type)) {
                    return String.format("Savings Account[ Account Number: %s | Balance: %.2f | Interest Rate: %s]",
                            accId, balance, SAVINGS_INTEREST_RATE);
                } else if ("CHECKING".equalsIgnoreCase(type)) {
                    return String.format("Checking Account[ Account Number: %s | Balance: %.2f | Transaction: %s]",
                            accId, balance, countTransactionsForAccount(accountNumber));//
                } else {
                    throw new IllegalArgumentException("Unknown account type: " + type);
                }
            } else {
                throw new AccountNotFoundException(accountNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching account: " + e.getMessage(), e);
        }
    }

    //GET ACCOUNT NUMBER IN DB IF IT EXIST
    public static String getAccountNumber(String accountId) throws AccountNotFoundException {

        try {
            stmt = con.prepareStatement(SELECT_ACCOUNTNUMBER);
            stmt.setString(1, accountId);

            try {
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("account_id");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while checking account: " + e.getMessage(), e);
        }
        throw new AccountNotFoundException(accountId);
    }

    //UPDATE BALANCE IN DB AFTER DEPOSIT
    public static void deposit(String accountNumber, BigDecimal amount) {


        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        try {
            stmt = con.prepareStatement(UPDATE_DEPOSIT);
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //UPDATE BALANCE IN DB AFTER WITHDRAW
    public static void withdraw(String accountNumber, BigDecimal amount) throws SQLException {

        BigDecimal currentBalance = getAccountBalance(accountNumber);

        if (currentBalance == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found or has a null balance.");
        }
        if (amount.compareTo(currentBalance) > 0) {
            throw new InsufficientFundsException(accountNumber, amount, currentBalance);
        }

        try {
            stmt = con.prepareStatement(UPDATE_WITHDRAW);
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //SHOW TRANSACTION OF SPECIFIC ACCOUNT
    public static void showAllTransactionsFromDatabase(String accountNumber) {

        try {

            stmt = con.prepareStatement(SHOW_TRANSACTION);
            stmt.setString(1, accountNumber);
            rs = stmt.executeQuery();

            System.out.println("\n=== Transaction History ===");
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-25s %-15s %-10s%n", "Transaction Date", "Account ID", "Amount");
            System.out.println("-------------------------------------------------------");


            while (rs.next()) {

                Date transactionDate = rs.getDate("transaction_date");
                String accountId = rs.getString("account_id");
                BigDecimal amount = rs.getBigDecimal("amount");

                System.out.printf("%-25s %-15s %-10.2f%n", transactionDate, accountId, amount);
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }
    }

    //GET BALANCE OF SPECIFIC ACCOUNT FROM DB
    public static BigDecimal getAccountBalance(String accountNumber) throws SQLException {

        try {
            stmt = con.prepareStatement(SELECT_BALANCE);
            stmt.setString(1, accountNumber);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return BigDecimal.ZERO;
    }


    //TRANSFER THEN UPDATE BALANCE OF BOTH ACCOUNT
    public static void transferFunds(String fromAcc, String toAcc, BigDecimal amount) throws SQLException {

        if (amount == null) {
            throw new IllegalArgumentException("Transfer amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (fromAcc.equals(toAcc)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }

        try {
            con.setAutoCommit(false);

            withdraw(fromAcc, amount);
            saveTransaction(fromAcc, amount.negate());

            deposit(toAcc, amount);
            saveTransaction(toAcc, amount);

            con.commit();
            System.out.println("Transfer successful: $" + amount + " from " + fromAcc + " to " + toAcc);

        } catch (SQLException | AccountNotFoundException | InsufficientFundsException e) {
            con.rollback();
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void transfer(String fromAcc, String toAcc, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException {
        Account from = bankingSystem.findAccount(fromAcc);
        Account to = bankingSystem.findAccount(toAcc);

        try {
            from.withdraw(amount);
            to.deposit(amount);
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BankingException("Transfer failed", e);
        }
    }

    public static int countTransactionsForAccount(String accountNumber) throws SQLException {

        stmt = con.prepareStatement(SHOW_TRANSACTION);
        stmt.setString(1, accountNumber);

        rs = stmt.executeQuery();

        int count = 0;
        while (rs.next()) {
            count++;
        }
        return count;
    }

    public static void deleteAccount(String accountNumber){

        try {
            stmt = con.prepareStatement(DELETE_ACCOUNT_TRANSACTION);
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
            stmt.close();

            stmt = con.prepareStatement(DELETE_ACCOUNT);
            stmt.setString(1, accountNumber);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("No account found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}

//    private static void findAccount(String number) {
//        // Assertions for invariants
//        assert number != null && !number.isEmpty() :
//                "Account number cannot be null or empty";
//    }
//


