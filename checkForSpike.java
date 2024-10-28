private void checkForSpike(double newConsumption) {
    double averageConsumption = getAverageConsumption();
    double spikeThreshold = averageConsumption * 1.3; // Set a 30% spike threshold

    if (newConsumption > spikeThreshold) {
        showAlert("High Water Usage Alert", "Water consumption is unusually high! New Consumption: " + newConsumption + " liters");
    }
}
