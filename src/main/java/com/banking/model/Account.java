package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banking.db.DatabaseConnection;
import com.banking.exception.InsufficientFundsException;
import com.banking.service.AccountService;

public abstract class Account{
    // Private fields - data encapsulation
    private final String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    // Protected field - accessible by subclasses
    protected LocalDateTime lastTransaction;

    // Static field - shared across instances
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");

    // Public constructor
    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.lastTransaction = LocalDateTime.now();
    }
    public Account(String accountNumber, BigDecimal balance, AccountType accountType) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Abstract method
    public abstract void processMonthlyFees();

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(accountNumber, amount, balance);
        }

        balance = balance.subtract(amount);
        lastTransaction = LocalDateTime.now();

    }


    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
        lastTransaction = LocalDateTime.now();
    }

    // Protected method for subclasses
    protected abstract boolean canWithdraw(BigDecimal amount);

    public AccountType getAccountType() {return accountType;}
    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getLastTransaction() {
        return lastTransaction;
    }

    protected BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    // Override Object class methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;
        Account other = (Account) obj;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Account[number=%s, balance=%.2f]",
                           accountNumber, balance);
    }
}
