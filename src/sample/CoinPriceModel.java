package sample;

import java.util.ArrayList;
import java.util.List;

public class CoinPriceModel implements java.io.Serializable {

    public String lastTimeStamp;

    public List<List<String>> dfiEurList;
    public List<List<String>> btcEurList;
    public List<List<String>> ethEurList;
    public List<List<String>> usdtEurList;
    public List<List<String>> ltcEurList;
    public List<List<String>> bchEurList;

    public List<List<String>> dfiUsdList;
    public List<List<String>> btcUsdList;
    public List<List<String>> ethUsdList;
    public List<List<String>> ltcUsdList;
    public List<List<String>> bchUsdList;
    public List<List<String>> usdtUsdList;

    public List<List<String>> dfiChfList;
    public List<List<String>> btcChfList;
    public List<List<String>> ethChfList;
    public List<List<String>> ltcChfList;
    public List<List<String>> bchChfList;
    public List<List<String>> usdtChfList;

    public List<List<String>> dfiBtcList;
    public List<List<String>> btcBtcList;
    public List<List<String>> ethBtcList;
    public List<List<String>> ltcBtcList;
    public List<List<String>> bchBtcList;

    public CoinPriceModel() {

        this.dfiEurList = new ArrayList<>();
        this.btcEurList = new ArrayList<>();
        this.ethEurList = new ArrayList<>();
        this.ltcEurList = new ArrayList<>();
        this.bchEurList = new ArrayList<>();
        this.usdtEurList = new ArrayList<>();

        this.dfiUsdList = new ArrayList<>();
        this.btcUsdList = new ArrayList<>();
        this.ethUsdList = new ArrayList<>();
        this.ltcUsdList = new ArrayList<>();
        this.bchUsdList = new ArrayList<>();
        this.usdtUsdList = new ArrayList<>();

        this.dfiChfList = new ArrayList<>();
        this.btcChfList = new ArrayList<>();
        this.ethChfList = new ArrayList<>();
        this.ltcChfList = new ArrayList<>();
        this.bchChfList = new ArrayList<>();
        this.usdtChfList = new ArrayList<>();

        this.dfiBtcList = new ArrayList<>();
        this.btcBtcList = new ArrayList<>();
        this.ethBtcList = new ArrayList<>();
        this.ltcBtcList = new ArrayList<>();
        this.bchBtcList = new ArrayList<>();

        lastTimeStamp = "1589179630";

        dfiEurList.add(new ArrayList<>() {
            {
                add("1589179630000");
                add("0.092523");
            }
        });

        dfiUsdList.add(new ArrayList<>() {
            {
                add("1589179630000");
                add("0.1");
            }
        });

        dfiChfList.add(new ArrayList<>() {
            {
                add("1589179630000");
                add("0.0973");
            }
        });

        dfiBtcList.add(new ArrayList<>() {
            {
                add("1602867600000");
                add("1.866e-05");
            }
        });
    }

    public List<List<String>> GetDfiList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.dfiEurList;
        if (fiatCurrency.equals("USD"))
            return this.dfiUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.dfiChfList;
        if (fiatCurrency.equals("BTC"))
            return this.dfiBtcList;

        return this.dfiUsdList;
    }

    public List<List<String>> GetBtcList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.btcEurList;
        if (fiatCurrency.equals("USD"))
            return this.btcUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.btcChfList;
        if (fiatCurrency.equals("BTC"))
            return this.btcBtcList;

        return this.btcEurList;
    }

    public List<List<String>> GetEthList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.ethEurList;
        if (fiatCurrency.equals("USD"))
            return this.ethUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.ethChfList;
        if (fiatCurrency.equals("BTC"))
            return this.ethBtcList;

        return this.ethEurList;
    }

    public List<List<String>> GetUsdtList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.usdtEurList;
        if (fiatCurrency.equals("USD"))
            return this.usdtUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.usdtChfList;

        return this.usdtEurList;
    }

    public List<List<String>> GetLtcList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.ltcEurList;
        if (fiatCurrency.equals("USD"))
            return this.ltcUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.ltcChfList;
        if (fiatCurrency.equals("BTC"))
            return this.ltcBtcList;

        return this.ltcEurList;
    }

    public List<List<String>> GetBchList(String fiatCurrency) {
        if (fiatCurrency.equals("EUR"))
            return this.bchEurList;
        if (fiatCurrency.equals("USD"))
            return this.bchUsdList;
        if (fiatCurrency.equals("CHF"))
            return this.bchChfList;
        if (fiatCurrency.equals("BTC"))
            return this.bchBtcList;

        return this.bchEurList;
    }
}
