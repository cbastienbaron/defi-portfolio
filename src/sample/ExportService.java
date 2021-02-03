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

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, CoinPriceModel coinPrices, String fiatCurrency, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Owner,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Block Hash,Block Height,Pool ID".replace(",", exportSplitter)).append("\n");

            for (TransactionModel transaction : transactions) {

                for (int i = 0; i < transaction.getAmountValue().length; i++) {


                    sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTimeValue())).append(exportSplitter);
                    sb.append(transaction.getOwnerValue()).append(exportSplitter);
                    sb.append(transaction.getTypeValue()).append(exportSplitter);
                    String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.getAmountValue()[i]);
                    sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(exportSplitter);
                    sb.append(CoinsAndAmounts[1]).append(exportSplitter);
                    if(CoinsAndAmounts[1] + this.settingsController.selectedFiatCurrency.getValue() ==""){

                    }
                    var price = this.coinPriceController.getPriceFromTimeStamp(CoinsAndAmounts[1] + this.settingsController.selectedFiatCurrency.getValue(), transaction.getBlockTime().getValue() * 1000L);
                    sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]) * price)).append(exportSplitter);
                    sb.append(fiatCurrency).append(exportSplitter);
                    sb.append(transaction.getBlockHashValue()).append(exportSplitter);
                    sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
                    sb.append(transaction.getPoolIDValue());
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

    public boolean exportPoolPairToExcel(List<PoolPairModel> poolPairModelList, String exportPath, CoinPriceModel coinPrices, String fiatCurrency, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Total in Fiat,Rewards,Crypto 1,Crypto 2".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");

            for (PoolPairModel poolPairModel : poolPairModelList) {
                    sb.append(poolPairModel.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPairModel.getFiatValue().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPairModel.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPairModel.getCryptoValue1().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPairModel.getCryptoValue2().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append("\n");
            }
            writer.write(sb.toString());
            writer.close();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }
}