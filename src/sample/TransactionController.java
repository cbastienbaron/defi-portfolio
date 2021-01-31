package sample;

import com.google.gson.Gson;
import okhttp3.Address;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;

public class TransactionController {

    URL url;
    URLConnection conn;
    String strCookieOath;
    String strCliPath;
    public List<TransactionModel> transactionList;
    public List<TransactionModel> addressModelList;
    String strTransactionData;
    int localBlockCount;
    SettingsController settingsController;
    OutputStreamWriter wr;
    public CoinPriceController coinPriceController;

    public TransactionController(String transactionData, SettingsController settingsController, CoinPriceController coinPriceController, String strCliPath, String strCookiePath) {

        this.strTransactionData = transactionData;
        this.settingsController = settingsController;
        this.coinPriceController = coinPriceController;
        this.transactionList = getLocalTransactionList();
        this.localBlockCount = getLocalBlockCount();
        this.strCliPath = strCliPath;
        this.strCookieOath = strCookiePath;
        //this.addressModelList = getLocalTransactionList();
    }

    public void initCrpConnection() {
        try {
            String strCookieData = "";

            if (new File(this.strCookieOath).exists()) {

                try {
                    this.url = new URL("http://127.0.0.1:8554");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                this.conn = url.openConnection();

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        strCookieOath));
                String line = reader.readLine();
                String[] kvpSplit = line.split(":");
                if (Arrays.stream(kvpSplit).count() == 2) {
                    strCookieData = kvpSplit[0] + ":" + kvpSplit[1];
                }
                reader.close();

                String basicAuth = "Basic " + new String(Base64.getEncoder().encode((strCookieData.getBytes())));
                conn.setRequestProperty("Authorization", basicAuth);
                conn.setRequestProperty("Content-Type", "application/json-rpc");
                conn.setDoOutput(true);
                wr = new OutputStreamWriter(conn.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public int getBlockCountRpc() {

        JSONObject jsonObject = getRpcResponse("{\"method\": \"getblockcount\"}");

        if (jsonObject.get("result") != null) {

            return Integer.parseInt(jsonObject.get("result").toString());

        } else {

            return 0;
        }
    }

    public int getBlockCountCli() {
        try {
            Process p;
            StringBuilder processOutput = new StringBuilder();
            p = Runtime.getRuntime().exec(strCliPath + " getblockcount");


            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine).append(System.lineSeparator());
                }

                p.waitFor();

                return Integer.parseInt(processOutput.toString().trim());

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getAccountHistoryCountRpc() {

        JSONObject jsonObject = getRpcResponse("{\"method\": \"accounthistorycount\",\"params\":[\"mine\"]}");
        return Integer.parseInt(jsonObject.get("result").toString());
    }

    public List<AddressModel> getListAddressGroupingsRpc() {
        List<AddressModel> addressList = new ArrayList<>();
        JSONObject jsonObject = getRpcResponse("{\"method\": \"listaddressgroupings\"}");
        JSONArray transactionJson = (JSONArray) jsonObject.get("result");
        for (var transaction : (JSONArray) transactionJson.get(0)) {
            addressList.add(getListReceivedByAddress(((JSONArray) transaction).get(0).toString()));
        }
        return addressList;
    }

    public AddressModel getListReceivedByAddress(String address) {

        AddressModel addressModel = null;
        JSONObject jsonObject = getRpcResponse("{\"method\": \"listreceivedbyaddress\",\"params\":[1, false, false, \"" + address + "\"]}");

        if (((JSONArray) jsonObject.get("result")).size() > 0) {
            JSONObject jArray = (JSONObject) (((JSONArray) jsonObject.get("result"))).get(0);
            String[] arr = new String[((JSONArray) (jArray.get("txids"))).size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (String) ((JSONArray) (jArray.get("txids"))).get(i);
            }
            return new AddressModel((String) jArray.get("address"), Double.parseDouble(jArray.get("amount").toString()), Long.parseLong(jArray.get("confirmations").toString()), (String) jArray.get("label"), arr);
        }
        return addressModel;
    }


    public int getAccountHistoryCountCli() {
        try {
            Process p;
            StringBuilder processOutput = new StringBuilder();
            p = Runtime.getRuntime().exec(strCliPath + " accounthistorycount mine");


            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine).append(System.lineSeparator());
                }
                p.waitFor();

                return Integer.parseInt(processOutput.toString().trim());

            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<TransactionModel> getListAccountHistoryRpc(int depth) {

        List<TransactionModel> transactionList = new ArrayList<>();
        JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"mine\", {\"depth\":" + depth + ",\"no_rewards\":" + false + ",\"limit\":" + depth * 2000 + "}]}");

        JSONArray transactionJson = (JSONArray) jsonObject.get("result");

        for (Object transaction : transactionJson
        ) {
            var transactionJ = (JSONObject) transaction;
            if (transactionJ.get("poolID") != null) {
                transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), new String[]{transactionJ.get("amounts").toString().replace("[", "").replace("]", "").replace("\"", "")}, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), transactionJ.get("poolID").toString(), "", this));
            } else {
                transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), new String[]{transactionJ.get("amounts").toString().replace("[", "").replace("]", "").replace("\"", "")}, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), "", transactionJ.get("txid").toString(), this));
            }
        }

        return transactionList;
    }

    public List<TransactionModel> getListAccountHistoryCli(int depth) {
        try {
            Gson gson = new Gson();
            StringBuilder processOutput;
            Process p;
            p = Runtime.getRuntime().exec(strCliPath + " listaccounthistory mine {\\\"depth\\\":" + depth + ",\\\"no_rewards\\\":" + false + ",\\\"limit\\\":" + depth * 2000 + "}");

            processOutput = new StringBuilder();

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine).append(System.lineSeparator());
                }
                p.waitFor();

                String jsonTransaction = processOutput.toString().trim();

                TransactionModel[] transactionsNew = gson.fromJson(jsonTransaction, TransactionModel[].class);

                List<TransactionModel> transactionListNew = Arrays.asList(transactionsNew);

                return new ArrayList<>(transactionListNew);

            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getRpcResponse(String requestJson) {
        try {
            initCrpConnection();

            if (conn != null & wr != null) {

                wr.write(requestJson);
                wr.flush();
                wr.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder jsonText = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    jsonText.append(line);
                }
                rd.close();

                Object obj = JSONValue.parse(jsonText.toString());
                return (JSONObject) obj;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    public List<TransactionModel> getLocalTransactionList() {

        File strPortfolioData = new File(this.strTransactionData);
        List<TransactionModel> transactionList = new ArrayList<>();

        if (strPortfolioData.exists()) {

            try {

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        strPortfolioData));
                String line = reader.readLine();

                while (line != null) {
                    String[] transactionSplit = line.split(";");
                    TransactionModel transAction = new TransactionModel(Long.parseLong(transactionSplit[0]), transactionSplit[1], transactionSplit[2], new String[]{transactionSplit[3]}, transactionSplit[4], Integer.parseInt(transactionSplit[5]), transactionSplit[6], transactionSplit[7], this);
                    transactionList.add(transAction);
                    line = reader.readLine();
                }
                reader.close();
                return transactionList;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return transactionList;
    }

    public int getLocalBlockCount() {
        if (transactionList.size() > 0) {

            return this.transactionList.get(transactionList.size() - 1).getBlockHeightValue();
        } else {
            return 0;
        }
    }

    public boolean startDefidExe() {
        //TODO: Start defid.exe
        //String strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
        return true;
    }

    public boolean updateTransactionData(int depth) {

        var transactionListNew = getListAccountHistoryRpc(depth);
        List<TransactionModel> updateTransactionList = new ArrayList<>();

        for (int i = transactionListNew.size() - 1; i >= 0; i--) {
            if (transactionListNew.get(i).getBlockHeightValue() > this.localBlockCount) {
                this.transactionList.add(transactionListNew.get(i));
                updateTransactionList.add(transactionListNew.get(i))
                ;
            }
        }

        if (updateTransactionList.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(this.strTransactionData, true));
                StringBuilder sb = new StringBuilder();
                String exportSplitter = ";";

                for (TransactionModel transactionModel : updateTransactionList) {

                    for (int i = 0; i < transactionModel.getAmountValue().length; i++) {
                        sb.append(transactionModel.getBlockTimeValue()).append(exportSplitter);
                        sb.append(transactionModel.getOwnerValue()).append(exportSplitter);
                        sb.append(transactionModel.getTypeValue()).append(exportSplitter);
                        sb.append(transactionModel.getAmountValue()[i]).append(exportSplitter);
                        sb.append(transactionModel.getBlockHashValue()).append(exportSplitter);
                        sb.append(transactionModel.getBlockHeightValue()).append(exportSplitter);
                        sb.append(transactionModel.getPoolIDValue()).append(exportSplitter);
                        if (transactionModel.getTxIDValue().equals(""))
                            sb.append("\"\"");
                        else
                            sb.append(transactionModel.getTxIDValue());

                        sb.append("\n");
                    }
                }
                writer.write(sb.toString());
                writer.close();

                this.localBlockCount = this.transactionList.get(this.transactionList.size() - 1).getBlockHeightValue();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<TransactionModel> getTransactionsInTime(List<TransactionModel> transactions, long startTime, long endTime) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getBlockTimeValue() >= startTime && transactions.get(ilist).getBlockTimeValue() <= endTime) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsOfType(List<TransactionModel> transactions, String type) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeValue().equals(type)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsOfOwner(List<TransactionModel> transactions, String owner) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeValue().equals(owner)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsBetweenBlocks(List<TransactionModel> transactions, long startBlock, long endBlock) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getBlockHeightValue() >= startBlock && transactions.get(ilist).getBlockHeightValue() <= endBlock) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public TreeMap getCryptoMap(List<TransactionModel> transactions, String intervall, int poolPairCount, String poolPair, String type, String plotCurrency) {

        TreeMap<String, Double> map = new TreeMap<>();
        TreeMap<String, Double> sorted = new TreeMap<>();
        for (TransactionModel item : transactions) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(item.getBlockTimeValue() * 1000L);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String[] AmountCoin = item.getAmountValue();
            String pool = "BTC-DFI";

            switch (item.getPoolIDValue()) {
                case "4":
                    pool = "ETH-DFI";
                    break;
                case "5":
                    pool = "BTC-DFI";
                    break;
                case "6":
                    pool = "USDT-DFI";
                    break;
                case "8":
                    pool = "DOGE-DFI";
                    break;
                default:
                    break;
            }
            for (int iAmount = 0; iAmount < AmountCoin.length; iAmount++) {
                String[] coinValue = AmountCoin[iAmount].split("@");

                if (coinValue[1].equals(poolPair.split("-")[poolPairCount]) & item.getTypeValue().equals(type) & poolPair.equals(pool)) {
                    String date = "";
                    if (intervall.equals("Daily")) {
                        if (day < 10) {
                            date = year + "-" + month + "-0" + day;
                        } else {
                            date = year + "-" + month + "-" + day;
                        }

                    } else if (intervall.equals("Monthly")) {
                        if (month < 10) {
                            date = year + "-0" + month;
                        } else {
                            date = year + "-" + month;
                        }
                    } else if (intervall.equals("Weekly")) {
                        int correct = 0;
                        if(month == 1 && (day == 1 || day == 2 || day == 3)){
                            correct = 1;
                        }
                        if (week < 10){
                            date = year-correct + "-0" + week;
                        }else{
                            date = year-correct + "-" + week;
                        }
                    } else if (intervall.equals("Yearly")) {
                        date = year + "-";
                    }

                    if (map.keySet().contains(date)) {
                        Double oldValue = map.get(date);

                        Double newValue = oldValue + (Double.parseDouble(coinValue[0]));
                        map.put(date, newValue);
                    } else {
                        map.put(date, Double.parseDouble(coinValue[0]));
                    }


                    if (item.getTypeValue().equals(type)) {
                        double fiatPrice = 1;
                        if(plotCurrency.equals("Fiat")){
                            fiatPrice = this.coinPriceController.getPriceFromTimeStamp(coinValue[1].equals(poolPair.split("-")[poolPairCount]) + this.settingsController.selectedFiatCurrency.getValue(), item.getBlockTimeValue()*1000L);
                        }
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);

                            Double newValue = oldValue + (Double.parseDouble(coinValue[0])*fiatPrice);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(coinValue[0])*fiatPrice);
                        }
                    }
                }
            }
        }
        sorted.putAll(map);
        return sorted;
    }

    public String convertTimeStampToString(long timeStamp) {
        Date date = new Date(timeStamp * 1000L);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.format(date);
    }

    public String convertTimeStampWithoutTimeToString(long timeStamp) {
        Date date = new Date(timeStamp * 1000L);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public double getTotalCoinAmount(List<TransactionModel> transactions, String coinName) {

        double amountCoin = 0;

        for (TransactionModel transaction : transactions) {

            for (int i = 0; i < transaction.getAmountValue().length; i++) {

                String[] CoinsAndAmounts = splitCoinsAndAmounts(transaction.getAmountValue()[i]);

                if (coinName.equals(CoinsAndAmounts[1])) {
                    amountCoin += Double.parseDouble(CoinsAndAmounts[0]);
                }

            }

        }
        return amountCoin;
    }

    public String[] splitCoinsAndAmounts(String amountAndCoin) {
        return amountAndCoin.split("@");
    }
}