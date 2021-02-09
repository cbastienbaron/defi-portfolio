package portfolio.services;

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

    CoinPriceController coinPriceController;
    TransactionController transactionController;
    SettingsController settingsController;

    public ExportService(CoinPriceController coinPriceController, TransactionController transactionController, SettingsController settingsController) {
        this.settingsController = settingsController;
        this.coinPriceController = coinPriceController;
        this.transactionController = transactionController;

    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Pool ID,Block Height,Block Hash,Owner").append("\n");

            for (TransactionModel transaction : transactions) {
                sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTimeValue())).append(exportSplitter);
                sb.append(transaction.getTypeValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getCryptoValueValue())).append(exportSplitter);
                sb.append(transaction.getCryptoCurrencyValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(exportSplitter);
                sb.append(transaction.getFiatCurrencyValue()).append(exportSplitter);
                sb.append(transaction.getPoolIDValue()).append(exportSplitter);
                sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
                sb.append(transaction.getBlockHashValue()).append(exportSplitter);
                sb.append(transaction.getOwnerValue());
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
            if (settingsController.selectedDecimal.getValue().equals(".")) {
                localeDecimal = Locale.US;
            }
            switch (mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Commissions":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
            switch (source) {
                case "Overview":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue2().getValue())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Rewards":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;

                case "Commissions":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValueValue() + poolPairModel.getCryptoValue2().getValue())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue1())).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.settingsController.selectedSeperator.getValue());
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