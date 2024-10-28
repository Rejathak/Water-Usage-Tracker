private void addData(int householdId, LocalDate date, double amount) throws SQLException {
    String query = "INSERT INTO Water_Consumption (household_id, consumption_date, consumption_amount) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseConnector.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, householdId);
        pstmt.setDate(2, Date.valueOf(date));
        pstmt.setDouble(3, amount);
        pstmt.executeUpdate();
        System.out.println("Data inserted successfully.");
        checkForSpike(amount);
    } catch (SQLException e) {
        System.out.println("Failed to insert data: " + e.getMessage());
        throw e;
    }
}