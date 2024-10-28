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