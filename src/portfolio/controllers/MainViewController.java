package portfolio.controllers;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import portfolio.models.PoolPairModel;
import portfolio.models.PortfolioModel;
import portfolio.models.TransactionModel;
import portfolio.services.ExportService;
import portfolio.views.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Timer;

public class MainViewController {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("No connection");
    public StringProperty strLastUpdate = new SimpleStringProperty("-");
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public BooleanProperty bDataBase = new SimpleBooleanProperty(false);

    //View
    public MainView mainView;
    public JFrame frameUpdate;

    //Table and plot lists
    public List<PoolPairModel> poolPairModelList = new ArrayList<>();
    public ObservableList<PoolPairModel> poolPairList;

    //Init all controller and services
    public SettingsController settingsController = SettingsController.getInstance();
    public DonateController donateController = DonateController.getInstance();
    public HelpController helpController = HelpController.getInstance();
    public CoinPriceController coinPriceController = new CoinPriceController(this.settingsController.DEFI_PORTFOLIO_HOME + this.settingsController.strCoinPriceData);
    public TransactionController transactionController = new TransactionController(this.settingsController.DEFI_PORTFOLIO_HOME + this.settingsController.strTransactionData, this.settingsController, this.coinPriceController, this.settingsController.CONFIG_FILE_PATH);
    public ExportService expService;
    public boolean updateSingleton = true;

    public Timer timer = new Timer("Timer");

    public MainViewController() {

        this.settingsController.logger.info("Start DeFi-Portfolio");
        if(!this.transactionController.checkRpc())this.transactionController.startServer();

        // init all relevant lists for tables and plots
        this.poolPairList = FXCollections.observableArrayList(this.poolPairModelList);
        this.expService = new ExportService(this);

        // get last block locally
        this.strCurrentBlockLocally.set(Integer.toString(transactionController.getLocalBlockCount()));

        //start timer for getting last block on blockchain
        startTimer();
        //Add listener to Fiat
        this.settingsController.selectedFiatCurrency.addListener(
                (ov, t, t1) -> {
                    this.transactionController.getPortfolioList().clear();
                    for (TransactionModel transactionModel : this.transactionController.getTransactionList()) {
                        if (!transactionModel.getCryptoCurrencyValue().contains("-")) {
                            transactionModel.setFiatCurrency(t1);
                            transactionModel.setFiatValue(transactionModel.getCryptoValueValue() * this.coinPriceController.getPriceFromTimeStamp(transactionModel.getCryptoCurrencyValue() + t1, transactionModel.getBlockTimeValue() * 1000L));
                        }

                        if (transactionModel.getTypeValue().equals("Rewards") | transactionModel.getTypeValue().equals("Commission")) {
                            this.transactionController.addToPortfolioModel(transactionModel);
                        }
                    }
                }
        );

    }

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerController(this), 0, 5000L);
    }

    public void stopTimer() {
        timer.cancel();
    }

    public void copySelectedRawDataToClipboard(List<TransactionModel> list, boolean withHeaders) {
        StringBuilder sb = new StringBuilder();
        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            for (TableColumn column : this.mainView.rawDataTable.getColumns()
            ) {
                sb.append(column.getText()).append(this.settingsController.selectedSeperator.getValue());
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }
        for (TransactionModel transaction : list) {
            sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTime().getValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
            String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.getAmount().getValue());
            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(this.settingsController.selectedSeperator.getValue());
            sb.append(CoinsAndAmounts[1]).append(this.settingsController.selectedSeperator.getValue());
            sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(this.settingsController.selectedFiatCurrency.getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getPoolID().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHeight().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHash().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getOwner().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getTxIDValue());
            sb.append("\n");
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void copySelectedDataToClipboard(List<PoolPairModel> list, boolean withHeaders) {
        StringBuilder sb = new StringBuilder();
        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Übersicht":
                case "Kommissionen":
                case "Commissions":
                    sb.append((this.mainView.plotTable.getColumns().get(0).getText() + "," + this.mainView.plotTable.getColumns().get(1).getText() + "," + this.mainView.plotTable.getColumns().get(2).getText() + "," + this.mainView.plotTable.getColumns().get(3).getText() + "," + this.mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                case "Belohnungen":
                    sb.append((this.mainView.plotTable.getColumns().get(0).getText() + "," + this.mainView.plotTable.getColumns().get(1).getText() + "," + this.mainView.plotTable.getColumns().get(3).getText() + "," + this.mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
        }

        for (PoolPairModel poolPair : list
        ) {
            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Übersicht":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Rewards":
                case "Belohnungen":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Commissions":
                case "Kommissionen":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                default:
                    break;
            }
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public boolean updateTransactionData() {
            if (new File(this.settingsController.DEFI_PORTFOLIO_HOME + this.settingsController.strTransactionData).exists()) {
                int depth = Integer.parseInt(this.transactionController.getBlockCountRpc()) - this.transactionController.getLocalBlockCount();
                this.transactionController.updateJFrame();
                return this.transactionController.updateTransactionData(depth);
            } else {
                this.transactionController.updateJFrame();
                return this.transactionController.updateTransactionData(Integer.parseInt(this.transactionController.getBlockCountRpc())); // - this.transactionController.getAccountHistoryCountRpc());
            }
    }

    public void btnUpdateDatabasePressed() {

        if (this.updateSingleton) {
            this.updateSingleton = false;
            if (updateTransactionData()) {

                this.showUpdateWindow();
                this.strCurrentBlockLocally.set(Integer.toString(this.transactionController.getLocalBlockCount()));
                this.strCurrentBlockOnBlockchain.set(this.transactionController.getBlockCountRpc());
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                this.strLastUpdate.setValue(dateFormat.format(date));
                this.closeUpdateWindow();

            } else {
                if (!this.transactionController.checkRpc()) {
                    this.strCurrentBlockOnBlockchain.set(this.settingsController.translationList.getValue().get("NoConnection").toString());
                }
            }
        }
        this.updateSingleton = true;
    }

    public void showUpdateWindow() {
        this.frameUpdate = new JFrame("Loading Database");
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\defi-portfolio\\src\\icons\\updating.png");
        JLabel jl = new JLabel("     Updating local files. Please wait...!", icon, JLabel.CENTER);
        frameUpdate.add(jl);

        if (this.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            jl.setForeground(Color.WHITE);
        } else {
            jl.setForeground(Color.BLACK);
        }
        if (this.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            frameUpdate.getContentPane().setBackground(new Color(55, 62, 67));
        }

        frameUpdate.setSize(350, 125);
        frameUpdate.setLocationRelativeTo(null);
        frameUpdate.setUndecorated(true);
        frameUpdate.setVisible(true);
        frameUpdate.toFront();
    }

    public void closeUpdateWindow() {
        this.frameUpdate.setVisible(false);
        this.frameUpdate.dispose();
    }

    public void plotUpdate(String openedTab) {
        switch (openedTab) {
            case "Overview":
            case "Übersicht":
                updateOverview();
                break;
            case "Rewards":
            case "Belohnungen":
                updateRewards();
                break;
            case "Commissions":
            case "Kommissionen":
                updateCommissions();
                break;
            default:
                break;
        }
    }

    public void updateOverview() {

        this.poolPairModelList.clear();
        this.mainView.plotOverview.setLegendVisible(true);
        this.mainView.plotOverview.getData().clear();
        this.mainView.plotOverview.getYAxis().setLabel("Total (" + this.settingsController.selectedFiatCurrency.getValue() + ")");

        double maxValue = 0;

        for (String poolPair : this.settingsController.cryptoCurrencies) {

            XYChart.Series<Number, Number> overviewSeries = new XYChart.Series();
            overviewSeries.setName(poolPair);

            if (this.transactionController.getPortfolioList().containsKey(poolPair + "-" + this.settingsController.selectedIntervallInt)) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(poolPair + "-" + this.settingsController.selectedIntervallInt).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervallInt)) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervallInt)) <= 0) {

                        if (poolPair.equals(entry.getValue().getPoolPairValue())) {
                            overviewSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value()));
                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getFiatRewards1Value(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), poolPair));
                        }
                    }
                }

                this.mainView.yAxis.setAutoRanging(false);

                maxValue += overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble();
                this.mainView.yAxis.setUpperBound(maxValue * 1.1);
                /*
                if (maxValue < overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble()) {
                    this.mainView.yAxis.setUpperBound(overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble() * 1.10);
                    maxValue = overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble();
                }*/
                this.mainView.plotOverview.getData().add(overviewSeries);
                this.mainView.plotOverview.setCreateSymbols(true);
            }

        }
        for (XYChart.Series<Number, Number> s : this.mainView.plotOverview.getData()) {
            if (s != null) {
                for (XYChart.Data d : s.getData()) {
                    if (d != null) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }
            }
        }

        this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
        this.poolPairList.clear();
        this.poolPairList.addAll(this.poolPairModelList);
    }

    public void updateRewards() {

        XYChart.Series<Number, Number> rewardsSeries = new XYChart.Series();

        this.poolPairModelList.clear();
        this.mainView.plotRewards.setLegendVisible(false);
        this.mainView.plotRewards.getData().clear();

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
        } else {
            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt)) {

            if (this.settingsController.selectedPlotType.getValue().equals(this.settingsController.translationList.getValue().get("Individual"))) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt).entrySet()) {

                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervallInt)) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervallInt)) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinRewards1Value()));
                        } else {
                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value()));
                        }
                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), entry.getValue().getFiatRewards1Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }


                if (this.mainView.plotRewards.getData().size() == 1) {
                    this.mainView.plotRewards.getData().remove(0);
                }

                this.mainView.plotRewards.getData().add(rewardsSeries);

                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }
            } else {

                XYChart.Series<Number, Number> rewardsCumulated = new XYChart.Series();

                double cumulatedCoinValue = 0;
                double cumulatedFiatValue = 0;

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervallInt)) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervallInt)) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            cumulatedCoinValue = cumulatedCoinValue + entry.getValue().getCoinRewards1Value();
                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedCoinValue));
                        } else {
                            cumulatedFiatValue = cumulatedFiatValue + entry.getValue().getFiatRewards1Value();
                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedFiatValue));
                        }

                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), entry.getValue().getFiatRewards1Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }
                if (this.mainView.plotRewards.getData().size() == 1) {
                    this.mainView.plotRewards.getData().remove(0);
                }

                this.mainView.plotRewards.getData().add(rewardsCumulated);

                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }

            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
            this.poolPairList.clear();
            this.poolPairList.addAll(this.poolPairModelList);
        }

    }

    public void updateCommissions() {

        XYChart.Series<Number, Number> commissionsSeries1 = new XYChart.Series();
        XYChart.Series<Number, Number> commissionsSeries2 = new XYChart.Series();
        this.mainView.plotCommissions1.getData().clear();
        this.mainView.plotCommissions2.getData().clear();
        this.poolPairModelList.clear();
        this.poolPairList.clear();
        this.mainView.plotCommissions1.setLegendVisible(false);
        this.mainView.plotCommissions2.setLegendVisible(false);

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0]);
        } else {
            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedFiatCurrency.getValue() + " (" + this.settingsController.selectedCoin.getValue().split("-")[1] + ")");
            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedFiatCurrency.getValue() + " (" + this.settingsController.selectedCoin.getValue().split("-")[0] + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt)) {

            if (this.settingsController.selectedPlotType.getValue().equals(this.settingsController.translationList.getValue().get("Individual"))) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervallInt)) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervallInt)) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions1Value()));
                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions2Value()));
                        } else {
                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions1Value()));
                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions2Value()));
                        }
                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }


                this.mainView.plotCommissions1.getData().add(commissionsSeries1);
                this.mainView.plotCommissions2.getData().add(commissionsSeries2);

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            } else {

                XYChart.Series<Number, Number> rewardsCumulated1 = new XYChart.Series();
                XYChart.Series<Number, Number> rewardsCumulated2 = new XYChart.Series();

                double cumulatedCommissions1CoinValue = 0;
                double cumulatedCommissions1FiatValue = 0;
                double cumulatedCommissions2CoinValue = 0;
                double cumulatedCommissions2FiatValue = 0;
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervallInt).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervallInt)) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervallInt)) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            cumulatedCommissions1CoinValue = cumulatedCommissions1CoinValue + entry.getValue().getCoinCommissions1Value();
                            cumulatedCommissions2CoinValue = cumulatedCommissions2CoinValue + entry.getValue().getCoinCommissions2Value();
                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1CoinValue));
                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2CoinValue));
                        } else {
                            cumulatedCommissions1FiatValue = cumulatedCommissions1FiatValue + entry.getValue().getFiatCommissions1Value();
                            cumulatedCommissions2FiatValue = cumulatedCommissions2FiatValue + entry.getValue().getFiatCommissions2Value();
                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1FiatValue));
                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2FiatValue));
                        }

                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue()));

                    }
                }

                if (this.mainView.plotCommissions1.getData().size() == 1) {
                    this.mainView.plotCommissions1.getData().remove(0);
                }

                if (this.mainView.plotCommissions2.getData().size() == 1) {
                    this.mainView.plotCommissions2.getData().remove(0);
                }

                this.mainView.plotCommissions1.getData().add(rewardsCumulated1);

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                this.mainView.plotCommissions2.getData().add(rewardsCumulated2);
                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }


            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
            this.poolPairList.clear();
            this.poolPairList.addAll(this.poolPairModelList);
        }

    }

    public ObservableList<TransactionModel> getTransactionTable() {
        return this.transactionController.getTransactionList();
    }

    public ObservableList<PoolPairModel> getPlotData() {
        return this.poolPairList;
    }

    public void exportTransactionToExcel(List<TransactionModel> list) {

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );
        fileChooser.setInitialDirectory(new File(this.settingsController.lastExportPath));
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileChooser.setInitialFileName(dateFormat.format(date)+"_Portfolio_Export_RawData");
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportTransactionToExcel(list, selectedFile.getPath(), localeDecimal, this.settingsController.selectedSeperator.getValue());
            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            } else {
                this.strProgressbar.setValue("Error while exporting excel!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            }
        }
    }

    public void exportPoolPairToExcel(List<PoolPairModel> list, String source) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );
        fileChooser.setInitialDirectory(new File(this.settingsController.lastExportPath));
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileChooser.setInitialFileName(dateFormat.format(date)+"_Portfolio_Export_"+this.mainView.tabPane.getSelectionModel().getSelectedItem().getText());
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportPoolPairToExcel(list, selectedFile.getPath(), source, this.mainView);

            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            } else {
                this.strProgressbar.setValue("Error while exporting excel!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            }
        }
    }

    public void openBlockChainExplorer(TransactionModel model) {
        try {
            Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + model.getBlockHashValue()).toURI());
        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }
    }

}
