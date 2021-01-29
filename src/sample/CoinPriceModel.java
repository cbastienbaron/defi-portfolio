package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceModel implements java.io.Serializable {

    public String lastTimeStamp;

    TreeMap<String, List<List<String>>> coinPriceList;

    public CoinPriceModel() {

        this.coinPriceList = new TreeMap<>();

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
