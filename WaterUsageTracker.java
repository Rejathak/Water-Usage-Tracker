import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseConnector {
    public static Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/water_usage"; // Update with your database info
            String user = "root"; // Your MySQL username
            String password = "Rakhack123"; // Your MySQL password
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }
}

public class WaterUsageTracker extends Application {

    private TableView<WaterConsumption> tableView;
    private ObservableList<WaterConsumption> dataList;
    private double costPerLiter = 0.002; // Set your rate per liter

    // Labels to display averages and totals
    private Label averageConsumptionLabel;
    private Label averageCostLabel;
    private Label totalConsumptionLabel;
    private Label totalCostLabel;

    @Override
    public void start(Stage primaryStage) {
        // Input fields
        Label householdLabel = new Label("Household ID:");
        TextField householdField = new TextField();

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();

        Label amountLabel = new Label("Consumption Amount (liters):");
        TextField amountField = new TextField();

        // Button for adding data
        Button addButton = new Button("Add Data");
        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                addData(Integer.parseInt(householdField.getText()), datePicker.getValue(), amount);
                double cost = calculateCost(amount);
                showAlert("Data Inserted", "Consumption Amount: " + amount + " liters\nCost: $" + cost);
            } catch (Exception ex) {
                System.out.println("Failed to insert data: " + ex.getMessage());
            }
            displayData(); // Refresh data
            updateStatistics(); // Update averages and totals
        });

        // TableView to display data
        tableView = new TableView<>();
        TableColumn<WaterConsumption, Integer> householdIdCol = new TableColumn<>("Household ID");
        householdIdCol.setCellValueFactory(cellData -> cellData.getValue().householdIdProperty().asObject());

        TableColumn<WaterConsumption, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<WaterConsumption, Double> amountCol = new TableColumn<>("Consumption Amount (liters)");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        tableView.getColumns().addAll(householdIdCol, dateCol, amountCol);

        // Delete Button
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> {
            deleteSelectedData();
            updateStatistics(); // Update averages and totals after deletion
        });

        // Labels for statistics
        averageConsumptionLabel = new Label("Average Consumption: 0 liters");
        averageCostLabel = new Label("Average Cost: $0");
        totalConsumptionLabel = new Label("Total Consumption: 0 liters");
        totalCostLabel = new Label("Total Cost: $0");

        // Chart button
        Button chartButton = new Button("Show Consumption Chart");
        chartButton.setOnAction(e -> showConsumptionChart());

        // Layout with GridPane for input fields
        GridPane inputLayout = new GridPane();
        inputLayout.setVgap(10);
        inputLayout.setHgap(10);
        inputLayout.setPadding(new javafx.geometry.Insets(10));

        inputLayout.add(householdLabel, 0, 0);
        inputLayout.add(householdField, 1, 0);
        inputLayout.add(dateLabel, 0, 1);
        inputLayout.add(datePicker, 1, 1);
        inputLayout.add(amountLabel, 0, 2);
        inputLayout.add(amountField, 1, 2);
        inputLayout.add(addButton, 0, 3, 2, 1); // Span 2 columns

        // Main layout
        VBox layout = new VBox(10, inputLayout, tableView, deleteButton,
                averageConsumptionLabel, averageCostLabel, totalConsumptionLabel, totalCostLabel, chartButton);
        layout.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(layout, 600, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Water Usage Tracker");
        primaryStage.show();

        // Load initial data
        displayData();
        updateStatistics(); // Calculate initial averages and totals
    }

    // Function to add data to the database
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

    // Function to check for spikes in water consumption
    private void checkForSpike(double newConsumption) {
        double averageConsumption = getAverageConsumption();
        double spikeThreshold = averageConsumption * 1.3; // Set a 30% spike threshold

        if (newConsumption > spikeThreshold) {
            showAlert("High Water Usage Alert", "Water consumption is unusually high! New Consumption: " + newConsumption + " liters");
        }
    }

    // Function to get average consumption from the database
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

    // Function to get total consumption from the database
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

    // Function to calculate total cost from total consumption
    private double getTotalCost() {
        double totalConsumption = getTotalConsumption();
        return totalConsumption * costPerLiter;
    }

    // Function to calculate average cost
    // Function to calculate average cost per household (or entry)
private double getAverageCost() {
    double totalCost = getTotalCost();
    int totalEntries = getTotalEntries();
    return totalEntries > 0 ? totalCost / totalEntries : 0;
}

// Function to get the total number of entries (households)
private int getTotalEntries() {
    int totalEntries = 0;
    String query = "SELECT COUNT(*) as total_entries FROM Water_Consumption";
    
    try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            totalEntries = rs.getInt("total_entries");
        }
    } catch (SQLException e) {
        System.out.println("Failed to fetch total entries: " + e.getMessage());
    }
    return totalEntries;
}

    // Function to calculate cost based on consumption
    private double calculateCost(double consumptionAmount) {
        return consumptionAmount * costPerLiter;
    }

    // Function to display data in TableView
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

    // Function to delete selected data
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

    // Function to update statistics
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

    // Function to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Function to show consumption chart
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

class WaterConsumption {
    private final SimpleIntegerProperty consumptionId;
    private final SimpleIntegerProperty householdId;
    private final SimpleStringProperty date;
    private final SimpleDoubleProperty amount;

    public WaterConsumption(int consumptionId, int householdId, String date, double amount) {
        this.consumptionId = new SimpleIntegerProperty(consumptionId);
        this.householdId = new SimpleIntegerProperty(householdId);
        this.date = new SimpleStringProperty(date);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public int getConsumptionId() {
        return consumptionId.get();
    }

    public SimpleIntegerProperty householdIdProperty() {
        return householdId;
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }
}

public static void main(String[] args) {
    launch(args);
}
}

