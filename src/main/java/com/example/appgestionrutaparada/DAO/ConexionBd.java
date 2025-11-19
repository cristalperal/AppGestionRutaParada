package com.example.appgestionrutaparada.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBd {
    private static final String URL = "jdbc:postgresql://localhost:5432/appgestionrutaparada";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}