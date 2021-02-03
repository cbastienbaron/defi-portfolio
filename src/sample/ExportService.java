package sample;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class ExportService {

    CoinPriceController coinPriceController;
    TransactionController transactionController;
    SettingsController settingsController;

    public ExportService(CoinPriceController coinPriceController, TransactionController transactionController,SettingsController settingsController) {
        this.settingsController = settingsController;
        this.coinPriceController = coinPriceController;
        this.transactionController = transactionController;

    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, String fiatCurrency, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,LM Pool,Block Height,Block Hash,Owner").append("\n");

            for (TransactionModel transaction : transactions) {

                for (int i = 0; i < transaction.getAmountValue().length; i++) {
                    sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTimeValue())).append(exportSplitter);
                    sb.append(transaction.getTypeValue()).append(exportSplitter);
                    sb.append(String.format(localeDecimal, "%.8f", transaction.getCryptoValueValue())).append(exportSplitter);
                    sb.append(transaction.getCryptoyValue()).append(exportSplitter);
                    sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue().doubleValue())).append(exportSplitter);
                    sb.append(transaction.getFiatCurrencyValue()).append(exportSplitter);
                    sb.append(transaction.getPoolIDValue()).append(exportSplitter);
                    sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
                    sb.append(transaction.getBlockHashValue()).append(exportSplitter);
                    sb.append(transaction.getOwnerValue()).append(exportSplitter);
                    sb.append("\n");


                }
            }
            writer.write(sb.toString());
            writer.close();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean exportPoolPairToExcel(List<PoolPairModel> poolPairModelList, String exportPath,String exportSplitter,String source) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            switch (source){
                case "Overview":
                    sb.append("Date,Pair, Total,Rewards,Commissions".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");

                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getFiatValue().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValue1().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValue2().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Rewards":
                    sb.append("Date,Pair,Total,Rewards, Commissions".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");

                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValue1().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValueValue2()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;

                case "Commissions":
                    sb.append("Date,Pair,Total Fiat,DFI in Fiat".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");

                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getFiatValueValue()+poolPairModel.getCryptoValue2().getValue()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValueValue1()).append(this.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getCryptoValueValue2()).append(this.settingsController.selectedSeperator.getValue());
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