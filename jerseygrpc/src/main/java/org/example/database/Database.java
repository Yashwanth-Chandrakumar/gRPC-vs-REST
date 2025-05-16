package org.example.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class Database {
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/students", "root", "pappu1031");
    }
}
