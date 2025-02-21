package com.banking;

import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientFundsException;
import com.banking.model.AccountType;
import com.banking.service.AccountService;
import com.banking.util.AccountNumberGenerator;
import com.banking.util.TransactionLogger;
import static com.banking.util.TransactionLogger.saveTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Dashboard extends AccountService {

    public Dashboard(BankingSystem bankingSystem) {
        super(bankingSystem);
    }

    public static void bankingMenu(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {
        boolean exit = false;

        do {
            System.out.println("\n=== Banking System Menu ===");
            System.out.println("[1] Create Account");
            System.out.println("[2] View Account");
            System.out.println("[3] Deposit");
            System.out.println("[4] Withdraw");
            System.out.println("[5] Transfer");
            System.out.println("[6] View Transactions");
            System.out.println("[7] Delete Account (additional)");
            System.out.println("[8] Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createAccount(sc, bs, transactionLogger);
                    break;
                case "2":
                    viewAccount(sc, bs, transactionLogger);
                    break;
                case "3":
                    depositMenu(sc, bs, transactionLogger);
                    break;
                case "4":
                    withdrawMenu(sc, bs, transactionLogger);
                    break;
                case "5":
                    transfer(sc, bs, transactionLogger);
                    break;
                case "6":
                    viewTransactions(sc, bs, transactionLogger);
                    break;
                case "7":
                    deleteAccountMenu(sc);
                    break;
                case "8":
                    System.out.println("Exiting Banking System. Thank you!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number between 1-7.");
            }
        } while (!exit);
        System.exit(0);
    }

    public static void createAccount(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {


        System.out.println("\nChoose Account Type: ");
        System.out.println("[1] Savings ");
        System.out.println("[2] Checking ");
        System.out.println("[3] Back ");
        System.out.print("\nEnter your choice: ");

        String choice = sc.nextLine().trim();
        AccountType accountType;

        switch (choice) {
            case "1":
                String savAN = AccountNumberGenerator.generateSavingsAccountNumber();
                accountType = AccountType.SAVINGS;

                System.out.println("\nSavings");
                System.out.println("-----------------------");
                System.out.println("Account Number: " + savAN);
                System.out.print("Enter Initial Deposit: ");

                BigDecimal sdeposit = getValidDeposit(sc);

                createAccount(savAN, accountType.name(), sdeposit);

                System.out.println("\nNew Savings Account Created! \nAccount Number: " + savAN + "\nInitial Deposit: $" + sdeposit);
                bankingMenu(sc, bs, transactionLogger);
                break;

            case "2":
                String chkAN = AccountNumberGenerator.generateCheckingAccountNumber();
                accountType = AccountType.CHECKING;

                System.out.println("\nChecking");
                System.out.println("-----------------------");
                System.out.println("Account Number: " + chkAN);
                System.out.print("Enter Initial Deposit: ");

                BigDecimal cdeposit = getValidDeposit(sc);

                createAccount(chkAN, accountType.name(), cdeposit);
                System.out.println("\nNew Checking Account Created! \nAccount Number: " + chkAN + "\nInitial Deposit: $" + cdeposit);
                bankingMenu(sc, bs, transactionLogger);
                break;
            case "3":
                bankingMenu(sc, bs, transactionLogger);
                break;
            default:
                System.out.println("Invalid choice! Please enter a number between 1-3.");
                createAccount(sc, bs, transactionLogger);
        }
    }


    public static void viewAccount(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {

        System.out.print("\nTo View Account\nEnter Account Number: ");

        String accountNumber = sc.nextLine().toUpperCase();

        try {

            String validAccount = getAccountNumber(accountNumber);

            String details = viewAccountInDatabase(validAccount);
            System.out.println(details);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
            bankingMenu(sc, bs, transactionLogger);

        }
    }



    public static void depositMenu(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {
        System.out.print("\nEnter Account Number: ");
        String accountNumber = getValidAccount(sc);

        if (accountNumber.toLowerCase().contains("exit")) {
            bankingMenu(sc, bs, transactionLogger);
            return;
        }

        try {

            System.out.println("\nAccount Number: " + accountNumber);
            System.out.print("Enter Deposit Amount: ");

            BigDecimal depositAmount = getValidDeposit(sc);

            deposit(accountNumber, depositAmount);
            saveTransaction(accountNumber, depositAmount);
            System.out.println("Deposit successful! New balance: $" + getAccountBalance(accountNumber));

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }

    }


    public static void withdrawMenu(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {

        System.out.print("\nEnter Account Number: ");
        String accountNumber = getValidAccount(sc);

        if (accountNumber.toLowerCase().contains("exit")) {
            bankingMenu(sc, bs, transactionLogger);
            return;
        }

        BigDecimal withdrawAmount;
        BigDecimal currentBalance;

        while (true) {
            currentBalance = getAccountBalance(accountNumber);
            System.out.println("\nAccount Number: " + accountNumber);

            if (currentBalance == null) {
                System.out.println("Account balance is null.");
            } else if (currentBalance.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("Your balance is $0.00. No further withdrawals can be made.");
                break;
            }

            System.out.print("Enter Withdraw Amount: ");

            withdrawAmount = getValidDeposit(sc);

            try {
                if (withdrawAmount.compareTo(currentBalance) > 0) {
                    throw new InsufficientFundsException(accountNumber, withdrawAmount, currentBalance);
                } else {
                    withdraw(accountNumber, withdrawAmount);
                    saveTransaction(accountNumber, withdrawAmount.negate());
                    System.out.println("Withdraw successful! New balance: $" + getAccountBalance(accountNumber));
                    break;
                }
            } catch (InsufficientFundsException e) {
                System.out.println("Please enter a valid amount less than or equal to your current balance.");
            }
        }
    }


    public static void viewTransactions(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) {
        System.out.print("\nEnter Account Number to View Transaction: ");
        String accountNumber = sc.nextLine().trim().toUpperCase();
        showAllTransactionsFromDatabase(accountNumber);
    }

    public static void transfer(Scanner sc, BankingSystem bs, TransactionLogger transactionLogger) throws SQLException {

            System.out.println("To Transfer Enter Account Number");
            System.out.print("From Account: ");

            String validFrom = getValidAccount(sc);
            if (validFrom.toLowerCase().contains("exit")) {
                bankingMenu(sc, bs, transactionLogger);
                return;
            }


            System.out.print("To Account: ");
            String validTo = getValidAccount(sc);

            if (validTo.toLowerCase().contains("exit")) {
                bankingMenu(sc, bs, transactionLogger);
                return;
            }


            if (validFrom.equals(validTo)) {
                System.out.println("You can't transfer to the same account. Starting over...\n");
            }

            System.out.print("Enter Amount: ");
            BigDecimal amount = getValidDeposit(sc);

            try {
                transferFunds(validFrom, validTo, amount);
            } catch (SQLException | RuntimeException e) {
                System.out.println(e.getMessage());
            }

    }


    //HELPER METHODS
    public static BigDecimal getValidDeposit(Scanner sc) {
        while (true) {
            try {
                BigDecimal deposit = sc.nextBigDecimal();
                sc.nextLine();

                if (deposit.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.print("Amount must be positive.\nPlease enter a valid amount: ");
                    continue;
                }
                return deposit;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. \nPlease enter a valid amount: ");
                sc.nextLine();
            }
        }
    }


    public static String getValidAccount(Scanner sc) {
        while (true) {
            String accountNumber = sc.nextLine().toUpperCase();

            if (accountNumber.equalsIgnoreCase("exit")) {
                return "EXIT";  // Signal to return to main menu
            }

            try {
                return getAccountNumber(accountNumber); // Return valid account
            } catch (AccountNotFoundException e) {
                System.out.print("\n" + e.getMessage() + ". \nPlease enter a Valid Account Number Or Type 'EXIT' to go back: ");

            }
        }
    }

    //additional method
    public static void deleteAccountMenu(Scanner sc) {
        System.out.print("\nEnter Account Number you want to Delete: ");

        String accountNum = getValidAccount(sc);

        try {
                      deleteAccount(accountNum);

        } catch (AccountNotFoundException e) {
          throw new AccountNotFoundException(accountNum);
        }
    }


    }
