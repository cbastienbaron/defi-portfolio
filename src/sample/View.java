package sample;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class View implements Initializable{

    @FXML private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse , anchorPanelExport;
    @FXML private ProgressBar progressBar;
    @FXML private Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strUpToDate, lblProgressBar;
    @FXML private ComboBox cmbCoins;
    @FXML private ImageView imgViewObj;
    @FXML private DatePicker dateExpStart = new DatePicker();
    @FXML private DatePicker dateExpEnd = new DatePicker();
    @FXML private DatePicker dateAnalyseStart = new DatePicker();
    @FXML private DatePicker dateAnalyseEnd = new DatePicker();
    @FXML private LineChart<Number,Number> hPlot;

    ViewModel viewModel = new ViewModel();

    public void btnUpdatePressed(){
        this.AnchorPanelUpdateDatabase.toFront();
    }
    public void btnAnalysePressed(){
        this.anchorPanelAnalyse.toFront();
        this.viewModel.hPlot = this.hPlot;
    }
    public void btnExportPressed(){
        this.anchorPanelExport.toFront();
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.viewModel.btnUpdateDatabasePressed();
    }
    public void btnPlotPressed(){
        this.viewModel.plotPressed();
    }

    public void btnExportExcelPressed(){
       this.viewModel.exportToExcel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.AnchorPanelUpdateDatabase.toFront();

        // Update Database Frame
        this.strCurrentBlockLocally.textProperty().bindBidirectional(this.viewModel.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(this.viewModel.strCurrentBlockOnBlockchain);

        // Status image and text
        Bindings.bindBidirectional(this.imgViewObj.imageProperty(), this.viewModel.imgStatus);
        this.strUpToDate.textProperty().bindBidirectional(this.viewModel.strUpToDate);

        // Progressbar and label
        this.progressBar.progressProperty().bind(this.viewModel.progress);
        this.lblProgressBar.textProperty().bindBidirectional(this.viewModel.strProgressbar);

        // Analyse Rewards Frame
        this.dateAnalyseStart.valueProperty().bindBidirectional(this.viewModel.dateAnalyseStart);
        this.dateAnalyseStart.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0 );
            }
        });
        this.dateAnalyseEnd.valueProperty().bindBidirectional(this.viewModel.dateAnalyseEnd);
        this.dateAnalyseEnd.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0 );
            }
        });

        // Export Rewards Frame
        this.cmbCoins.getItems().addAll("BTC","DFI","ETH","USTD");
        this.cmbCoins.valueProperty().bindBidirectional(this.viewModel.selectedCoin);
        this.dateExpStart.valueProperty().bindBidirectional(this.viewModel.dateExpStart);
        this.dateExpStart.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0 );
            }
        });
        this.dateExpEnd.valueProperty().bindBidirectional(this.viewModel.dateExpEnd);
        this.dateExpEnd.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0 );
            }
        });

      //  this.coinPriceHistory = Update.updateCoinHistory(strPathAppData+"coinHistory.portfolio");
      //  this.transactionList = Update.updatePortfolioData(strPathAppData+"data.portfolio");

//        double DFI_Amount = 0;
//        if(!decimalLetter.equals(",")) {
//            localeDecimal = Locale.US;
//        }
    }
}
