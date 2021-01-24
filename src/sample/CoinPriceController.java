package sample;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;

public class CoinPriceController {

    public CoinPriceModel coinPriceModel= new CoinPriceModel();
    String strCoinPriceData;
    public CoinPriceController(String strCoinPriceData){
        this.strCoinPriceData = strCoinPriceData;
        coinPriceModel = getCoinPriceLocal(this.strCoinPriceData);

    }

    public boolean updateCoinPriceData() {

        CoinPriceModel coinPrice = getCoinPriceLocal(this.strCoinPriceData);
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        var currentTimeStamp = new Timestamp(System.currentTimeMillis()).getTime() / 1000L;

        if (client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices().size() > 0) {

            //Update DFI
            coinPrice.dfiEurList.addAll(client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.dfiUsdList.addAll(client.getCoinMarketChartRangeById("defichain", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.dfiChfList.addAll(client.getCoinMarketChartRangeById("defichain", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.dfiBtcList.addAll(client.getCoinMarketChartRangeById("defichain", "btc", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BTC
            coinPrice.btcEurList.addAll(client.getCoinMarketChartRangeById("bitcoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.btcUsdList.addAll(client.getCoinMarketChartRangeById("bitcoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.btcChfList.addAll(client.getCoinMarketChartRangeById("bitcoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.btcBtcList.addAll(client.getCoinMarketChartRangeById("bitcoin", "btc", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update ETH
            coinPrice.ethEurList.addAll(client.getCoinMarketChartRangeById("ethereum", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ethUsdList.addAll(client.getCoinMarketChartRangeById("ethereum", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ethChfList.addAll(client.getCoinMarketChartRangeById("ethereum", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ethBtcList.addAll(client.getCoinMarketChartRangeById("ethereum", "btc", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update USDT
            coinPrice.usdtEurList.addAll(client.getCoinMarketChartRangeById("tether", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.usdtUsdList.addAll(client.getCoinMarketChartRangeById("tether", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.usdtChfList.addAll(client.getCoinMarketChartRangeById("tether", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update LTC
            coinPrice.ltcEurList.addAll(client.getCoinMarketChartRangeById("litecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ltcUsdList.addAll(client.getCoinMarketChartRangeById("litecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ltcChfList.addAll(client.getCoinMarketChartRangeById("litecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.ltcBtcList.addAll(client.getCoinMarketChartRangeById("litecoin", "btc", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BCH
            coinPrice.bchEurList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.bchUsdList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.bchChfList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPrice.bchBtcList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "btc", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            coinPrice.lastTimeStamp = Long.toString(currentTimeStamp);


            // Serialization
            try {
                //Saving of object in a file
                FileOutputStream file = new FileOutputStream(this.strCoinPriceData);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(coinPrice);
                out.close();
                file.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            return true;
        }
        return false;
    }

    public String getLastTimeStamp(String strCoinPricePath){
        return getCoinPriceLocal(strCoinPricePath).lastTimeStamp;
    }

    public CoinPriceModel getCoinPriceLocal(String strCoinPricePath){
        CoinPriceModel coinPrice = new CoinPriceModel();
        if (new File(strCoinPricePath).exists()) {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strCoinPricePath);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                coinPrice = (CoinPriceModel) in.readObject();

                in.close();
                file.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return coinPrice;
    }

    public double getPriceFromTimeStamp(List<List<String>> coinPrices, Long timeStamp) {
        double price=0;

        for (int i = coinPrices.size() - 1; i >= 0; i--)
            if(timeStamp>Long.parseLong(coinPrices.get(i).get(0))){
                price = Double.parseDouble(coinPrices.get(i).get(1));
                break;
            }

        return price;
    }
}
