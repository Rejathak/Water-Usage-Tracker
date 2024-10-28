import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    
    // Method to establish and return a connection to the database
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/water_usage_tracker";
            String user = "root"; 
            String password = "Rakhack123"; 
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Main method to test the connection
    public static void main(String[] args) {
        // Call the connect method and check if the connection is successful
        Connection conn = connect();
        
        // If the connection is successful, the object won't be null
        if (conn != null) {
            System.out.println("Database connection successful.");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}
