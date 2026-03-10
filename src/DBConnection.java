import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/service_app";
    private static final String USER = "root"; // your MySQL username
    private static final String PASSWORD = "root123"; // your MySQL password

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection Failed!");
        }
        return conn;
    }

    // Test the connection
    public static void main(String[] args) {
        getConnection();
    }
}