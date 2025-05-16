package org.example.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {

            return DriverManager.getConnection("jdbc:mysql://localhost:3306/students", "root", "pappu1031");

    }
}
