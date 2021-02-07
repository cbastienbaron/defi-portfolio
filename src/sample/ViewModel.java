package sample;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Timer;

public class ViewModel {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("No connection");
    public StringProperty strLastUpdate = new SimpleStringProperty("-");
    public StringProperty strProgressbar = new SimpleStringProperty("");

    //View
    public View view;

    //Table and plot lists
    public List<PoolPairModel> poolPairModelList = new ArrayList<>();
    public ObservableList<TransactionModel> transactionList;
    public ObservableList<PoolPairModel> poolPairList;

    //Init all controller and services
    public SettingsController settingsController = SettingsController.getInstance();
    public CoinPriceController coinPriceController = new CoinPriceController(this.settingsController.strPathAppData + this.settingsController.strCoinPriceData);
    public TransactionController transactionController = new TransactionController(this.settingsController.strPathAppData + this.settingsController.strTransactionData, this.settingsController, this.coinPriceController, this.settingsController.strCookiePath, this.settingsController.strPathDefid);
    public ExportService expService;

    public ViewModel() {

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            //Betriebssystem ist Windows-basiert
        } else if (os.contains("osx")) {
            //Betriebssystem ist Apple OSX
        } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
            //Betriebssystem ist Linux/Unix basiert
        }

        // generate folder //defi-portfolio if no one exists
        File directory = new File(this.settingsController.strPathAppData);
        if (!directory.exists()) {
            directory.mkdir();
        }
        // init all relevant lists for tables and plots
        this.transactionList = FXCollections.observableArrayList(this.transactionController.getTransactionList());
        this.poolPairList = FXCollections.observableArrayList(this.poolPairModelList);
        this.expService = new ExportService(this.coinPriceController, this.transactionController, this.settingsController);

        // get last block locally
        this.strCurrentBlockLocally.set(Integer.toString(transactionController.getLocalBlockCount()));

        //start timer for getting last block on blockchain
        startTimer();

        //Add listener to Fiat
        this.settingsController.selectedFiatCurrency.addListener(
                (ov, t, t1) -> {
                    for (TransactionModel transactionModel:this.transactionController.getTransactionList()) {
                        transactionModel.setFiatCurrency(t);
                        transactionModel.setFiatValue(this.coinPriceController.getPriceFromTimeStamp(transactionModel.getCryptoCurrencyValue()+t,transactionModel.getBlockTimeValue()*1000L));
                        //TODO Portfolio clear and   this.transactionController.addPortfoli...
                    }
                    this.transactionList.clear();
                    this.transactionList.addAll(this.transactionController.getTransactionList());

                }
        );

    }

    public void startTimer() {
        TimerController timerController = new TimerController(this);
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(timerController, 0, 30000L);
    }

    public void copySelectedRawDataToClipboard(List<TransactionModel> list, boolean withHeaders) {
        StringBuilder sb = new StringBuilder();

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            sb.append("Date,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Pool ID,Block Height,Block Hash,Owner,".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
        }

        for (TransactionModel transaction : list
        ) {
            sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTime().getValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
            String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.getAmount().getValue()[0]);
            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(this.settingsController.selectedSeperator.getValue());
            sb.append(CoinsAndAmounts[1]).append(this.settingsController.selectedSeperator.getValue());
            sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(this.settingsController.selectedFiatCurrency.getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getPoolID().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHeight().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHash().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getOwner().getValue());
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
            switch (this.view.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                    sb.append((this.view.plotTable.getColumns().get(0).getText() + "," + this.view.plotTable.getColumns().get(1).getText() + "," + this.view.plotTable.getColumns().get(2).getText() + "," + this.view.plotTable.getColumns().get(3).getText() + "," + this.view.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                    sb.append((this.view.plotTable.getColumns().get(0).getText() + "," + this.view.plotTable.getColumns().get(1).getText() + "," + this.view.plotTable.getColumns().get(3).getText() + "," + this.view.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Commissions":
                    sb.append((this.view.plotTable.getColumns().get(0).getText() + "," + this.view.plotTable.getColumns().get(1).getText() + "," + this.view.plotTable.getColumns().get(2).getText() + "," + this.view.plotTable.getColumns().get(3).getText() + "," + this.view.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
        }

        for (PoolPairModel poolPair : list
        ) {
            switch (this.view.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Rewards":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Commissions":
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
        if (this.transactionController.checkCrp()) {
            if (new File(this.settingsController.strPathAppData + this.settingsController.strTransactionData).exists()) {
                int depth = Integer.parseInt(this.transactionController.getBlockCountRpc()) - this.transactionController.getLocalBlockCount();
                return this.transactionController.updateTransactionData(depth);
            } else {
                return this.transactionController.updateTransactionData(this.transactionController.getAccountHistoryCountRpc());
            }
        } else {
              return false;
        }

    }

    public boolean checkIfDeFiAppIsRunning() {
        String line;
        StringBuilder pidInfo = new StringBuilder();
        Process p;

        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");


            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pidInfo.toString().contains("defi-app");
    }

    public void btnUpdateDatabasePressed() {

        if (!checkIfDeFiAppIsRunning()) {

            if (updateTransactionData()) {
                this.strCurrentBlockLocally.set(Integer.toString(this.transactionController.getLocalBlockCount()));
                this.strCurrentBlockOnBlockchain.set(this.transactionController.getBlockCountRpc());

                transactionList.clear();
                transactionList.addAll(this.transactionController.getTransactionList());
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                this.strLastUpdate.setValue(dateFormat.format(date));


            } else {
                JOptionPane.showMessageDialog(new JFrame(), "The Defid.exe is not running! Please start it manually.", "Defid.exe not running", JOptionPane.WARNING_MESSAGE);
                this.strCurrentBlockOnBlockchain.set("No connection");
            }
        } else {
            //TODO Defi App is running cant update
        }
    }

    public void plotUpdate(String openedTab) {
        switch (openedTab) {
            case "Overview":
                updateOverview();
                break;
            case "Rewards":
                updateRewards();
                break;
            case "Commissions":
                updateCommissions();
                break;
            default:
                break;
        }
    }

    public void updateOverview() {

//TODO klappt noch nicht wie es soll?

        this.poolPairModelList.clear();
        this.view.plotOverview.setLegendVisible(true);
        this.view.plotOverview.getData().clear();
        this.view.plotOverview.getYAxis().setLabel("Total (" + this.settingsController.selectedFiatCurrency.getValue() + ")");


        double maxValue = 0;

        for (String poolPair : this.settingsController.cryptoCurrencies) {

            XYChart.Series<Number, Number> overviewSeries = new XYChart.Series();
            overviewSeries.setName(poolPair);

            if (this.transactionController.getPortfolioList().containsKey(poolPair + "-" + this.settingsController.selectedIntervall.getValue())) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(poolPair + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {

                    if (poolPair.equals(entry.getValue().getPoolPairValue())) {
                        overviewSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value()));
                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getFiatRewards1Value(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), poolPair));
                    }
                }

                this.view.yAxis.setAutoRanging(false);
                if (maxValue < overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble()) {
                    this.view.yAxis.setUpperBound(overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble() * 1.10);
                    maxValue = overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble();
                }
                this.view.plotOverview.getData().add(overviewSeries);
                this.view.plotOverview.setCreateSymbols(true);
            }

        }
        for (XYChart.Series<Number, Number> s : this.view.plotOverview.getData()) {
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
        this.view.plotRewards.setLegendVisible(false);
        this.view.plotRewards.getData().clear();

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.view.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
        } else {
            this.view.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue())) {

            if (this.settingsController.selectedPlotType.getValue().equals("Individual")) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {
                    if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                        rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinRewards1Value()));
                    } else {
                        rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value()));
                    }
                    this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", 1, entry.getValue().getCoinRewards1Value(), entry.getValue().getFiatRewards1Value(), this.settingsController.selectedCoin.getValue()));
                }

                this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
                this.poolPairList.clear();
                this.poolPairList.addAll(this.poolPairModelList);

                if (this.view.plotRewards.getData().size() == 1) {
                    this.view.plotRewards.getData().remove(0);
                }

                this.view.plotRewards.getData().add(rewardsSeries);

                for (XYChart.Series<Number, Number> s : this.view.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }
            } else {

                ArrayList<PortfolioModel> valueList = new ArrayList<>(this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).values());

                XYChart.Series<Number, Number> rewardsCumulated = new XYChart.Series();

                double cumulatedCoinValue = 0;
                double cumulatedFiatValue = 0;
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {

                    if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                        cumulatedCoinValue = cumulatedCoinValue + entry.getValue().getCoinRewards1Value();
                        rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedCoinValue));
                    } else {
                        cumulatedFiatValue = cumulatedFiatValue + entry.getValue().getFiatRewards1Value();
                        rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedFiatValue));
                    }
                }
                if (this.view.plotRewards.getData().size() == 1) {
                    this.view.plotRewards.getData().remove(0);
                }

                this.view.plotRewards.getData().add(rewardsCumulated);

                for (XYChart.Series<Number, Number> s : this.view.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }
        }

    }

    public void updateCommissions() {

        XYChart.Series<Number, Number> commissionsSeries1 = new XYChart.Series();
        XYChart.Series<Number, Number> commissionsSeries2 = new XYChart.Series();
        this.view.plotCommissions1.getData().clear();
        this.view.plotCommissions2.getData().clear();
        this.poolPairModelList.clear();
        this.poolPairList.clear();
        this.view.plotCommissions1.setLegendVisible(false);
        this.view.plotCommissions2.setLegendVisible(false);

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.view.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
            this.view.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0]);
        } else {
            this.view.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
            this.view.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue())) {

            if (this.settingsController.selectedPlotType.getValue().equals("Individual")) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {

                    if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                        commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions1Value()));
                        commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions2Value()));
                    } else {
                        commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions1Value()));
                        commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions2Value()));
                    }
                    this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue()));
                }

                this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
                this.poolPairList.clear();
                this.poolPairList.addAll(this.poolPairModelList);

                this.view.plotCommissions1.getData().add(commissionsSeries1);
                this.view.plotCommissions2.getData().add(commissionsSeries2);

                for (XYChart.Series<Number, Number> s : this.view.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                for (XYChart.Series<Number, Number> s : this.view.plotCommissions2.getData()) {
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
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {

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
                }

                if (this.view.plotCommissions1.getData().size() == 1) {
                    this.view.plotCommissions1.getData().remove(0);
                }

                if (this.view.plotCommissions2.getData().size() == 1) {
                    this.view.plotCommissions2.getData().remove(0);
                }

                this.view.plotCommissions1.getData().add(rewardsCumulated1);

                for (XYChart.Series<Number, Number> s : this.view.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                this.view.plotCommissions2.getData().add(rewardsCumulated2);
                for (XYChart.Series<Number, Number> s : this.view.plotCommissions2.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }
        }

    }

    public ObservableList<TransactionModel> getTransactionTable() {
        return this.transactionList;
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
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportTransactionToExcel(list, selectedFile.getPath(), this.settingsController.selectedFiatCurrency.getValue(), localeDecimal, this.settingsController.selectedSeperator.getValue());
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

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportPoolPairToExcel(list, selectedFile.getPath(), this.settingsController.selectedSeperator.getValue(), source, this.view);

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
            e.printStackTrace();
        }
    }

}
