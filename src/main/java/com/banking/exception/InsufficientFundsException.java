package com.banking.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankingException {

    public InsufficientFundsException(String accountNumber, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Insufficient funds in account %s: requested %.2f, available %.2f",
                          accountNumber, requestedAmount, availableBalance));
    }

}
