package org.example.interfazzcrudjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante"; // Cambia el nombre de tu base de datos
    private static final String USER = "root";  // Cambia si usas otro usuario
    private static final String PASSWORD = "";  // Cambia si usas una contraseña

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}