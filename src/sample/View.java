package sample;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.util.*;

public class View implements Initializable {

    @FXML
    private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse, anchorPanelExport;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strUpToDate, lblProgressBar, strUpdatingDatabase;
    @FXML
    private ComboBox<String> cmbCoins, cmbIntervall;
    @FXML
    private ImageView imgViewObj;
    @FXML
    private DatePicker dateFrom = new DatePicker();
    @FXML
    private DatePicker dateTo = new DatePicker();
    @FXML
    private final ProgressIndicator spinner = new ProgressIndicator();
    @FXML
    private LineChart<Number, Number> hPlot, hPlotKumuliert;
    @FXML
    private TableView<TransactionModel> hTable;
    @FXML
    private TableColumn<TransactionModel, Long> blockTimeColumn;
    @FXML
    private TableColumn<TransactionModel, String> typeColumn;
    @FXML
    private TableColumn<TransactionModel, Double> cryptoValueColumn;
    @FXML
    private TableColumn<TransactionModel, String> cryptoCurrencyColumn;
    @FXML
    private TableColumn<TransactionModel, String> blockHashColumn;
    @FXML
    private TableColumn<TransactionModel, Integer> blockHeightColumn;
    @FXML
    private TableColumn<TransactionModel, String> poolIDColumn;
    @FXML
    private TableColumn<TransactionModel, String> ownerColumn;
    @FXML
    private TableColumn<TransactionModel, Double> fiatValueColumn;
    @FXML
    private TableColumn<TransactionModel, String> fiatCurrencyColumn;

    ViewModel viewModel = new ViewModel();

    public void btnUpdatePressed() {
        this.AnchorPanelUpdateDatabase.toFront();
    }

    public void btnAnalysePressed() {
        this.anchorPanelAnalyse.toFront();
        this.viewModel.hPlot = this.hPlot;
        this.viewModel.hPlotKumuliert = this.hPlotKumuliert;
        this.viewModel.hTable = this.hTable;
    }

    public void btnExportPressed() {
        this.anchorPanelExport.toFront();
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.spinner.setVisible(true);

        this.viewModel.btnUpdateDatabasePressed();
        this.spinner.setVisible(false);
    }

    public void btnPlotPressed() {
        this.viewModel.plotPressed();
    }

    public void btnExportExcelPressed() {
        this.viewModel.exportToExcel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.AnchorPanelUpdateDatabase.toFront();

        // Update Database Frame
        this.strCurrentBlockLocally.textProperty().bindBidirectional(this.viewModel.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(this.viewModel.strCurrentBlockOnBlockchain);
        this.strUpdatingDatabase.textProperty().bindBidirectional(this.viewModel.strUpdatingDatabase);
        Bindings.bindBidirectional(this.spinner.visibleProperty(), this.viewModel.spinner);

        // Status image and text
        Bindings.bindBidirectional(this.imgViewObj.imageProperty(), this.viewModel.imgStatus);
        this.strUpToDate.textProperty().bindBidirectional(this.viewModel.strUpToDate);

        // Progressbar and label
        this.progressBar.progressProperty().bind(this.viewModel.progress);
        this.lblProgressBar.textProperty().bindBidirectional(this.viewModel.strProgressbar);

        this.cmbIntervall.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        this.cmbIntervall.valueProperty().bindBidirectional(this.viewModel.settingsController.cmbIntervall);

        this.cmbCoins.getItems().addAll(this.viewModel.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedCoin);

        this.dateFrom.valueProperty().bindBidirectional(this.viewModel.settingsController.dateFrom);
        this.dateFrom.setValue(LocalDate.now());
        this.dateFrom.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });
        this.dateTo.valueProperty().bindBidirectional(this.viewModel.settingsController.dateTo);
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        hTable.itemsProperty().set(this.viewModel.getTransactionTable());
        ownerColumn.setCellValueFactory(param -> param.getValue().ownerProperty);
        blockTimeColumn.setCellValueFactory(new PropertyValueFactory("blockTimeProperty"));
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty);
        cryptoCurrencyColumn.setCellValueFactory(param -> param.getValue().cryptoCurrencyProperty);
        cryptoValueColumn.setCellValueFactory(new PropertyValueFactory("cryptoValueProperty"));
        blockHashColumn.setCellValueFactory(param -> param.getValue().blockHashProperty);
        blockHeightColumn.setCellValueFactory(new PropertyValueFactory("blockHeightProperty"));
        poolIDColumn.setCellValueFactory(param -> param.getValue().poolIDProperty);
        fiatValueColumn.setCellValueFactory(new PropertyValueFactory("fiatValueProperty"));
        fiatCurrencyColumn.setCellValueFactory(param -> param.getValue().fiatCurrencyProperty);

        poolIDColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String poolID, boolean empty) {
                super.updateItem(poolID, empty);
                if (empty) {
                    setText(null);
                } else {

                    String pool = "-";

                    switch (poolID) {
                        case "4":
                            pool = "ETH-DFI";
                            break;
                        case "5":
                            pool = "BTC-DFI";
                            break;
                        case "6":
                            pool = "USDT-DFI";
                            break;
                        default:
                            break;
                    }

                    setText(pool);
                }
            }
        });

        cryptoValueColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {
                    String pattern = "#######.########";
                    DecimalFormat decimalFormat = new DecimalFormat(pattern);
                    setText(decimalFormat.format(cryptoValue));
                }
            }
        });

        blockTimeColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Long blockTime, boolean empty) {
                super.updateItem(blockTime, empty);
                if (empty) {
                    setText(null);
                } else {
                    Date date = new Date(blockTime * 1000L);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    setText(dateFormat.format(date));
                }
            }
        });
    }
}
