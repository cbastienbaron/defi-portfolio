package sample;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class View implements Initializable{

    private final StringConverter<String> S_CONVERTER = new DefaultStringConverter();

    @FXML private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse , anchorPanelExport;
    @FXML private ProgressBar progressBar;
    @FXML private Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strUpToDate, lblProgressBar, strUpdatingDatabase;
    @FXML private ComboBox cmbCoins,cmbCoinsAnalyse, cmbIntervall;
    @FXML private ImageView imgViewObj;
    @FXML private DatePicker dateExpStart = new DatePicker();
    @FXML private DatePicker dateExpEnd = new DatePicker();
    @FXML private DatePicker dateAnalyseStart = new DatePicker();
    @FXML private DatePicker dateAnalyseEnd = new DatePicker();
    @FXML private ProgressIndicator spinner = new ProgressIndicator();
    @FXML private LineChart<Number,Number> hPlot;
    @FXML private TableView<TransactionModel> hTable;
    @FXML private TableColumn<TransactionModel,Long> blockTimeColumn;
    @FXML private TableColumn<TransactionModel,String> typeColumn;
    @FXML private TableColumn<TransactionModel,Double> cryptoValueColumn;
    @FXML private TableColumn<TransactionModel,String> cryptoCurrencyColumn;
    @FXML private TableColumn<TransactionModel,String> blockHashColumn;
    @FXML private TableColumn<TransactionModel,Integer> blockHeightColumn;
    @FXML private TableColumn<TransactionModel,String> poolIDColumn;
    @FXML private TableColumn<TransactionModel,String> ownerColumn;
    @FXML private TableColumn<TransactionModel,Double> fiatValueColumn;
    @FXML private TableColumn<TransactionModel,String> fiatCurrencyColumn;

    ViewModel viewModel = new ViewModel();

    public void btnUpdatePressed(){
        this.AnchorPanelUpdateDatabase.toFront();
    }
    public void btnAnalysePressed(){
        this.anchorPanelAnalyse.toFront();
        this.viewModel.hPlot = this.hPlot;
        this.viewModel.hTable = this.hTable;
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
        this.strUpdatingDatabase.textProperty().bindBidirectional(this.viewModel.strUpdatingDatabase);
        Bindings.bindBidirectional(this.spinner.visibleProperty(),this.viewModel.spinner);

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
        this.cmbCoinsAnalyse.getItems().addAll("BTC","DFI","ETH","USTD");
        this.cmbCoinsAnalyse.valueProperty().bindBidirectional(this.viewModel.selectedCoinAnalyse);


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

        hTable.itemsProperty().set(viewModel.getTransactionTable());
        ownerColumn.setCellValueFactory(param -> param.getValue().ownerProperty);
        blockTimeColumn.setCellValueFactory(new PropertyValueFactory("blockTimeProperty"));
        typeColumn.setCellValueFactory(new PropertyValueFactory("typeProperty"));
        cryptoCurrencyColumn.setCellValueFactory(new PropertyValueFactory("cryptoCurrencyProperty"));
        cryptoValueColumn.setCellValueFactory(new PropertyValueFactory("cryptoValueProperty"));
        blockHashColumn.setCellValueFactory(new PropertyValueFactory("blockHashProperty"));
        blockHeightColumn.setCellValueFactory(new PropertyValueFactory("blockHeightProperty"));
        poolIDColumn.setCellValueFactory(new PropertyValueFactory("poolIDProperty"));
        fiatValueColumn.setCellValueFactory(new PropertyValueFactory("fiatCurrencyProperty"));
        fiatCurrencyColumn.setCellValueFactory(new PropertyValueFactory("fiatCurrencyProperty"));
    }
}
