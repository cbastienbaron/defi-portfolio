package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.util.*;

public class View implements Initializable {

    @FXML
    private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse, anchorPanelRawData;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strUpToDate, lblProgressBar;
    @FXML
    private ComboBox<String> cmbCoins, cmbIntervall,cmbFiat,cmbPlotCurrency;
    @FXML
    private ImageView imgViewObj;
    @FXML
    private DatePicker dateFrom = new DatePicker();
    @FXML
    private DatePicker dateTo = new DatePicker();
    @FXML
    private final ProgressIndicator spinner = new ProgressIndicator();
    @FXML
    private LineChart<Number, Number> plotRewards,plotCommissions,plotCommissions2;
    @FXML
    private TableView<TransactionModel> rawDataTable;
    @FXML
    private TableView<PoolPairModel> plotTable;
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
    @FXML
    private TableColumn<PoolPairModel, Long> timeStampColumn;
    @FXML
    private TableColumn<PoolPairModel, String> typePlotColumn;
    @FXML
    private TableColumn<PoolPairModel, Double> crypto1Column;
    @FXML
    private TableColumn<PoolPairModel, Double> crypto2Column;
    @FXML
    private TableColumn<PoolPairModel, Double> fiatColumn;
    @FXML
    private TableColumn<PoolPairModel, String> poolPairColumn;

    ViewModel viewModel = new ViewModel();

    public void btnUpdatePressed() {
        this.AnchorPanelUpdateDatabase.toFront();
    }

    public void btnAnalysePressed() {
        this.anchorPanelAnalyse.toFront();
        this.viewModel.plotRewards = this.plotRewards;
        this.viewModel.plotCommissions = this.plotCommissions;
        this.viewModel.plotCommissions2 = this.plotCommissions2;
        this.viewModel.plotUpdate();
    }

    public void btnRawDataPressed() {
        this.anchorPanelRawData.toFront();
        this.viewModel.plotRewards = this.plotRewards;
        this.viewModel.plotCommissions = this.plotCommissions;
        this.viewModel.plotCommissions2 = this.plotCommissions2;
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {

        this.viewModel.btnUpdateDatabasePressed();

    }

    public void closePressed(){
        System.exit(0);
    }
    public void helpPressed() throws IOException {
        //  final ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\src\\icons\\mail.png");
        //  JOptionPane.showMessageDialog(null, "Contact us: \ndefiportfoliomanagement@gmail.com\n", "Contact information", JOptionPane.INFORMATION_MESSAGE, icon);
        Parent root = FXMLLoader.load(getClass().getResource("HelpFXML.fxml"));
        Scene scene = new Scene(root);
        Stage s = new Stage();
        s.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/help.png"));
        s.setTitle(("Contact information"));
        s.setScene(scene);
        s.show();
    }
    public void openAccountInformation() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("DonateFXML.fxml"));
        Scene scene = new Scene(root);
        Stage s = new Stage();
        s.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/donate.png"));
        s.setTitle("Donate");
        s.setScene(scene);
        s.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.AnchorPanelUpdateDatabase.toFront();

        // Update Database Frame
        this.strCurrentBlockLocally.textProperty().bindBidirectional(this.viewModel.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(this.viewModel.strCurrentBlockOnBlockchain);
        //this.strUpdatingDatabase.textProperty().bindBidirectional(this.viewModel.strUpdatingDatabase);
       // Bindings.bindBidirectional(this.spinner.visibleProperty(), this.viewModel.spinner);

        // Status image and text
        Bindings.bindBidirectional(this.imgViewObj.imageProperty(), this.viewModel.imgStatus);
        this.strUpToDate.textProperty().bindBidirectional(this.viewModel.strUpToDate);

        // Progressbar and label
        this.progressBar.progressProperty().bind(this.viewModel.progress);
        this.lblProgressBar.textProperty().bindBidirectional(this.viewModel.strProgressbar);

        this.cmbIntervall.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        this.cmbIntervall.valueProperty().bindBidirectional(this.viewModel.settingsController.cmbIntervall);
        this.cmbIntervall.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null)  viewModel.plotUpdate();
        });

        this.cmbCoins.getItems().addAll(this.viewModel.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedCoin);
        this.cmbCoins.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null) viewModel.plotUpdate();
        });

        this.cmbFiat.getItems().addAll(this.viewModel.fiatCurrencies);
        this.cmbFiat.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedFiatCurrency);
        this.cmbFiat.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null) viewModel.plotUpdate();
        });
        this.cmbPlotCurrency.getItems().addAll(this.viewModel.plotCurrencies);
        this.cmbPlotCurrency.valueProperty().bindBidirectional(this.viewModel.settingsController.selectedPlotCurrency);
        this.cmbPlotCurrency.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null) viewModel.plotUpdate();
        });


        this.dateFrom.valueProperty().bindBidirectional(this.viewModel.settingsController.dateFrom);
        this.dateFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null) viewModel.plotUpdate();
        });
        this.dateFrom.setValue(LocalDate.now().minusDays(7L));
        this.dateFrom.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateTo.valueProperty().bindBidirectional(this.viewModel.settingsController.dateTo);
        this.dateTo.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(viewModel.plotRewards !=null) viewModel.plotUpdate();
        });
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        initializeTableViewContextMenu();

        rawDataTable.itemsProperty().set(this.viewModel.getTransactionTable());
        rawDataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        plotTable.itemsProperty().set(this.viewModel.getPlotData());
        plotTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        timeStampColumn.setCellValueFactory(param -> param.getValue().getBlockTime().asObject());
        typePlotColumn.setCellValueFactory(param -> param.getValue().getType());
        crypto1Column.setCellValueFactory(param -> param.getValue().getCryptoValue1().asObject());
        crypto2Column.setCellValueFactory(param -> param.getValue().getCryptoValue2().asObject());
        fiatColumn.setCellValueFactory(param -> param.getValue().getFiatValue().asObject());
        poolPairColumn.setCellValueFactory(param-> param.getValue().getPoolPair());
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
                        case "8":
                            pool = "DOGE-DFI";
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

    private void initializeTableViewContextMenu(){

        ContextMenu contextMenuRawData = new ContextMenu();

        MenuItem menuItemCopySelected = new MenuItem("Copy");
        MenuItem menuItemCopyHeaderSelected = new MenuItem("Copy with header");
        MenuItem menuItemExportSelected = new MenuItem("Export selected to CSV");
        MenuItem menuItemExportAllSelected = new MenuItem("Export all selected to CSV");
        MenuItem menuItemOpenInDefiExplorer = new MenuItem("Open in DeFi Blockchain Explorer");

        menuItemCopySelected.setOnAction(event -> viewModel.copySelectedRawDataToClipboard( rawDataTable.selectionModelProperty().get().getSelectedItems(),false));
        menuItemCopyHeaderSelected.setOnAction(event -> viewModel.copySelectedRawDataToClipboard( rawDataTable.selectionModelProperty().get().getSelectedItems(),true));
        menuItemExportSelected.setOnAction(event -> viewModel.exportTransactionToExcel( rawDataTable.selectionModelProperty().get().getSelectedItems()));

        menuItemCopySelected.setOnAction(event -> viewModel.copySelectedDataToClipboard( plotTable.selectionModelProperty().get().getSelectedItems(),false));
        menuItemCopyHeaderSelected.setOnAction(event -> viewModel.copySelectedDataToClipboard( plotTable.selectionModelProperty().get().getSelectedItems(),true));
        menuItemExportSelected.setOnAction(event -> viewModel.exportPoolPairToExcel( plotTable.selectionModelProperty().get().getSelectedItems()));

        contextMenuRawData.getItems().add(menuItemCopySelected);
        contextMenuRawData.getItems().add(menuItemCopyHeaderSelected);
        contextMenuRawData.getItems().add(menuItemExportSelected);
        contextMenuRawData.getItems().add(menuItemOpenInDefiExplorer);
        rawDataTable.contextMenuProperty().set(contextMenuRawData);
        plotTable.contextMenuProperty().set(contextMenuRawData);

    }
}
