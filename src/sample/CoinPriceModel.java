package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceModel implements java.io.Serializable {

    public String lastTimeStamp;

    TreeMap<String, List<List<String>>> coinPriceList;

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

        this.coinPriceList = new TreeMap<>();

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

        List<List<String>> initialList = new ArrayList<>();
        List<String> initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.092523");
        initialList.add(initialDFIList);

        coinPriceList.put("DFIEUR",initialList);

        initialList = new ArrayList<>();
        initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.1");


        coinPriceList.put("DFIUSD",initialList);

        initialList = new ArrayList<>();
        initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.0973");

        coinPriceList.put("DFICHF",initialList);
    }

    public TreeMap<String, List<List<String>>> GetKeyMap(){
        return this.coinPriceList;
    }

    public void SetKeyMap(TreeMap<String, List<List<String>>> coinPriceList){
        this.coinPriceList=coinPriceList;
    }

}
