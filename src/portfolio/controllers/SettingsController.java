package portfolio.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.time.LocalDate;


public class SettingsController {
    private static SettingsController OBJ = null;

    static {
        try {
            OBJ = new SettingsController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringProperty selectedLanguage = new SimpleStringProperty("English");
    public StringProperty selectedFiatCurrency = new SimpleStringProperty("EUR");
    public StringProperty selectedDecimal = new SimpleStringProperty(".");
    public StringProperty selectedSeperator = new SimpleStringProperty(",");

    public StringProperty selectedCoin = new SimpleStringProperty("BTC-DFI");
    public StringProperty selectedPlotCurrency = new SimpleStringProperty("Coin");
    public StringProperty selectedPlotType = new SimpleStringProperty("Individual");
    public StringProperty selectedIntervall = new SimpleStringProperty("Daily");
    public ObjectProperty<LocalDate> dateFrom = new SimpleObjectProperty();
    public ObjectProperty<LocalDate> dateTo = new SimpleObjectProperty();


    //Combo box filling
    public String[] cryptoCurrencies = new String[]{"BTC-DFI", "ETH-DFI", "USDT-DFI", "LTC-DFI", "DOGE-DFI"};
    public String[] intervall = new String[]{"Daily", "Weekly", "Monthly", "Yearly"};
    public String[] plotCurrency = new String[]{"Coin", "Fiat"};
    public String[] plotType = new String[]{"Individual", "Cumulated"};

    //All relevant paths and files
    public String strCookiePath = System.getenv("APPDATA") + "\\DeFi Blockchain\\.cookie";
    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";
    public String[] languages = new String[]{"English"};
    public String[] currencies = new String[]{"EUR", "USD", "CHF"};
    public String[] decSeperators = new String[]{",", "."};
    public String[] csvSeperators = new String[]{",", ";"};
    public String pathSettingsFile = System.getenv("APPDATA") + "\\defi-portfolio\\settings.csv";

    private SettingsController() throws IOException {
        this.loadSettings();
    }

    public static SettingsController getInstance() {
        return OBJ;
    }

    public void loadSettings() throws IOException {
        File f = new File(pathSettingsFile);
        if (f.exists() && !f.isDirectory()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(pathSettingsFile));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                this.selectedLanguage.setValue(data[0]);
                this.selectedFiatCurrency.setValue(data[1]);
                switch (data[2]) {
                    case "comma":
                        this.selectedDecimal.setValue(",");
                        break;
                    case "dot":
                        this.selectedDecimal.setValue(".");
                        break;
                }
                switch (data[3]) {
                    case "comma":
                        this.selectedSeperator.setValue(",");
                        break;
                    case "semicolon":
                        this.selectedSeperator.setValue(".");
                        break;
                }
                this.selectedCoin.setValue(data[4]);
                this.selectedIntervall.setValue(data[5]);
                this.selectedPlotType.setValue(data[6]);
                this.selectedPlotCurrency.setValue(data[7]);
                this.dateFrom.setValue(LocalDate.parse(data[8]));
            }
        }
    }

    public void saveSettings() {

        FileWriter csvWriter = null;
        try {
            csvWriter = new FileWriter(pathSettingsFile);

            csvWriter.append(this.selectedLanguage.getValue());
            csvWriter.append(",");
            csvWriter.append(this.selectedFiatCurrency.getValue());
            csvWriter.append(",");

            switch (this.selectedDecimal.getValue()) {
                case ".":
                    csvWriter.append("dot");
                    break;
                case ",":
                    csvWriter.append("comma");
                    break;
            }
            csvWriter.append(",");
            switch (this.selectedSeperator.getValue()) {
                case ".":
                    csvWriter.append("dot");

                    break;
                case ";":
                    csvWriter.append("semicolon");
                    break;
            }
            csvWriter.append(",");

            csvWriter.append(this.selectedCoin.getValue());
            csvWriter.append(",");
            csvWriter.append(this.selectedIntervall.getValue());
            csvWriter.append(",");
            csvWriter.append(this.selectedPlotType.getValue());
            csvWriter.append(",");
            csvWriter.append(this.selectedPlotCurrency.getValue());
            csvWriter.append(",");
            csvWriter.append(this.dateFrom.getValue().toString());
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}