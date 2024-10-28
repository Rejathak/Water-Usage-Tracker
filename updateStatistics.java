private void updateStatistics() {
    double averageConsumption = getAverageConsumption();
    double totalConsumption = getTotalConsumption();
    double averageCost = getAverageCost();
    double totalCost = getTotalCost();

    averageConsumptionLabel.setText(String.format("Average Consumption: %.2f liters", averageConsumption));
    totalConsumptionLabel.setText(String.format("Total Consumption: %.2f liters", totalConsumption));
    averageCostLabel.setText(String.format("Average Cost: $%.2f", averageCost));
    totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
}