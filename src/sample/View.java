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
import java.text.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class View implements Initializable {

    private final StringConverter<String> S_CONVERTER = new DefaultStringConverter();

    @FXML
    private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse, anchorPanelExport;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strUpToDate, lblProgressBar, strUpdatingDatabase;
    @FXML
    private ComboBox cmbCoins, cmbCoinsAnalyse, cmbIntervall;
    @FXML
    private ImageView imgViewObj;
    @FXML
    private DatePicker dateExpStart = new DatePicker();
    @FXML
    private DatePicker dateExpEnd = new DatePicker();
    @FXML
    private DatePicker dateAnalyseStart = new DatePicker();
    @FXML
    private DatePicker dateAnalyseEnd = new DatePicker();
    @FXML
    private ProgressIndicator spinner = new ProgressIndicator();
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

        // Analyse Rewards Frame
        this.dateAnalyseStart.valueProperty().bindBidirectional(this.viewModel.dateAnalyseStart);
        this.dateAnalyseStart.setValue(LocalDate.now());
        this.dateAnalyseStart.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });
        this.dateAnalyseEnd.valueProperty().bindBidirectional(this.viewModel.dateAnalyseEnd);
        this.dateAnalyseEnd.setValue(LocalDate.now());
        this.dateAnalyseEnd.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });
        this.cmbCoinsAnalyse.getItems().addAll("BTC", "DFI", "ETH", "USTD");
        this.cmbCoinsAnalyse.valueProperty().bindBidirectional(this.viewModel.selectedCoinAnalyse);
        this.cmbIntervall.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        this.cmbIntervall.valueProperty().bindBidirectional(this.viewModel.cmbIntervall);


        // Export Rewards Frame
        this.cmbCoins.getItems().addAll("BTC", "DFI", "ETH", "USTD");
        this.cmbCoins.valueProperty().bindBidirectional(this.viewModel.selectedCoin);
        this.dateExpStart.valueProperty().bindBidirectional(this.viewModel.dateExpStart);
        this.dateExpStart.setValue(LocalDate.now());
        this.dateExpStart.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });
        this.dateExpEnd.valueProperty().bindBidirectional(this.viewModel.dateExpEnd);
        this.dateExpEnd.setValue(LocalDate.now());
        this.dateExpEnd.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
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

        poolIDColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String poolID, boolean empty) {
                super.updateItem(poolID, empty);
                if (empty) {
                    setText(null);
                } else {

                    String pool = "5";

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

        cryptoValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
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

        blockTimeColumn.setCellFactory(tc -> new TableCell<TransactionModel, Long>() {
            @Override
            protected void updateItem(Long blockTime, boolean empty) {
                super.updateItem(blockTime, empty);
                if (empty) {
                    setText(null);
                } else {
                    Date date = new Date(blockTime * 1000L);


                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    setText(dateFormat.format(date));
                }
            }
        });
    }
}
