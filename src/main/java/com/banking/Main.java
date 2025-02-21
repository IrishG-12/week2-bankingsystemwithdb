package com.banking;

import com.banking.model.*;
import com.banking.service.AccountService;
import com.banking.util.TransactionLogger;
import com.banking.exception.*;
import org.h2.tools.Server;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

import static com.banking.db.DatabaseConnection.connect;
import static com.banking.db.DatabaseConnection.createTable;

public class Main {
    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        BankingSystem bs = new BankingSystem();
        TransactionLogger logger = new TransactionLogger();


        //Connect to H2 Database
        connect();
        //run localhost
        Server.createWebServer("-web").start();

        Dashboard.bankingMenu(sc, bs, logger);


    }
}
