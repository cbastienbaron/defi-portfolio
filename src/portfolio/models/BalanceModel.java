package portfolio.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BalanceModel {
    private final StringProperty balanceNameProperty = new SimpleStringProperty("");
    private final DoubleProperty balanceValueProperty = new SimpleDoubleProperty(0.0);
}
