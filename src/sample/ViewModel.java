package sample;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.util.Duration;
import java.io.File;
import java.sql.Array;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class ViewModel {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("Current Block locally: 0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("Current Block on Blockchain: No connection");
    public ObjectProperty<javafx.scene.image.Image> imgStatus = new SimpleObjectProperty<>();
    public StringProperty strUpToDate = new SimpleStringProperty("Database not up to date");
    public SimpleDoubleProperty progress = new SimpleDoubleProperty(.0);
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public StringProperty strUpdatingDatabase = new SimpleStringProperty("");
    public BooleanProperty spinner = new SimpleBooleanProperty();
    public LineChart<Number, Number> hPlot, hPlotKumuliert;
    public TableView hTable;
    public List<TransactionModel> transactionModelList;
    public ObservableList<TransactionModel> transactionList;
    public String[] cryptoCurrencies = new String[]{"BTC", "DFI", "ETH", "USDT","LTC","BCH"};
    public String[] fiatCurrencies = new String[]{"EUR", "USDT", "CHF"};


    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strPathDefiCli = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";
    public String strSettingsData = "settings.portfolio";

    public CoinPriceController coinPriceController = new CoinPriceController(this.strPathAppData + strCoinPriceData);
    public SettingsController settingsController = new SettingsController(this.strPathAppData + strSettingsData);
    public TransactionController transactionController = new TransactionController(this.strPathAppData + this.strTransactionData,this.settingsController,this.coinPriceController);

    public CoinPriceModel coinPriceHistory;
    public ExportService expService;

    public ViewModel() {
        // generate folder %appData%//defiPortfolio if no one exists
        File directory = new File(strPathAppData);
        if (!directory.exists()) {
            directory.mkdir();
        }

        /*Process p;
        StringBuilder processOutput = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(strPathDefid);

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        this.transactionList =FXCollections.observableArrayList(this.transactionController.transactionList);
        this.transactionModelList = this.transactionController.transactionList;
        this.coinPriceHistory = this.coinPriceController.coinPriceModel;
        this.expService = new ExportService(this.coinPriceController,this.transactionController);

        //var test = this.transactionController.getListAddressGroupingsRpc();


        this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + transactionController.getBlockCountRpc());
        this.strCurrentBlockLocally.set("Current Block locally: " + transactionController.getLocalBlockCount());

        // Init gui elements
        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\warning.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);
    }

    public boolean updateTransactionData() {

        if(!this.coinPriceController.updateCoinPriceData()){
            //Logg error @ coin price update
           // upDatePrice();
        }

        if (new File(strPathAppData + strTransactionData).exists()) {
            int depth = this.transactionController.getBlockCountRpc() - this.transactionController.getLocalBlockCount();
            return this.transactionController.updateTransactionData(depth);
        } else {
            return this.transactionController.updateTransactionData(this.transactionController.getAccountHistoryCountRpc());
        }
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.spinner.setValue(true);
        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\accept.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);

        if (updateTransactionData()) {

            this.strUpToDate.setValue("Database up to date");
            this.strProgressbar.setValue("Updating database finished");

            this.strCurrentBlockLocally.set("Current Block locally: " + this.transactionController.getLocalBlockCount());
            this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + this.transactionController.getBlockCountRpc());

            transactionList.removeAll(transactionList);
            transactionList.addAll(this.transactionController.transactionList);
            this.transactionModelList = this.transactionController.transactionList;

        } else {

        }
        PauseTransition pause = new PauseTransition(Duration.seconds(10));
        pause.setOnFinished(e -> this.strProgressbar.setValue(null));
        pause.play();

        this.spinner.setValue(false);
    }

    public void plotUpdate() {

        XYChart.Series<Number, Number> rewardsSeries = new XYChart.Series();
        rewardsSeries.setName("Rewards");

        XYChart.Series<Number, Number> commisionsSeries = new XYChart.Series();
        commisionsSeries.setName("Commissions");

        long TimeStampStart = Timestamp.valueOf(this.settingsController.dateFrom.getValue() + " 00:00:00").getTime()/1000L;
        long TimeStampEnd = Timestamp.valueOf(this.settingsController.dateTo.getValue() + " 23:59:59").getTime()/1000L;

        List<TransactionModel> transactionsInTime = this.transactionController.getTransactionsInTime(this.transactionList,TimeStampStart,TimeStampEnd);
        TreeMap<String,Double> joinedRewards =  this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),this.settingsController.selectedCoin.getValue(),"Rewards");
        TreeMap<String,Double> joinedCommissions =  this.transactionController.getFiatMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),this.settingsController.selectedCoin.getValue(),"Commission");

        // Plot timeSeries
        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }

        // Plot timeSeries
        for (HashMap.Entry<String, Double> entry : joinedCommissions.entrySet()) {
            commisionsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }

        this.hPlot.getData().clear();

        if (this.hPlot.getData().size() == 1) {
            this.hPlot.getData().remove(0);
        }

        this.hPlot.getData().add(rewardsSeries);
        this.hPlot.getData().add(commisionsSeries);

        for (XYChart.Series<Number, Number> s : this.hPlot.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }

        // Plot Kumuliert
        Collection<Double> values = joinedRewards.values();
        ArrayList<Double> valueList = new ArrayList<>(values);
        XYChart.Series<Number, Number> series2 = new XYChart.Series();

        for (int i = 0 ;i<valueList.size()-1;i++){
            valueList.set(i+1, valueList.get(i)+valueList.get(i+1));
        }

        int iterator = 0;
        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
              entry.setValue(valueList.get(iterator));
              iterator++;
        }
        series2.setName("Rewards kumuliert");

        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
            series2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        if (this.hPlotKumuliert.getData().size() == 1) {
            this.hPlotKumuliert.getData().remove(0);
        }

        this.hPlotKumuliert.getData().add(series2);
        for (XYChart.Series<Number, Number> s : this.hPlotKumuliert.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }


    }

    public ObservableList<TransactionModel> getTransactionTable(){
        return transactionList;
    }

    public void exportToExcel() {

        Locale localeDecimal = Locale.GERMAN;
        if(settingsController.selectedDecimal.equals(".")){
            localeDecimal = Locale.US;
        }

        //TODO brows dialog export --> strPathAppData

        LocalDate startTime = this.settingsController.dateFrom.getValue();
        long TimeStampStart = Timestamp.valueOf(String.valueOf(startTime) + " 00:00:00").getTime()/1000L;
        LocalDate endTime = this.settingsController.dateTo.getValue();
        long TimeStampEnd = Timestamp.valueOf(String.valueOf(endTime) + " 23:59:59").getTime()/1000L;
        boolean success = this.expService.exportTransactionToExcel(this.transactionModelList, strPathAppData+"//test.csv",this.coinPriceHistory,this.settingsController.selectedFiatCurrency.getValue(),localeDecimal,this.settingsController.selectedSeperator.getValue(),TimeStampStart,TimeStampEnd);

        if (success) {
            this.strProgressbar.setValue("Excel successfully exported!");
            //   this.strProgressbar.setTextFill(Color.LIGHTGREEN);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.strProgressbar.setValue(null));
            pause.play();
        } else {
            this.strProgressbar.setValue("Error while exporting excel!");
            //  this.strProgressbar.setTextFill(Color.RED);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.strProgressbar.setValue(null));
            pause.play();
        }

    }

}
