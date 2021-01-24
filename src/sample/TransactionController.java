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
    public List<TransactionModel> transactionList = new ArrayList<>();
    String strTransactionData;
    int localBlockCount = 0;
    OutputStreamWriter wr;
    public TransactionController(String transactionData){
        this.strTransactionData = transactionData;
        this.localBlockCount = getLocalBlockCount();
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
            wr = new OutputStreamWriter(conn.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBlockCountRpc() {

                JSONObject jsonObject =getRpcResponse("{\"method\": \"getblockcount\"}");
                return Integer.parseInt(jsonObject.get("result").toString());
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

        JSONObject jsonObject = getRpcResponse("{\"method\": \"accounthistorycount\",\"params\":[\"mine\"]}");
        return Integer.parseInt(jsonObject.get("result").toString());

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

        List<TransactionModel> transactionList = new ArrayList<>();
                JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"mine\", {\"depth\":" + depth + ",\"no_rewards\":" + false + ",\"limit\":" + depth * 2000 + "}]}");

        JSONArray transactionJson = (JSONArray) jsonObject.get("result");

        for (Object transaction:transactionJson
             ) {
            var transactionJ = (JSONObject) transaction;
            transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()),transactionJ.get("owner").toString(),transactionJ.get("type").toString(),new String[]{transactionJ.get("amounts").toString().replace("[","").replace("]","").replace("\"","")},transactionJ.get("blockHash").toString(),Integer.parseInt(transactionJ.get("blockHeight").toString()),transactionJ.get("poolID").toString(),this));
        }

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
                    TransactionModel transAction = new TransactionModel(Long.parseLong(transactionSplit[0]),transactionSplit[1],transactionSplit[2],new String[]{transactionSplit[3]},transactionSplit[4],Integer.parseInt(transactionSplit[5]),transactionSplit[6],this);
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
        if(transactionList.size() > 0){

            return this.transactionList.get(transactionList.size()-1).getBlockHeightProperty();
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

        List<TransactionModel> updateTransactionList = new ArrayList<TransactionModel>();

        for (int i = transactionListNew.size() - 1; i >= 0; i--) {
            if (transactionListNew.get(i).getBlockHeightProperty() > this.localBlockCount) {
                this.transactionList.add( transactionListNew.get(i));
                updateTransactionList.add( transactionListNew.get(i))
                ;
            }
        }

        if (updateTransactionList.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(this.strTransactionData , true));
                StringBuilder sb = new StringBuilder();
                String exportSplitter = ";";

                for (int iTransaction =0; iTransaction < updateTransactionList.size(); iTransaction++) {

                    for (int i = 0; i < updateTransactionList.get(iTransaction).getAmountProperty().length; i++) {
                        sb.append(updateTransactionList.get(iTransaction).getBlockTimeProperty() + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getOwnerProperty() + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getTypeProperty() + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getAmountProperty()[i] + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getBlockHashProperty() + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getBlockHeightProperty() + exportSplitter);
                        sb.append(updateTransactionList.get(iTransaction).getPoolIDProperty());
                        sb.append("\n");
                    }
                }
                writer.write(sb.toString());
                writer.close();

                this.localBlockCount = this.transactionList.get(this.transactionList.size()-1).getBlockHeightProperty();
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
            if (transactions.get(ilist).getBlockTimeProperty() >= startTime && transactions.get(ilist).getBlockTimeProperty() <= endTime) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsOfType(List<TransactionModel> transactions, String type) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeProperty().equals(type)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsOfOwner(List<TransactionModel> transactions, String owner) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getTypeProperty().equals(owner)) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsBetweenBlocks(List<TransactionModel> transactions, long startBlock, long endBlock) {
        List<TransactionModel> filteredTransactions = new ArrayList<>();
        for (int ilist = 1; ilist < transactions.size(); ilist++) {
            if (transactions.get(ilist).getBlockHeightProperty() >= startBlock && transactions.get(ilist).getBlockHeightProperty() <= endBlock) {
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }
    public static List getRewardsJoined(List<TransactionModel> transactions, String inervall){
        List<TransactionModel> filteredTransactions = new ArrayList<>();

        switch (inervall){
            case "daily":

                return filteredTransactions;
            case "weekly":

                return filteredTransactions;
            case "monthly":

                return filteredTransactions;
            case "yearly":

                return filteredTransactions;
        }

        return filteredTransactions;
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

            for (int i = 0; i < transactions.get(iTransaction).getAmountProperty().length; i++) {

                String[] CoinsAndAmounts = splitCoinsAndAmounts(transactions.get(iTransaction).getAmountProperty()[i].toString());

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