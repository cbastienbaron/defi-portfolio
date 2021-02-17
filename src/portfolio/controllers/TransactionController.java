package portfolio.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import portfolio.models.AddressModel;
import portfolio.models.PortfolioModel;
import portfolio.models.TransactionModel;

import javax.swing.*;
import java.awt.*;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {

    private URL url;
    private URLConnection conn;
    private String strCookiePath;
    private List<TransactionModel> transactionList;
    private String strTransactionData;
    private int localBlockCount;
    private SettingsController settingsController;
    private OutputStreamWriter wr;
    private CoinPriceController coinPriceController;
    private TreeMap<String, TreeMap<String, PortfolioModel>> portfolioList = new TreeMap<>();
    private JFrame frameUpdate;
    public JLabel jl;

    public TransactionController(String transactionData, SettingsController settingsController, CoinPriceController coinPriceController, String strCookiePath) {
        this.strTransactionData = transactionData;
        this.settingsController = settingsController;
        this.coinPriceController = coinPriceController;
        this.transactionList = getLocalTransactionList();
        this.localBlockCount = getLocalBlockCount();
        this.strCookiePath = strCookiePath;
    }

    public boolean checkRpc() {
        return new File(this.strCookiePath).exists();
    }

    public void startServer() {
        try {
            Runtime.getRuntime().exec("cmd /c start " + this.settingsController.strPathDefid.replace(" ","\" \""));
        } catch (IOException e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }
    }

    public void closeServer() {
        try {
            initCrpConnection();
            getRpcResponse("{\"method\": \"stop\"}");
        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }
    }

    public CoinPriceController getCoinPriceController() {
        return coinPriceController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public TreeMap<String, TreeMap<String, PortfolioModel>> getPortfolioList() {
        return portfolioList;
    }

    public List<TransactionModel> getTransactionList() {
        return transactionList;
    }

    public void initCrpConnection() {
        try {
            String strCookieData = "";

            if (checkRpc()) {

                try {
                    this.url = new URL("http://127.0.0.1:8554");
                } catch (MalformedURLException e) {
                    this.settingsController.logger.warning("Exception occured: " + e.toString());
                }
                this.conn = url.openConnection();

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        strCookiePath));
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
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }


    }

    public String getBlockCountRpc() {
        try {

            JSONObject jsonObject = getRpcResponse("{\"method\": \"getblockcount\"}");

            if (jsonObject.get("result") != null) {
                return jsonObject.get("result").toString();
            } else {
                return "No connection";
            }
        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
            return "No connection";
        }
    }

    public int getAccountHistoryCountRpc() {

        JSONObject jsonObject = getRpcResponse("{\"method\": \"accounthistorycount\",\"params\":[\"mine\"]}");
        return Integer.parseInt(jsonObject.get("result").toString());
    }

    public List<AddressModel> getListAddressGroupingsRpc() {
        List<AddressModel> addressList = new ArrayList<>();
        JSONObject jsonObject = getRpcResponse("{\"method\": \"listaddressgroupings\"}");
        JSONArray transactionJson = (JSONArray) jsonObject.get("result");
        for (Object transaction : (JSONArray) transactionJson.get(0)) {
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

    public List<TransactionModel> getListAccountHistoryRpc(int depth) {

        List<TransactionModel> transactionList = new ArrayList<>();

        try {

            int blockCount = Integer.parseInt(getBlockCountRpc());
            int blockDepth = 10000;
            int restBlockCount = blockCount + blockDepth + 1;
            for (int i = 0; i < Math.ceil(depth / blockDepth); i = i + 1) {
                this.jl.setText(this.settingsController.translationList.getValue().get("UpdateData").toString() + Math.ceil((((double) (i) * blockDepth) / (double) depth) * 100) + "%");
                JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"mine\", {\"maxBlockHeight\":" + (blockCount - (i * blockDepth) - i) + ",\"depth\":" + blockDepth + ",\"no_rewards\":" + false + ",\"limit\":" + blockDepth * 2000 + "}]}");
                JSONArray transactionJson = (JSONArray) jsonObject.get("result");
                for (Object transaction : transactionJson) {
                    JSONObject transactionJ = (JSONObject) transaction;

                    for (String amount : (transactionJ.get("amounts").toString().replace("[", "").replace("]", "").replace("\"", "")).split(",")) {

                        if (transactionJ.get("poolID") != null) {
                            transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), amount, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), transactionJ.get("poolID").toString(), "", this));
                        } else {
                            transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), amount, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), "", transactionJ.get("txid").toString(), this));
                        }
                    }
                }
                restBlockCount = blockCount - i * blockDepth;;
            }

            restBlockCount = restBlockCount - blockDepth;
            JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"mine\", {\"maxBlockHeight\":" + (restBlockCount - 1) + ",\"depth\":" + depth % blockDepth + ",\"no_rewards\":" + false + ",\"limit\":" + (depth % blockDepth) * 2000 + "}]}");
            JSONArray transactionJson = (JSONArray) jsonObject.get("result");
            for (Object transaction : transactionJson) {
                JSONObject transactionJ = (JSONObject) transaction;
                for (String amount : (transactionJ.get("amounts").toString().replace("[", "").replace("]", "").replace("\"", "")).split(",")) {
                    if (transactionJ.get("poolID") != null) {
                        transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), amount, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), transactionJ.get("poolID").toString(), "", this));
                    } else {
                        transactionList.add(new TransactionModel(Long.parseLong(transactionJ.get("blockTime").toString()), transactionJ.get("owner").toString(), transactionJ.get("type").toString(), amount, transactionJ.get("blockHash").toString(), Integer.parseInt(transactionJ.get("blockHeight").toString()), "", transactionJ.get("txid").toString(), this));
                    }
                }
            }
        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }

        return transactionList;
    }

    public void updateJFrame() {
        this.frameUpdate = new JFrame();
        this.frameUpdate.setLayout(null);
        this.frameUpdate.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png").getImage());
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\defi-portfolio\\src\\icons\\ajaxloader.gif");
        this.jl = new JLabel(this.settingsController.translationList.getValue().get("InitializingData").toString(), icon, JLabel.CENTER);
        this.jl.setSize(400, 100);
        this.jl.setLocation(0, 0);
        this.jl.setForeground(Color.WHITE);
    this.frameUpdate.add(jl);
        this.frameUpdate.setSize(400, 110);
        this.frameUpdate.setLocationRelativeTo(null);
        this.frameUpdate.setUndecorated(true);
        this.frameUpdate.getContentPane().setBackground(new Color(55, 62, 67));
        this.frameUpdate.setVisible(true);
        this.frameUpdate.toFront();
    }

    private JSONObject getRpcResponse(String requestJson) {
        try {

            initCrpConnection();

            if (conn != null & wr != null) {
                wr.write(requestJson);
                wr.flush();
                wr.close();

               String jsonText="";
                 try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                     jsonText = br.readLine();

            }catch(Exception ex){
                this.settingsController.logger.warning("Exception occured: " + ex.toString());
            }
                Object obj = JSONValue.parse(jsonText);
                return (JSONObject) obj;
        }

    } catch(Exception e)
    {
        this.settingsController.logger.warning("Exception occured: " + e.toString());
    }
        return new   JSONObject();
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
                    TransactionModel transAction = new TransactionModel(Long.parseLong(transactionSplit[0]), transactionSplit[1], transactionSplit[2], transactionSplit[3], transactionSplit[4], Integer.parseInt(transactionSplit[5]), transactionSplit[6], transactionSplit[7], this);
                    transactionList.add(transAction);
                    if (transAction.getTypeValue().equals("Rewards") | transAction.getTypeValue().equals("Commission"))
                        addToPortfolioModel(transAction);
                    line = reader.readLine();
                }

                reader.close();
                return transactionList;
            } catch (IOException e) {
                this.settingsController.logger.warning("Exception occured: " + e.toString());
            }
        }

        return transactionList;
    }

    public void addToPortfolioModel(TransactionModel transactionSplit) {

        String pool = "BTC-DFI";
        switch (transactionSplit.getPoolIDValue()) {
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
            case "10":
                pool = "LTC-DFI";
                break;
            default:
                break;
        }
        String[] intervallList = new String[]{"Daily", "Weekly", "Monthly", "Yearly"};

        for (String intervall : intervallList) {

            String keyValue = pool + "-" + intervall;

            if (!portfolioList.containsKey(keyValue)) {
                portfolioList.put(keyValue, new TreeMap<>());
            }

            Double newFiatRewards = 0.0;
            Double newFiatCommissions1 = 0.0;
            Double newFiatCommissions2 = 0.0;
            Double newCoinRewards = 0.0;
            Double newCoinCommissions1 = 0.0;
            Double newCoinCommissions2 = 0.0;

            if (transactionSplit.getTypeValue().equals("Rewards")) {
                newFiatRewards = transactionSplit.getFiatValueValue();
                newCoinRewards = transactionSplit.getCryptoValueValue();
            }

            if (transactionSplit.getTypeValue().equals("Commission")) {
                if (pool.split("-")[1].equals(transactionSplit.getCryptoCurrencyValue())) {
                    newFiatCommissions1 = transactionSplit.getFiatValueValue();
                    newCoinCommissions1 = transactionSplit.getCryptoValueValue();
                } else {
                    newFiatCommissions2 = transactionSplit.getFiatValueValue();
                    newCoinCommissions2 = transactionSplit.getCryptoValueValue();
                }
            }

            if (portfolioList.get(keyValue).containsKey(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall))) {

                Double oldCoinRewards = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getCoinRewards1Value();
                Double oldFiatRewards = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getFiatRewards1Value();
                Double oldCoinCommissions1 = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getCoinCommissions1Value();
                Double oldFiatCommissions1 = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getFiatCommissions1Value();
                Double oldCoinCommissions2 = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getCoinCommissions2Value();
                Double oldFiatCommissions2 = portfolioList.get(keyValue).get(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall)).getFiatCommissions2Value();

                portfolioList.get(keyValue).put(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall), new PortfolioModel(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall), oldFiatRewards + newFiatRewards, oldFiatCommissions1 + newFiatCommissions1, oldFiatCommissions2 + newFiatCommissions2, oldCoinRewards + newCoinRewards, oldCoinCommissions1 + newCoinCommissions1, oldCoinCommissions2 + newCoinCommissions2, pool, intervall));
            } else {
                portfolioList.get(keyValue).put(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall), new PortfolioModel(getDate(Long.toString(transactionSplit.getBlockTimeValue()), intervall), newFiatRewards, newFiatCommissions1, newFiatCommissions2, newCoinRewards, newCoinCommissions1, newCoinCommissions2, pool, intervall));
            }
        }
    }

    public String getDate(String blockTime, String intervall) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(blockTime) * 1000L);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String date = "";
        switch (intervall) {
            case "Täglich":
            case "Daily":
                String monthAdapted = Integer.toString(month);
                if (month < 10) {
                    monthAdapted = "0" + month;
                }
                if (day < 10) {
                    date = year + "-" + monthAdapted + "-0" + day;
                } else {
                    date = year + "-" + monthAdapted + "-" + day;
                }
                break;
            case "Monatlich":
            case "Monthly":
                if (month < 10) {
                    date = year + "-0" + month;
                } else {
                    date = year + "-" + month;
                }
                break;
            case "Wöchentlich":
            case "Weekly":
                int correct = 0;
                if (month == 1 && (day == 1 || day == 2 || day == 3)) {
                    correct = 1;
                }
                if (week < 10) {
                    date = year - correct + "-0" + week;
                } else {
                    date = year - correct + "-" + week;
                }
                break;
            case "Jährlich":
            case "Yearly":
                date = Integer.toString(year);
        }
        return date;
    }

    public String convertDateToIntervall(String strDate, String intervall) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        } catch (ParseException e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }
        assert date != null;
        Timestamp ts = new Timestamp(date.getTime());
        return this.getDate(Long.toString(ts.getTime() / 1000), intervall);
    }

    public int getLocalBlockCount() {
        if (transactionList.size() > 0) {

            return this.transactionList.get(transactionList.size() - 1).getBlockHeightValue();
        } else {
            return 0;
        }
    }

    public boolean updateTransactionData(int depth) {

        List<TransactionModel> transactionListNew = getListAccountHistoryRpc(depth);
        List<TransactionModel> updateTransactionList = new ArrayList<>();

        for (int i = transactionListNew.size() - 1; i >= 0; i--) {
            if (transactionListNew.get(i).getBlockHeightValue() > this.localBlockCount) {
                this.transactionList.add(transactionListNew.get(i));
                updateTransactionList.add(transactionListNew.get(i));
                if (transactionListNew.get(i).getTypeValue().equals("Rewards") | transactionListNew.get(i).getTypeValue().equals("Commission"))
                    addToPortfolioModel(transactionListNew.get(i));
                jl.setText(this.settingsController.translationList.getValue().get("PreparingData").toString() + Math.ceil((((double) transactionListNew.size() - i) / (double) transactionListNew.size()) * 100) + "%");
            }
        }
        int i = 1;
        if (updateTransactionList.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(this.strTransactionData, true));
                StringBuilder sb = new StringBuilder();
                String exportSplitter = ";";

                for (TransactionModel transactionModel : updateTransactionList) {

                    sb.append(transactionModel.getBlockTimeValue()).append(exportSplitter);
                    sb.append(transactionModel.getOwnerValue()).append(exportSplitter);
                    sb.append(transactionModel.getTypeValue()).append(exportSplitter);
                    sb.append(transactionModel.getAmountValue()).append(exportSplitter);
                    sb.append(transactionModel.getBlockHashValue()).append(exportSplitter);
                    sb.append(transactionModel.getBlockHeightValue()).append(exportSplitter);
                    sb.append(transactionModel.getPoolIDValue()).append(exportSplitter);
                    if (transactionModel.getTxIDValue().equals(""))
                        sb.append("\"\"");
                    else
                        sb.append(transactionModel.getTxIDValue());

                    sb.append("\n");
                    jl.setText(this.settingsController.translationList.getValue().get("SaveData").toString() + Math.ceil(((double) i / updateTransactionList.size()) * 100) + "%");
                    i++;
                }
                writer.write(sb.toString());
                writer.close();
                this.frameUpdate.dispose();
                this.localBlockCount = this.transactionList.get(this.transactionList.size() - 1).getBlockHeightValue();
                return true;
            } catch (IOException e) {
                this.settingsController.logger.warning("Exception occured: " + e.toString());
            }
        }
        this.frameUpdate.dispose();
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
            String[] CoinsAndAmounts = splitCoinsAndAmounts(transaction.getAmountValue());
            if (coinName.equals(CoinsAndAmounts[1])) {
                amountCoin += Double.parseDouble(CoinsAndAmounts[0]);
            }
        }
        return amountCoin;
    }

    public String[] splitCoinsAndAmounts(String amountAndCoin) {
        return amountAndCoin.split("@");
    }
}