package sample;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceController {

    public CoinPriceModel coinPriceModel;
    String strCoinPriceData;

    public CoinPriceController(String strCoinPriceData) {

           this.strCoinPriceData = strCoinPriceData;
        try{
        updateCoinPriceData();
       }catch(Exception e){

        }
    }

    public boolean updateCoinPriceData() {

        CoinPriceModel coinPrice = getCoinPriceLocal(this.strCoinPriceData);
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        var currentTimeStamp = new Timestamp(System.currentTimeMillis()).getTime() / 1000L;

        if (client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices().size() > 0) {

            //Update DFI
            TreeMap<String, List<List<String>>> coinPriceList = new TreeMap<>();
            coinPriceList = coinPrice.GetKeyMap();

            //Update DFI
            coinPriceList.get("DFIEUR").addAll(client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("DFIUSD").addAll(client.getCoinMarketChartRangeById("defichain", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("DFICHF").addAll(client.getCoinMarketChartRangeById("defichain", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BTC
            coinPriceList.get("BTCEUR").addAll(client.getCoinMarketChartRangeById("bitcoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("BTCUSD").addAll(client.getCoinMarketChartRangeById("bitcoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("BTCCHF").addAll(client.getCoinMarketChartRangeById("bitcoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update ETH
            coinPriceList.get("ETHEUR").addAll(client.getCoinMarketChartRangeById("ethereum", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("ETHUSD").addAll(client.getCoinMarketChartRangeById("ethereum", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("ETHCHF").addAll(client.getCoinMarketChartRangeById("ethereum", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update USDT
            coinPriceList.get("USDTEUR").addAll(client.getCoinMarketChartRangeById("tether", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("USDTUSD").addAll(client.getCoinMarketChartRangeById("tether", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("USDTCHF").addAll(client.getCoinMarketChartRangeById("tether", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update LTC
            coinPriceList.get("LTCEUR").addAll(client.getCoinMarketChartRangeById("litecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("LTCUSD").addAll(client.getCoinMarketChartRangeById("litecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("LTCCHF").addAll(client.getCoinMarketChartRangeById("litecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BCH
            coinPriceList.get("BCHEUR").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("BCHUSD").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("BCHCHF").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "chf",coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BCH
            coinPriceList.get("DOGEEUR").addAll(client.getCoinMarketChartRangeById("dogecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("DOGEUSD").addAll(client.getCoinMarketChartRangeById("dogecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.get("DOGECHF").addAll(client.getCoinMarketChartRangeById("dogecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            coinPrice.SetKeyMap(coinPriceList);
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
                this.coinPriceModel = coinPrice;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }

    public String getLastTimeStamp(String strCoinPricePath) {
        return getCoinPriceLocal(strCoinPricePath).lastTimeStamp;
    }

    public CoinPriceModel getCoinPriceLocal(String strCoinPricePath) {
        CoinPriceModel coinPrice = new CoinPriceModel();
        if (new File(strCoinPricePath).exists()) {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strCoinPricePath);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                coinPrice = (CoinPriceModel) in.readObject();
                this.coinPriceModel = coinPrice;
                in.close();
                file.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return coinPrice;
    }

    public double getPriceFromTimeStamp(String coinFiatPair, Long timeStamp) {
        double price = 0;
        if(this.coinPriceModel.GetKeyMap().get(coinFiatPair) == null){
            int a= 2;
        }
        for (int i = this.coinPriceModel.GetKeyMap().get(coinFiatPair).size() - 1; i >= 0; i--)
            if (timeStamp > Long.parseLong(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(0))) {
                return Double.parseDouble(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(1));
            }
        return price;

    }
}
