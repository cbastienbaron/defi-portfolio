package portfolio.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


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
    public StringProperty selectedPlotType = new SimpleStringProperty();
    public StringProperty selectedIntervall = new SimpleStringProperty();
    public ObjectProperty<LocalDate> dateFrom = new SimpleObjectProperty();
    public ObjectProperty<LocalDate> dateTo = new SimpleObjectProperty();
    public ObjectProperty<JSONObject> translationList = new SimpleObjectProperty();


    //Combo box filling
    public String[] cryptoCurrencies = new String[]{"BTC-DFI", "ETH-DFI", "USDT-DFI", "LTC-DFI", "DOGE-DFI"};
    public String[] plotCurrency = new String[]{"Coin", "Fiat"};

    //All relevant paths and files
    public String strCookiePath = System.getenv("APPDATA") + "\\DeFi Blockchain\\.cookie";
    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";
    public String[] languages = new String[]{"English","Deutsch"};
    public String[] currencies = new String[]{"EUR", "USD", "CHF"};
    public String[] decSeperators = new String[]{",", "."};
    public String[] csvSeperators = new String[]{",", ";"};
    public String pathSettingsFile = System.getenv("APPDATA") + "\\defi-portfolio\\settings.csv";
    public Logger logger = Logger.getLogger("Logger");

    private SettingsController() throws IOException {
        FileHandler fh;
        // This block configure the logger with handler and formatter
        fh = new FileHandler(strPathAppData+"\\log.txt");
        this.logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        this.loadSettings();
        updateLanguage();
    }

    public void updateLanguage(){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        String fileName = System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/translations/";
        switch (selectedLanguage.getValue()) {
            case "English":
                fileName += "en.json";
                break;
            case "Deutsch":
                fileName += "de.json";
                break;
            default:
                break;
        }
        try (FileReader reader = new FileReader(fileName)) {
            Object obj = jsonParser.parse(reader);
            this.translationList.setValue((JSONObject) obj);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
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
                this.selectedPlotCurrency.setValue(data[5]);
                this.dateFrom.setValue(LocalDate.parse(data[6]));
            }
        }
    }

    public void saveSettings() {

        FileWriter csvWriter;
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
            csvWriter.append(this.selectedPlotCurrency.getValue());
            csvWriter.append(",");
            csvWriter.append(this.dateFrom.getValue().toString());
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            this.logger.warning("Exception occured: "+e.toString());
        }
    }
}