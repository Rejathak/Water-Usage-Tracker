private double getAverageConsumption() {
    double totalConsumption = 0;
    String query = "SELECT AVG(consumption_amount) as avg_consumption FROM Water_Consumption";

    try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            totalConsumption = rs.getDouble("avg_consumption");
        }
    } catch (SQLException e) {
        System.out.println("Failed to fetch average consumption: " + e.getMessage());
    }
    return totalConsumption;
}