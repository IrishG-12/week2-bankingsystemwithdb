package com.banking.util;

public interface QueryConstant {

    String CREATE_ACCOUNT = "INSERT INTO accounts (account_id, account_type, balance) VALUES (?, ?, ?)";
    String FIND_ACCOUNT = "SELECT account_id, balance, account_type FROM accounts WHERE account_id = ?";
    String SHOW_TRANSACTION = "SELECT * FROM transactions WHERE ACCOUNT_ID = ? ORDER BY transaction_date DESC";
    String SAVE_TRANSACTION = "INSERT INTO transactions (account_id, amount, transaction_date) VALUES (?, ?, ?)";
    String UPDATE_DEPOSIT = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
    String UPDATE_WITHDRAW = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
    String SELECT_ACCOUNTNUMBER = "SELECT account_id FROM accounts WHERE account_id = ?";
    String SELECT_BALANCE = "SELECT balance FROM accounts WHERE account_id = ?";
    String DELETE_ACCOUNT = "DELETE FROM accounts WHERE account_id = ?";
    String DELETE_ACCOUNT_TRANSACTION = "DELETE FROM transactions WHERE account_id = ?";
}
