package portfolio.controllers;

import javafx.beans.property.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class SettingsController {
    public String Version = "V1.3";

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
    public StringProperty selectedStyleMode = new SimpleStringProperty("Dark Mode");

    public StringProperty selectedCoin = new SimpleStringProperty("BTC-DFI");
    public StringProperty selectedPlotCurrency = new SimpleStringProperty("Coin");
    public StringProperty selectedPlotType = new SimpleStringProperty();
    public StringProperty selectedIntervall = new SimpleStringProperty();
    public ObjectProperty<LocalDate> dateFrom = new SimpleObjectProperty();
    public ObjectProperty<LocalDate> dateTo = new SimpleObjectProperty();
    public ObjectProperty<JSONObject> translationList = new SimpleObjectProperty();
    public String selectedIntervallInt = "Daily";
    public boolean showDisclaim = true;
    public boolean selectedLaunchDefid = false;

    //Combo box filling
    public String[] cryptoCurrencies = new String[]{"BTC-DFI", "ETH-DFI", "USDT-DFI", "LTC-DFI", "DOGE-DFI"};
    public String[] plotCurrency = new String[]{"Coin", "Fiat"};
    public String[] styleModes = new String[]{"Light Mode", "Dark Mode"};


    public String USER_HOME_PATH = System.getProperty("user.home");
    public String BINARY_FILE_NAME = getPlatform() == "win" ? "defid.exe" : "defid";
    public String BINARY_FILE_PATH = getPlatform() == "win" ?
            (System.getenv("LOCALAPPDATA") + "/Programs/defi-app/resources/binary/win/" + BINARY_FILE_NAME).replace("\\", "/") : //WIN PATH
            getPlatform() == "mac" ?
                    USER_HOME_PATH + "/../.." + "/Applications/defi-app.app/Contents/Resources/binary/mac/" + BINARY_FILE_NAME : //MAC PATH
                    getPlatform() == "linux" ?
                            System.getProperty("user.dir") + "/PortfolioDateien/" + BINARY_FILE_NAME : //Linux PATH
                            ""; //LINUX PATH;
    public String CONFIG_FILE_PATH = getPlatform() == "win" ?
            USER_HOME_PATH + "/.defi/defi.conf" : //WIN PATH
            getPlatform() == "mac" ? USER_HOME_PATH + "/Library/Application\\ Support/DeFi/defi.conf" : //MAC PATH
                    getPlatform() == "linux" ? USER_HOME_PATH + "/.defi/defi.conf" : //LINUX PATH
                            "";
    public String DEFI_PORTFOLIO_HOME = getPlatform() == "win" ?
            System.getenv("APPDATA") + "/defi-portfolio/" : //WIN PATH
            getPlatform() == "mac" ? USER_HOME_PATH + "/Library/Application\\ Support/defi-portfolio/" : //MAC PATH
                    getPlatform() == "linux" ? System.getProperty("user.dir") + "/PortfolioDateien/" : //LINUX PATH;
                            "";
    public String COOKIE_FILE_PATH = getPlatform() == "win" ?
            System.getenv("APPDATA") + "/DeFi Blockchain/.cookie" : //WIN PATH
            getPlatform() == "mac" ? USER_HOME_PATH + "/Library/Application\\ Support/DeFi/.cookie" : //MAC PATH
                    getPlatform() == "linux" ? USER_HOME_PATH + "/.defi/bla.cookie"  : //LINUX PATH;
                    ""; //LINUX PATH;

    public String SETTING_FILE_PATH = DEFI_PORTFOLIO_HOME + "settings.csv";
    //All relevant paths and files
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";
    public String[] languages = new String[]{"English", "Deutsch"};
    public String[] currencies = new String[]{"EUR", "USD", "CHF"};
    public String[] decSeperators = new String[]{".", ","};
    public String[] csvSeperators = new String[]{",", ";"};
    public Logger logger = Logger.getLogger("Logger");
    public String rpcauth;
    public String rpcuser;
    public String rpcpassword;
    public String rpcbind;
    public String rpcport;

    public boolean runTimer = true;
    public boolean debouncer = false;
    public String auth;

    public Timer timer = new Timer("Timer");

    public String lastExportPath = USER_HOME_PATH;

    private SettingsController() throws IOException {
        FileHandler fh;

        File directory = new File(DEFI_PORTFOLIO_HOME);
        if (!directory.exists()) {
            directory.mkdir();
        }

        fh = new FileHandler(DEFI_PORTFOLIO_HOME + "log.txt");
        this.logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        this.loadSettings();
        updateLanguage();
        getConfig();
    }

    public void updateLanguage() {

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

    public String getPlatform() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return "mac";
        } else if (OS.indexOf("win") >= 0) {
            return "win";
        } else if (OS.indexOf("nux") >= 0) {
            return "linux";
        } else {
            return "win";
        }
    }

    public void loadSettings() throws IOException {
        File f = new File(SETTING_FILE_PATH);
        if (f.exists() && !f.isDirectory()) {
            File configFile = new File(SETTING_FILE_PATH);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            }

            try {
                this.selectedLanguage.setValue(configProps.getProperty("SelectedLanguage"));
                this.selectedFiatCurrency.setValue(configProps.getProperty("SelectedFiatCurrency"));
                this.selectedDecimal.setValue(configProps.getProperty("SelectedDecimal"));
                this.selectedSeperator.setValue(configProps.getProperty("SelectedSeperator"));
                this.selectedCoin.setValue(configProps.getProperty("SelectedCoin"));
                this.selectedPlotCurrency.setValue(configProps.getProperty("SelectedPlotCurrency"));
                this.selectedStyleMode.setValue(configProps.getProperty("SelectedStyleMode"));
                this.dateFrom.setValue(LocalDate.parse(configProps.getProperty("SelectedDate")));
                if (!configProps.getProperty("LastUsedExportPath").equals(""))
                    this.lastExportPath = configProps.getProperty("LastUsedExportPath");
                this.showDisclaim = configProps.getProperty("ShowDisclaim").equals("true");
                this.selectedLaunchDefid = configProps.getProperty("SelectedLaunchDefid").equals("true");


            } catch (Exception e) {
                e.printStackTrace();
                saveSettings();
            }
        }
    }

    public void saveSettings() {

        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(SETTING_FILE_PATH);
            csvWriter.append("SelectedLanguage=" + this.selectedLanguage.getValue()).append("\n");
            csvWriter.append("SelectedFiatCurrency=" + this.selectedFiatCurrency.getValue()).append("\n");
            csvWriter.append("SelectedDecimal=" + this.selectedDecimal.getValue()).append("\n");
            csvWriter.append("SelectedSeperator=" + this.selectedSeperator.getValue()).append("\n");
            csvWriter.append("SelectedCoin=" + this.selectedCoin.getValue()).append("\n");
            csvWriter.append("SelectedPlotCurrency=" + this.selectedPlotCurrency.getValue()).append("\n");
            csvWriter.append("SelectedStyleMode=" + this.selectedStyleMode.getValue()).append("\n");
            csvWriter.append("SelectedDate=" + this.dateFrom.getValue()).append("\n");
            csvWriter.append("LastUsedExportPath=" + this.lastExportPath).append("\n");
            csvWriter.append("ShowDisclaim=" + this.showDisclaim).append("\n");
            csvWriter.append("SelectedLaunchDefid=" + this.selectedLaunchDefid).append("\n");
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            this.logger.warning("Exception occured: " + e.toString());
        }
    }

    public void getConfig() {
        long startTime = System.currentTimeMillis();

        // copy config file
        try {
            File pathConfig = new File(this.CONFIG_FILE_PATH);
            File pathPortfoliohDataConfig = new File(this.DEFI_PORTFOLIO_HOME+"\\defi.conf");

            Files.copy(pathConfig.toPath(), pathPortfoliohDataConfig.toPath());
        }
        catch(Exception e){
        }
        // adapt port
        Path path = Paths.get(this.DEFI_PORTFOLIO_HOME+"\\defi.conf");
        Charset charset = StandardCharsets.UTF_8;
        try {
            String content = new String(Files.readAllBytes(path), charset);
            content = content.replaceAll("8555", "8554");
            Files.write(path, content.getBytes(charset));
        }catch(Exception e){
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime- startTime);
        // Load config
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.DEFI_PORTFOLIO_HOME+"\\defi.conf"));

            File configFile = new File(this.CONFIG_FILE_PATH);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.rpcauth = configProps.getProperty("rpcauth");
            this.rpcuser = configProps.getProperty("rpcuser");
            this.rpcpassword = configProps.getProperty("rpcpassword");
            this.rpcbind = configProps.getProperty("rpcbind");
            this.rpcport = configProps.getProperty("rpcport");
            this.auth = this.rpcuser + ":" + this.rpcpassword;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}