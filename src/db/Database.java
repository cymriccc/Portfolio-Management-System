package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3307/student_portfolio";
            String user = "root";
            String password = "studentportfolio";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}