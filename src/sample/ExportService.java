package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class ExportService {

    public static boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, CoinPriceModel coinPrices, String fiatCurrency, Locale localeDecimal, String exportSplitter) {
        try {
            PrintWriter writer = new PrintWriter(new File(exportPath));
            StringBuilder sb = new StringBuilder();

            sb.append("Date,Owner,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Block Hash,Block Height,Pool ID".replace(",",exportSplitter)+"\n");

            for (int iTransaction = 0; iTransaction < transactions.size(); iTransaction++) {

                for (int i = 0; i < transactions.get(iTransaction).amounts.length; i++) {

                    sb.append(TransactionController.convertTimeStampToString(transactions.get(iTransaction).blockTime) + exportSplitter);
                    sb.append(transactions.get(iTransaction).owner + exportSplitter);
                    sb.append(transactions.get(iTransaction).type + exportSplitter);

                    String[] CoinsAndAmounts = TransactionController.splitCoinsAndAmounts(transactions.get(iTransaction).amounts[i].toString());
                    sb.append(String.format(localeDecimal, "%.18f", Double.parseDouble(CoinsAndAmounts[0])) + exportSplitter);
                    sb.append(CoinsAndAmounts[1] + exportSplitter);

                    List<List<String>> coinPriceList=null;

                    switch (CoinsAndAmounts[1] ) {
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

                    if(coinPriceList!= null) {
                        var price = getPrice(coinPriceList, transactions.get(iTransaction).blockTime * 1000L);
                        sb.append(String.format(localeDecimal, "%.18f", Double.parseDouble(CoinsAndAmounts[0]) * price) + exportSplitter);

                    }else{
                        sb.append(exportSplitter);
                    }

                    sb.append(fiatCurrency + exportSplitter);
                    sb.append(transactions.get(iTransaction).blockHash + exportSplitter);
                    sb.append(transactions.get(iTransaction).blockHeight + exportSplitter);
                    sb.append(transactions.get(iTransaction).poolID);
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

    private static double getPrice(List<List<String>> coinPrices, Long timeStamp) {
        double price=0;

        for (int i = coinPrices.size() - 1; i >= 0; i--)
                        if(timeStamp>Long.parseLong(coinPrices.get(i).get(0))){
                            price = Double.parseDouble(coinPrices.get(i).get(1));
                            break;
                        }

        return price;
    }

}
