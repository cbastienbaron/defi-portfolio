package portfolio.services;

import javafx.scene.control.TableColumn;
import portfolio.Main;
import portfolio.controllers.MainViewController;
import portfolio.views.MainView;
import portfolio.controllers.CoinPriceController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;
import portfolio.models.PoolPairModel;
import portfolio.models.TransactionModel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class ExportService {

    MainViewController mainViewController;

    public ExportService(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            for (TableColumn column : this.mainViewController.mainView.rawDataTable.getColumns()
            ) {
                sb.append(column.getText()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
            }

            sb.setLength(sb.length() - 1);
            sb.append("\n");

            for (TransactionModel transaction : transactions) {
                sb.append(this.mainViewController.transactionController.convertTimeStampToString(transaction.getBlockTimeValue())).append(exportSplitter);
                sb.append(transaction.getTypeValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getCryptoValueValue())).append(exportSplitter);
                sb.append(transaction.getCryptoCurrencyValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(exportSplitter);
                sb.append(transaction.getFiatCurrencyValue()).append(exportSplitter);
                sb.append(transaction.getPoolIDValue()).append(exportSplitter);
                sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
                sb.append(transaction.getBlockHashValue()).append(exportSplitter);
                sb.append(transaction.getOwnerValue()).append(exportSplitter);
                sb.append(transaction.getTxIDValue());
                sb.append("\n");
            }
            writer.write(sb.toString());
            writer.close();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean exportPoolPairToExcel(List<PoolPairModel> poolPairModelList, String exportPath, String source, MainView mainView) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            Locale localeDecimal = Locale.GERMAN;
            if (this.mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                localeDecimal = Locale.US;
            }
            switch (mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Commissions":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
            switch (source) {
                case "Overview":
                case "Übersicht":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValue().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Rewards":
                case "Belohnungen":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;

                case "Commissions":
                case "Kommissionen":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValueValue() + poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue1())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
            }
            writer.write(sb.toString());
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}