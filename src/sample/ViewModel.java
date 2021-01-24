package sample;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class ViewModel {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("Current Block locally: 0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("Current Block on Blockchain: 1000");
    public ObjectProperty<javafx.scene.image.Image> imgStatus = new SimpleObjectProperty<>();
    public StringProperty strUpToDate = new SimpleStringProperty("Database not up to date");
    public SimpleDoubleProperty progress = new SimpleDoubleProperty(.0);
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public StringProperty selectedCoin = new SimpleStringProperty("BTC");
    public StringProperty selectedCoinAnalyse = new SimpleStringProperty("DFI");
    public StringProperty selectedFiatCurrency = new SimpleStringProperty("EUR");
    public StringProperty cmbIntervall = new SimpleStringProperty("Daily");
    public StringProperty selectedDecimal = new SimpleStringProperty(",");
    public StringProperty selectedSeperator = new SimpleStringProperty(",");
    public ObjectProperty<java.time.LocalDate> dateExpStart = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateExpEnd = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateAnalyseStart = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateAnalyseEnd = new SimpleObjectProperty();
    public StringProperty strUpdatingDatabase = new SimpleStringProperty("");
    public BooleanProperty spinner = new SimpleBooleanProperty();
    public LineChart<Number, Number> hPlot;
    public TableView hTable;
    public List<TransactionModel> transactionModelList;
    public ObservableList<TransactionModel> transactionList;

    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strPathDefiCli = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";

    public TransactionController transactionController = new TransactionController(this.strPathAppData + this.strTransactionData);
    public CoinPriceController coinPriceController = new CoinPriceController(this.strPathAppData + strCoinPriceData);
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
        this.expService = new ExportService(this.coinPriceController);

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

    public void plotPressed() {
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName("Rewards");

        long TimeStampStart = Timestamp.valueOf(String.valueOf(this.dateAnalyseStart.getValue()) + " 00:00:00").getTime()/1000L;
        long TimeStampEnd = Timestamp.valueOf(String.valueOf(this.dateAnalyseEnd.getValue()) + " 23:59:59").getTime()/1000L;

        List<TransactionModel> transactionsInTime = TransactionController.getTransactionsInTime(this.transactionList,TimeStampStart,TimeStampEnd);
        TreeMap<String,Double> joinedTransactions = TransactionController.getRewardsJoined(transactionsInTime, this.cmbIntervall.getValue(),this.selectedCoinAnalyse.getValue());


        for (HashMap.Entry<String, Double> entry : joinedTransactions.entrySet()) {
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        if (this.hPlot.getData().size() == 1) {
            this.hPlot.getData().remove(0);
        }

        this.hPlot.getData().add(series);
        for (XYChart.Series<Number, Number> s : this.hPlot.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
//
    }

    public ObservableList<TransactionModel> getTransactionTable(){
        return transactionList;
    }

    public void exportToExcel() {

        Locale localeDecimal = Locale.GERMAN;
        if(selectedDecimal.equals(".")){
            localeDecimal = Locale.US;
        }

        //TODO brows dialog export --> strPathAppData

        LocalDate startTime = this.dateExpStart.getValue();
        long TimeStampStart = Timestamp.valueOf(String.valueOf(startTime) + " 00:00:00").getTime()/1000L;
        LocalDate endTime = this.dateExpEnd.getValue();
        long TimeStampEnd = Timestamp.valueOf(String.valueOf(endTime) + " 23:59:59").getTime()/1000L;
        boolean success = this.expService.exportTransactionToExcel(this.transactionModelList, strPathAppData+"//test.csv",this.coinPriceHistory,this.selectedFiatCurrency.getValue(),localeDecimal,this.selectedSeperator.getValue(),TimeStampStart,TimeStampEnd);

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
