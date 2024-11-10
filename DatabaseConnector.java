public class DatabaseConnector {
    public static Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/water_usage"; 
            String user = ""; 
            String password = ""; 
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }
}
