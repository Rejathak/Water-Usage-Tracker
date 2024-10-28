 private void showConsumptionChart() {
    Stage chartStage = new Stage();
    chartStage.setTitle("Water Consumption vs. Number of Data Entries");

    // Create axes for the chart
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Number of Data Entries");
    
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Consumption Amount (liters)");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Water Consumption vs. Number of Data Entries");

    String query = "SELECT consumption_amount FROM Water_Consumption";

    try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Water Consumption"); // Name of the series

        int entryCount = 0;
        while (rs.next()) {
            entryCount++;
            double consumptionAmount = rs.getDouble("consumption_amount");
            // Use entryCount as X value and consumptionAmount as Y value
            series.getData().add(new XYChart.Data<>(entryCount, consumptionAmount));
        }

        lineChart.getData().add(series); // Add series to the line chart
    } catch (SQLException e) {
        System.out.println("Failed to fetch chart data: " + e.getMessage());
    }

    VBox chartLayout = new VBox(lineChart);
    Scene chartScene = new Scene(chartLayout, 600, 400);

    chartStage.setScene(chartScene);
    chartStage.show();
}