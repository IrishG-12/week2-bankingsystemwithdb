package com.banking.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection con ;
    public static Statement state = null;
    public static ResultSet rs = null;
    public static PreparedStatement stmt = null;


    private static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        try (
                InputStream input = DatabaseConnection.class
                        .getClassLoader()
                        .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }

    //public method to be called in main
    public static void connect() {
        try {
                con = getConnection();

            if (con != null) {
                System.out.println("Connected to the database successfully!");
                createTable();
            }
        } catch (SQLException e) {
            System.out.println("Unsuccessful connecting to database. " + e.getMessage());
        }
    }

    //read table creation from sql file
    public static void createTable() {

        try {
            String schemaFilePath = "src/main/resources/schema.sql";
            String sql = new String(Files.readAllBytes(Paths.get(schemaFilePath)));

            state = con.createStatement();
            if (state == null) {
                throw new RuntimeException("Failed to create Statement.");
            }

            state.execute(sql);
           // System.out.println("Schema executed successfully!");

        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());

        }
    }

}
