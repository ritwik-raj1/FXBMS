package com.ritwik.fxbms.Models;

import java.sql.*;

public class Conn {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fxbms";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "nest";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
