package com.banking.util;

import com.banking.model.AccountType;

public class AccountNumberGenerator {

    private static int scounter = 1000; // Start account numbers from 1000
    private static int ccounter = 1000;
    public static String generateSavingsAccountNumber() {
        return AccountType.SAVINGS.getCode() + (scounter++);
    }

    public static String generateCheckingAccountNumber() {
        return AccountType.CHECKING.getCode() + (ccounter++);
    }
}
