package sample;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class SettingsController {

    public SettingsController(String strSettingsData) {
        this.strSettingsData = strSettingsData;
    }
    String strSettingsData;
    public StringProperty selectedCoin = new SimpleStringProperty("DFI");
    public StringProperty selectedFiatCurrency = new SimpleStringProperty("EUR");
    public StringProperty selectedPlotCurrency = new SimpleStringProperty("Crypto");
    public StringProperty cmbIntervall = new SimpleStringProperty("Daily");
    public StringProperty selectedDecimal = new SimpleStringProperty(".");
    public StringProperty selectedSeperator = new SimpleStringProperty(",");
    public ObjectProperty<LocalDate>  dateFrom = new SimpleObjectProperty();
    public ObjectProperty<LocalDate> dateTo= new SimpleObjectProperty();

}
