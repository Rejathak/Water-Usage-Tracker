private double getTotalConsumption() {
    double totalConsumption = 0;
    String query = "SELECT SUM(consumption_amount) as total_consumption FROM Water_Consumption";

    try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            totalConsumption = rs.getDouble("total_consumption");
        }
    } catch (SQLException e) {
        System.out.println("Failed to fetch total consumption: " + e.getMessage());
    }
    return totalConsumption;
}