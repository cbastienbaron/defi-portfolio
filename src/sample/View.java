package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private ComboBox<String> cmbCoins, cmbIntervall,cmbFiat;
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
        this.viewModel.plotUpdate();
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.spinner.setVisible(true);

        this.viewModel.btnUpdateDatabasePressed();
        this.spinner.setVisible(false);
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
        this.cmbIntervall.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.hPlot!=null)  viewModel.plotUpdate();
        });

        this.cmbCoins.getItems().addAll(this.viewModel.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedCoin);
        this.cmbCoins.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.hPlot!=null) viewModel.plotUpdate();
        });

        this.cmbFiat.getItems().addAll(this.viewModel.fiatCurrencies);
        this.cmbFiat.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedFiatCurrency);
        this.cmbFiat.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.hPlot!=null) viewModel.plotUpdate();
        });



        this.dateFrom.valueProperty().bindBidirectional(this.viewModel.settingsController.dateFrom);
        this.dateFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.hPlot!=null) viewModel.plotUpdate();
        });
        this.dateFrom.setValue(LocalDate.now());
        this.dateFrom.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateTo.valueProperty().bindBidirectional(this.viewModel.settingsController.dateTo);
        this.dateTo.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.hPlot!=null) viewModel.plotUpdate();
        });
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        hTable.itemsProperty().set(this.viewModel.getTransactionTable());
        ownerColumn.setCellValueFactory(param -> param.getValue().getOwner());
        blockTimeColumn.setCellValueFactory(param -> param.getValue().getBlockTime().asObject());
        typeColumn.setCellValueFactory(param -> param.getValue().getType());
        cryptoCurrencyColumn.setCellValueFactory(param -> param.getValue().getCrypto());
        cryptoValueColumn.setCellValueFactory(param -> param.getValue().getCryptoValue().asObject());
        blockHashColumn.setCellValueFactory(param -> param.getValue().getBlockHash());
        blockHeightColumn.setCellValueFactory(param -> param.getValue().getBlockHeight().asObject());
        poolIDColumn.setCellValueFactory(param -> param.getValue().getPoolID());
        fiatValueColumn.setCellValueFactory(param -> param.getValue().getFiat().asObject());
        fiatCurrencyColumn.setCellValueFactory(param -> param.getValue().getFiatCurrency());

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

        fiatCurrencyColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String fiatCurrency, boolean empty) {
                super.updateItem(fiatCurrency, empty);
                if (empty) {
                    setText(null);
                } else {

                    setText(viewModel.settingsController.selectedFiatCurrency.getValue());
                }
            }
        });


        fiatValueColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (viewModel.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
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
                    Locale localeDecimal = Locale.GERMAN;
                    if (viewModel.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
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
