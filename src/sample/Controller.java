package sample;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import javafx.animation.PauseTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class Controller implements Initializable{


    @FXML private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse , anchorPanelExport;
    @FXML private ProgressBar progressBar;
    @FXML private Label strCurrentBlockLocally, strCurrentBlockOnChain, strUpToDate, lblProgressBar;
    @FXML private Button btnUpdateDatabse;
    @FXML private ComboBox cmbCoins;
    @FXML private ImageView imgViewObj;
    @FXML private DatePicker dateExpStart = new DatePicker();
    @FXML private DatePicker dateExpEnd = new DatePicker();
    @FXML private DatePicker dateAnalyseStart = new DatePicker();
    @FXML private DatePicker dateAnalyseEnd = new DatePicker();
    @FXML private LineChart<Number,Number> hPlot;
    @FXML private StackPane stackPane;
    @FXML private CategoryAxis xAxis;

    int blockCount,latestBlock;
    String strPathAppData,strPathDefid,strPathDefiCli;
    Transaction[] transactions = new Transaction[0];
    Gson gson = new Gson();
    List<Transaction> transactionList = new ArrayList<>();
    CoinPrice coinPriceHistory;
    String fiatCurrency = "EUR";
    String decimalLetter = ","; // , or . for decimal and . or , for thousands
    String exportSplitter = ",";
    Locale localeDecimal = Locale.GERMAN;

    public void btnUpdatePressed(){
        System.out.println("updatePressder");
        this.AnchorPanelUpdateDatabase.toFront();
    }
    public void btnAnalysePressed(){
        System.out.println("analysePressder");
        this.anchorPanelAnalyse.toFront();
    }
    public void btnExportPressed(){
        System.out.println("exportPressder");
        this.anchorPanelExport.toFront();
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.progressBar.setProgress(1);

        File file = new File(System.getProperty("user.dir")+"\\src\\icons\\acceppt.png");
        Image image = new Image(file.toURI().toString() );
        this.imgViewObj.setImage(image);

        this.strCurrentBlockLocally.setText("Current Block locally: " + this.blockCount );
        this.strUpToDate.setText("Database up to date");
    }
    public void btnPlotPressed(){
       // this.hPlot


        LocalDate startDate = this.dateAnalyseStart.getValue();
        LocalDate endDate = this.dateAnalyseStart.getValue();
        Timestamp aasdasd = Timestamp.valueOf("2021-01-02 00:00:00");


        Series<Number,Number> series = new Series();
        series.setName("My portfolio");

        series.getData().add(new XYChart.Data("2", 14));
        series.getData().add(new XYChart.Data("Mar", 15));
        series.getData().add(new XYChart.Data("3", 24));
        series.getData().add(new XYChart.Data("May", 34));
        series.getData().add(new XYChart.Data("4", 36));
        series.getData().add(new XYChart.Data("Jul", 22));
        series.getData().add(new XYChart.Data("Aug", 45));
        series.getData().add(new XYChart.Data("Sep", 43));
        series.getData().add(new XYChart.Data("Oct", 17));
        series.getData().add(new XYChart.Data("Nov", 29));
        series.getData().add(new XYChart.Data("Dec", 25));
        series.getData().add(new XYChart.Data("01", 30));
        series.getData().add(new XYChart.Data("10", 23));
        series.getData().add(new XYChart.Data("20", 14));
        series.getData().add(new XYChart.Data("0Mar", 15));
        series.getData().add(new XYChart.Data("03", 24));
        series.getData().add(new XYChart.Data("M0ay", 34));
        series.getData().add(new XYChart.Data("40", 36));
        series.getData().add(new XYChart.Data("J0ul", 22));
        series.getData().add(new XYChart.Data("Au0g", 45));
        series.getData().add(new XYChart.Data("Sep0", 43));
        series.getData().add(new XYChart.Data("0Oct", 17));
        series.getData().add(new XYChart.Data("N0ov", 29));
        series.getData().add(new XYChart.Data("De0c", 25));
        series.getData().add(new XYChart.Data("10", 30));

        if(this.hPlot.getData().size()==1){
            this.hPlot.getData().remove(0);
        }

        this.hPlot.getData().add(series);
        for(XYChart.Series<Number,Number> s : this.hPlot.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t =  new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event ->  d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event ->  d.getNode().getStyleClass().remove("onHover"));
            }
        }

//        List<XYChart.Series> v = new ArrayList<XYChart.Series>();
//        Collections.addAll( v, series);
      //  new ZoomManager(this.stackPane, this.hPlot, v);

    }

    public void btnExportExcelPressed(){
        boolean success = Export.exportToExcel(this.transactionList, this.strPathAppData,this.coinPriceHistory,this.fiatCurrency,this.localeDecimal,this.exportSplitter);
        if (success){
            this.lblProgressBar.setText("Excel successfully exported!");
            this.lblProgressBar.setTextFill(Color.LIGHTGREEN);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.lblProgressBar.setText(null));
            pause.play();
        }else{
            this.lblProgressBar.setText("Error while exporting excel!");
            this.lblProgressBar.setTextFill(Color.RED);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.lblProgressBar.setText(null));
            pause.play();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        this.hPlot.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    hPlot.setScaleX(1.0);
                    hPlot.setScaleY(1.0);
                }
            }
        });

        this.AnchorPanelUpdateDatabase.toFront();

        this.strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
        this.strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
        this.strPathDefiCli = System.getProperty("user.dir")+"\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";

        this.cmbCoins.getItems().addAll("BTC","DFI","ETH","USTD");
        this.cmbCoins.getSelectionModel().select(1);

        // generate folder %appData%//defiPortfolio if no one exists
        File directory = new File(strPathAppData);
        if (! directory.exists()){
            directory.mkdir();
        }

        this.coinPriceHistory = Update.updateCoinHistory(strPathAppData+"coinHistory.portfolio");
        this.transactionList = Update.updatePortfolioData(strPathAppData+"data.portfolio");


        double DFI_Amount = 0;
        if(!decimalLetter.equals(",")) {
            localeDecimal = Locale.US;
        }


    }


}
