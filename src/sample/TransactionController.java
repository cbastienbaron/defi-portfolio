package sample;

import com.google.gson.Gson;
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
    String strCookieOath = System.getenv("APPDATA") + "\\DeFi Blockchain\\.cookie";
    String strCliPath = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public List<TransactionModel> transactionList;
    String strTransactionData;
    int localBlockCount;
    OutputStreamWriter wr;

    public TransactionController(String transactionData) {
        this.strTransactionData = transactionData;
        this.transactionList = getLocalTransactionList();
        this.localBlockCount = getLocalBlockCount();

    }

    public void initCrpConnection() {
        try {
            String strCookieData = "";
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

            return this.transactionList.get(transactionList.size() - 1).getBlockHeightProperty();
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
            if (transactionListNew.get(i).getBlockHeightProperty() > this.localBlockCount) {
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

                    for (int i = 0; i < transactionModel.getAmountProperty().length; i++) {
                        sb.append(transactionModel.getBlockTimeProperty()).append(exportSplitter);
                        sb.append(transactionModel.getOwnerProperty()).append(exportSplitter);
                        sb.append(transactionModel.getTypeProperty()).append(exportSplitter);
                        sb.append(transactionModel.getAmountProperty()[i]).append(exportSplitter);
                        sb.append(transactionModel.getBlockHashProperty()).append(exportSplitter);
                        sb.append(transactionModel.getBlockHeightProperty()).append(exportSplitter);
                        sb.append(transactionModel.getPoolIDProperty()).append(exportSplitter);
                        if (transactionModel.getTxIDProperty().equals(""))
                            sb.append("\"\"");
                        else
                            sb.append(transactionModel.getTxIDProperty());

                        sb.append("\n");
                    }
                }
                writer.write(sb.toString());
                writer.close();

                this.localBlockCount = this.transactionList.get(this.transactionList.size() - 1).getBlockHeightProperty();
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
            if (transactions.get(ilist).getBlockTimeProperty() >= startTime && transactions.get(ilist).getBlockTimeProperty() <= endTime) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsOfType(List<TransactionModel> transactions, String type) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeProperty().equals(type)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsOfOwner(List<TransactionModel> transactions, String owner) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeProperty().equals(owner)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public List<TransactionModel> getTransactionsBetweenBlocks(List<TransactionModel> transactions, long startBlock, long endBlock) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getBlockHeightProperty() >= startBlock && transactions.get(ilist).getBlockHeightProperty() <= endBlock) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static TreeMap getRewardsJoined(List<TransactionModel> transactions, String intervall , String Coin){
        TreeMap<String, Double> map = new TreeMap<>();
        TreeMap<String, Double> sorted = new TreeMap<>();
        for(TransactionModel item : transactions){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(item.getBlockTimeProperty()*1000L);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String[] AmountCoin = item.getAmountProperty();

            for(int iAmount = 0 ;iAmount<AmountCoin.length;iAmount++) {
                String[] coinValue = AmountCoin[iAmount].split("@");
                if (coinValue[1].equals(Coin)) {
                    String date = "";
                    if(intervall.equals("Daily")) {
                        date = year + "-" + month + "-" + day;
                    }
                    else if(intervall.equals("Monthly")) {
                        date = year + "-" + month;
                    }
                    else if(intervall.equals("Weekly")) {
                        date = year + "-" + week;
                    }
                    else if(intervall.equals("Yearly")) {
                        date = year + "-";
                    }

                    if (item.getTypeProperty().equals("Rewards")) {
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);
                            Double newValue = oldValue + Double.parseDouble(coinValue[0]);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(coinValue[0]));
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

            for (int i = 0; i < transaction.getAmountProperty().length; i++) {

                String[] CoinsAndAmounts = splitCoinsAndAmounts(transaction.getAmountProperty()[i]);

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