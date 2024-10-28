private void displayData() {
    dataList = FXCollections.observableArrayList();
    String query = "SELECT * FROM Water_Consumption";
    try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            WaterConsumption wc = new WaterConsumption(
                    rs.getInt("consumption_id"),  // Get consumption_id from the database
                    rs.getInt("household_id"),
                    rs.getDate("consumption_date").toString(),
                    rs.getDouble("consumption_amount")
            );
            dataList.add(wc);
        }
        tableView.setItems(dataList);
    } catch (SQLException e) {
        System.out.println("Failed to fetch data: " + e.getMessage());
    }
}