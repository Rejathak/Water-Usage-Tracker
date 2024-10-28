private void deleteSelectedData() {
    WaterConsumption selected = tableView.getSelectionModel().getSelectedItem();
    if (selected != null) {
        String query = "DELETE FROM Water_Consumption WHERE consumption_id = ?"; // Use consumption_id for deletion
        try (Connection conn = DatabaseConnector.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, selected.getConsumptionId()); // Pass consumption_id for deletion
            pstmt.executeUpdate();
            System.out.println("Data deleted successfully.");
            displayData(); // Refresh data
            updateStatistics(); // Update averages and totals after deletion
        } catch (SQLException e) {
            System.out.println("Failed to delete data: " + e.getMessage());
        }
    } else {
        showAlert("Selection Error", "Please select a record to delete.");
    }
}