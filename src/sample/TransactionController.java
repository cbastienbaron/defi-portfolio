package sample;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;

public class TransactionController {

    URL url;
    URLConnection conn;
    String strCookieOath = System.getenv("APPDATA") + "\\DeFi Blockchain\\.cookie";
    String strCliPath = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public List<TransactionModel> transactionList = new ArrayList<>();
    String strTransactionData;
    public TransactionController(String transactionData){
        this.strTransactionData = transactionData;
        this.transactionList = getLocalTransactionList();
    }

    public void initCrpConnection() {
        try {
            String userpass = "";
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
                userpass = kvpSplit[0] + ":" + kvpSplit[1];
            }
            reader.close();

            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((userpass.getBytes())));
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json-rpc");
            conn.setDoOutput(true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBlockCountRpc() {
        try {
            initCrpConnection();
            if (conn != null) {

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("{\"method\": \"getblockcount\"}");
                wr.flush();
                wr.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String jsonText = "";
                while ((line = rd.readLine()) != null) {
                    jsonText += line;
                }
                rd.close();

                Object obj = JSONValue.parse(jsonText);
                JSONObject jsonObject = (JSONObject) obj;

                return Integer.parseInt(jsonObject.get("result").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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
                    processOutput.append(readLine + System.lineSeparator());
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
        try {
            initCrpConnection();
            if (conn != null) {

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("{\"method\": \"accounthistorycount\",\"params\":[\"mine\"]}");
                //wr.write("{\"method\":\"getbalance\",\"params\":[],\"id\":1}");
                wr.flush();
                wr.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String jsonText = "";
                while ((line = rd.readLine()) != null) {
                    jsonText += line;
                }
                rd.close();

                Object obj = JSONValue.parse(jsonText);
                JSONObject jsonObject = (JSONObject) obj;

                return Integer.parseInt(jsonObject.get("result").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getAccountHistoryCountCli() {
        try {
            Process p = null;
            StringBuilder processOutput = new StringBuilder();
            p = Runtime.getRuntime().exec(strCliPath + " accounthistorycount mine");


            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine + System.lineSeparator());
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

        Gson gson = new Gson();
        JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"mine\", {\"depth\":" + depth + ",\"no_rewards\":" + false + ",\"limit\":" + depth * 2000 + "}]}");
        TransactionModel[] transactions = gson.fromJson(jsonObject.get("result").toString(), TransactionModel[].class);
        List<TransactionModel> transactionList = Arrays.asList(transactions);
        return new ArrayList<>(transactionList);

    }

    public List<TransactionModel> getListAccountHistoryCli(int depth) {
        try {
            Gson gson = new Gson();
            StringBuilder processOutput;
            Process p;
            p = Runtime.getRuntime().exec(strCliPath + " listaccounthistory mine {\\\"depth\\\":" + depth + ",\\\"no_rewards\\\":" + false + ",\\\"limit\\\":" + depth * 2000 + "}");

            processOutput = new StringBuilder();

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine + System.lineSeparator());
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

            if (conn != null) {

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(requestJson);
                wr.flush();
                wr.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String jsonText = "";
                while ((line = rd.readLine()) != null) {
                    jsonText += line;
                }
                rd.close();

                Object obj = JSONValue.parse(jsonText);
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
                    TransactionModel transAction = new TransactionModel();
                    transAction.blockTime = Long.parseLong(transactionSplit[0]);
                    transAction.owner = transactionSplit[1];
                    transAction.type = transactionSplit[2];
                    transAction.amounts = new String[]{transactionSplit[3]};
                    transAction.blockHash = transactionSplit[4];
                    transAction.blockHeight = Integer.parseInt(transactionSplit[5]);
                    transAction.poolID = transactionSplit[6];
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

        if(getLocalTransactionList().size()>0){
            return getLocalTransactionList().get(getLocalTransactionList().size()-1).blockHeight;
        }else{
            return 0;
        }

    }

    public boolean startDefidExe() {
        //TODO: Start defid.exe
        //String strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
        return true;
    }

    public boolean updateTransactionData(int depth) {

        List<TransactionModel> transactionList = getLocalTransactionList();
        var transactionListNew = getListAccountHistoryRpc(depth);
        int blockCountLocal = 0;

        if (transactionList.size() > 0) {
            blockCountLocal = transactionList.get(transactionList.size()-1).blockHeight;
        }

        List<TransactionModel> updateTransactionList = new ArrayList<TransactionModel>();

        for (int i = transactionListNew.size() - 1; i >= 0; i--) {
            if (transactionListNew.get(i).blockHeight > blockCountLocal) {
                transactionList.add(0, transactionListNew.get(i));
                updateTransactionList.add(0, transactionListNew.get(i))
                ;
            }
        }

        if (updateTransactionList.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(this.strTransactionData , true));
                StringBuilder sb = new StringBuilder();
                String exportSplitter = ";";

                for (int iTransaction =updateTransactionList.size()-1; iTransaction >= 0; iTransaction--) {

                    for (int i = 0; i < updateTransactionList.get(iTransaction).amounts.length; i++) {
                        sb.append(updateTransactionList.get(iTransaction).blockTime + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).owner + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).type + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).amounts[i] + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).blockHash + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).blockHeight + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).poolID);
                        sb.append("\n");
                    }
                }
                writer.write(sb.toString());
                writer.close();

                this.transactionList = transactionList;

                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List getTransactionsInTime(List<TransactionModel> transactions, long startTime, long endTime) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).blockTime >= startTime && transactions.get(ilist).blockTime <= endTime) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsOfType(List<TransactionModel> transactions, String type) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).type.equals(type)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsOfOwner(List<TransactionModel> transactions, String owner) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).type.equals(owner)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsBetweenBlocks(List<TransactionModel> transactions, long startBlock, long endBlock) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).blockHeight >= startBlock && transactions.get(ilist).blockHeight <= endBlock) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }
    public static TreeMap getRewardsJoined(List<TransactionModel> transactions, String inervall , String Coin){
        TreeMap<String, Double> map = new TreeMap<>();
        TreeMap<String, Double> sorted = new TreeMap<>();
        switch (inervall){
            case "Daily":
                for(TransactionModel item : transactions){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(item.blockTime*1000L);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH)+1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    String[] AmountCoin = item.amounts[0].split("@");

                    String date = year + "-" + month + "-" + day;
                    if(item.type.equals("Rewards")) {
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);
                            Double newValue = oldValue + Double.parseDouble(AmountCoin[0]);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(AmountCoin[0]));
                        }
                    }
                }
                sorted.putAll(map);
                return sorted;

            case "Weekly":
                for(TransactionModel item : transactions){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(item.blockTime*1000L);
                    int year = cal.get(Calendar.YEAR);
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    String[] AmountCoin = item.amounts[0].split("@");

                    String date = year + "-" + week;
                    if(item.type.equals("Rewards")) {
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);
                            Double newValue = oldValue + Double.parseDouble(AmountCoin[0]);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(AmountCoin[0]));
                        }
                    }
                }
                sorted.putAll(map);
                return map;
            case "Monthly":
                for(TransactionModel item : transactions){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(item.blockTime*1000L);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH)+1;
                    String[] AmountCoin = item.amounts[0].split("@");

                    String date = year + "-" + month;
                    if(item.type.equals("Rewards")) {
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);
                            Double newValue = oldValue + Double.parseDouble(AmountCoin[0]);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(AmountCoin[0]));
                        }
                    }
                }
                sorted.putAll(map);
                return map;
            case "Yearly":
                for(TransactionModel item : transactions){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(item.blockTime*1000L);
                    int year = cal.get(Calendar.YEAR);
                    String[] AmountCoin = item.amounts[0].split("@");

                    String date = year+ "";
                    if(item.type.equals("Rewards")) {
                        if (map.keySet().contains(date)) {
                            Double oldValue = map.get(date);
                            Double newValue = oldValue + Double.parseDouble(AmountCoin[0]);
                            map.put(date, newValue);
                        } else {
                            map.put(date, Double.parseDouble(AmountCoin[0]));
                        }
                    }
                }
                sorted.putAll(map);
                return map;
        }

        return map;
    }

    public static String convertTimeStampToString(long timeStamp) {
        Date date = new Date(timeStamp * 1000L);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String convertTimeStampWithoutTimeToString(long timeStamp) {
        Date date = new Date(timeStamp * 1000L);
        DateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static double getTotalCoinAmount(List<TransactionModel> transactions, String coinName) {

        double amountCoin = 0;

        for (int iTransaction = 0; iTransaction < transactions.size(); iTransaction++) {

            for (int i = 0; i < transactions.get(iTransaction).amounts.length; i++) {

                String[] CoinsAndAmounts = splitCoinsAndAmounts(transactions.get(iTransaction).amounts[i].toString());

                if (coinName.equals(CoinsAndAmounts[1])) {
                    amountCoin += Double.parseDouble(CoinsAndAmounts[0]);
                }

            }

        }
        return amountCoin;
    }

    public static String[] splitCoinsAndAmounts(String amountAndCoin) {
        String[] splittedamountAndCoin = amountAndCoin.split("@");
        return splittedamountAndCoin;
    }
}