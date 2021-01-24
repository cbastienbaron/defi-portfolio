package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class ExportService {

    CoinPriceController coinPriceController;

    public ExportService(CoinPriceController coinPriceController) {
        this.coinPriceController = coinPriceController;
    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, CoinPriceModel coinPrices, String fiatCurrency, Locale localeDecimal, String exportSplitter, Long TimeStampStart, Long TimeStampEnd) {
        try {
            PrintWriter writer = new PrintWriter(new File(exportPath));
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Owner,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Block Hash,Block Height,Pool ID".replace(",", exportSplitter) + "\n");

            for (int iTransaction = 0; iTransaction < transactions.size(); iTransaction++) {

                for (int i = 0; i < transactions.get(iTransaction).getAmountProperty().length; i++) {

                    if ((transactions.get(iTransaction).getBlockTimeProperty() >= TimeStampStart) && (transactions.get(iTransaction).getBlockTimeProperty() <= TimeStampEnd)) {

                        sb.append(TransactionController.convertTimeStampToString(transactions.get(iTransaction).getBlockTimeProperty()) + exportSplitter);
                        sb.append(transactions.get(iTransaction).getOwnerProperty() + exportSplitter);
                        sb.append(transactions.get(iTransaction).getTypeProperty() + exportSplitter);
                        String[] CoinsAndAmounts = TransactionController.splitCoinsAndAmounts(transactions.get(iTransaction).getAmountProperty()[i].toString());
                        sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0])) + exportSplitter);
                        sb.append(CoinsAndAmounts[1] + exportSplitter);

                        List<List<String>> coinPriceList = null;

                        switch (CoinsAndAmounts[1]) {
                            case "DFI":
                                coinPriceList = coinPrices.GetDfiList(fiatCurrency);
                                break;
                            case "BTC":
                                coinPriceList = coinPrices.GetBtcList(fiatCurrency);
                                break;
                            case "ETH":
                                coinPriceList = coinPrices.GetEthList(fiatCurrency);
                                break;
                            case "USDT":
                                coinPriceList = coinPrices.GetUsdtList(fiatCurrency);
                                break;
                            case "LTC":
                                coinPriceList = coinPrices.GetLtcList(fiatCurrency);
                                break;
                            case "BCH":
                                coinPriceList = coinPrices.GetBchList(fiatCurrency);
                                break;
                            default:
                                break;
                        }

                        if (coinPriceList != null) {
                            var price = this.coinPriceController.getPriceFromTimeStamp(coinPriceList, transactions.get(iTransaction).getBlockTimeProperty() * 1000L);
                            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]) * price) + exportSplitter);

                        } else {
                            sb.append(exportSplitter);
                        }

                        sb.append(fiatCurrency + exportSplitter);
                        sb.append(transactions.get(iTransaction).getBlockHashProperty() + exportSplitter);
                        sb.append(transactions.get(iTransaction).getBlockHeightProperty() + exportSplitter);
                        sb.append(transactions.get(iTransaction).getPoolIDProperty());
                        sb.append("\n");

                    }
                }
            }
            writer.write(sb.toString());
            writer.close();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }
}