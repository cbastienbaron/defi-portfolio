package portfolio.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import portfolio.models.AddressModel;
import portfolio.models.PortfolioModel;
import portfolio.models.TransactionModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionController {

    private static TransactionController OBJ = null;

    static {
        OBJ = new TransactionController();
    }

    private  SettingsController settingsController = SettingsController.getInstance();
    private  CoinPriceController coinPriceController = CoinPriceController.getInstance();
    private  String strTransactionData = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + SettingsController.getInstance().strTransactionData;
    private  ObservableList<TransactionModel> transactionList;
    private int localBlockCount;
    private final TreeMap<String, TreeMap<String, PortfolioModel>> portfolioList = new TreeMap<>();
    private final TreeMap<String, Double> balanceList = new TreeMap<>();
    private JFrame frameUpdate;
    public JLabel jl;
    public Process defidProcess;
    private Boolean classSingleton=true;

    public TransactionController() {
        if(classSingleton){
            classSingleton =false;
            this.transactionList = getLocalTransactionList();
            this.localBlockCount = getLocalBlockCount();
        }
        }

    public void clearTransactionList(){
        transactionList.clear();
    }

    public static TransactionController getInstance() {
        return OBJ;
    }


    public boolean checkRpc() {
        JSONObject jsonObject = getRpcResponse("{\"method\": \"getrpcinfo\"}");
        if (jsonObject != null) {
            return jsonObject.get("result") != null;
        } else {
            return false;
        }
    }

    public void startServer() {
        try {
            if (!this.checkRpc()) {
                switch (this.settingsController.getPlatform()) {
                    case "mac":
                        FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "defi.sh");
                        myWriter.write(this.settingsController.BINARY_FILE_PATH + " -conf=" + this.settingsController.PORTFOLIO_CONFIG_FILE_PATH);
                        myWriter.close();

                        defidProcess = Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + System.getProperty("user.dir") + "/PortfolioData/./" + "defi.sh");
                        break;
                    case "win":
                        defidProcess =Runtime.getRuntime().exec("cmd /c start " + this.settingsController.BINARY_FILE_PATH + " -conf=" + this.settingsController.PORTFOLIO_CONFIG_FILE_PATH); // + " -conf=" + this.settingsController.CONFIG_FILE_PATH);
                        break;
                    case "linux":
                        defidProcess =Runtime.getRuntime().exec("/usr/bin/x-terminal-emulator -e " + this.settingsController.BINARY_FILE_PATH + " -conf=" + this.settingsController.PORTFOLIO_CONFIG_FILE_PATH);
                        break;
                }
            }
        } catch (IOException e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }
    }

    public void stopServer() {
        try {
            getRpcResponse("{\"method\": \"stop\"}");
            if(defidProcess!=null)defidProcess.destroy();
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

    public void clearPortfolioList() {
        this.portfolioList.clear();
    }

    public ObservableList<TransactionModel> getTransactionList() {
        return transactionList;
    }

    public String getBlockCount() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.defichain.io/v1/stats").openConnection();
            String jsonText = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                jsonText = br.readLine();
            } catch (Exception ex) {
                this.settingsController.logger.warning("Exception occured: " + ex.toString());
            }
            JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
            if (obj.get("blockHeight") != null) {
                return obj.get("blockHeight").toString();
            } else {
                return "No connection";
            }
        } catch (IOException e) {
            this.settingsController.logger.warning("Exception occured: " + e.toString());
        }

        return "No connection";
    }

    public String getBlockCountRpc() {
        try {
            JSONObject jsonObject = getRpcResponse("{\"method\": \"getblockcount\"}");
            if (jsonObject != null) {
                if (jsonObject.get("result") != null) {
                    return jsonObject.get("result").toString();
                } else {
                    return "No connection";
                }
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
                if (this.settingsController.getPlatform().equals("mac")) {
                    try {
                        FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                        myWriter.write(this.settingsController.translationList.getValue().get("UpdateData").toString() + Math.ceil((((double) (i) * blockDepth) / (double) depth) * 100) + "%");
                        myWriter.close();
                    } catch (IOException e) {
                        this.settingsController.logger.warning("Could not write to update.portfolio.");
                    }
                } else {
                    this.jl.setText(this.settingsController.translationList.getValue().get("UpdateData").toString() + Math.ceil((((double) (i) * blockDepth) / (double) depth) * 100) + "%");
                }
                JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"all\", {\"maxBlockHeight\":" + (blockCount - (i * blockDepth) - i) + ",\"depth\":" + blockDepth + ",\"no_rewards\":" + false + ",\"limit\":" + blockDepth * 2000 + "}]}");
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
                restBlockCount = blockCount - i * blockDepth;
            }

            restBlockCount = restBlockCount - blockDepth;
            JSONObject jsonObject = getRpcResponse("{\"method\":\"listaccounthistory\",\"params\":[\"all\", {\"maxBlockHeight\":" + (restBlockCount - 1) + ",\"depth\":" + depth % blockDepth + ",\"no_rewards\":" + false + ",\"limit\":" + (depth % blockDepth) * 2000 + "}]}");
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
        if (this.settingsController.getPlatform().equals("mac")) {
            this.jl = new JLabel(this.settingsController.translationList.getValue().get("InitializingData").toString(), JLabel.CENTER);
        } else {
            ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/ajaxloader.gif");
            this.jl = new JLabel(this.settingsController.translationList.getValue().get("InitializingData").toString(), icon, JLabel.CENTER);
        }
        this.jl.setSize(400, 100);
        this.jl.setLocation(0, 0);
        if (this.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            this.jl.setForeground(Color.WHITE);
        } else {
            this.jl.setForeground(Color.BLACK);
        }
        this.frameUpdate.add(jl);
        this.frameUpdate.setSize(400, 110);
        this.frameUpdate.setLocationRelativeTo(null);
        this.frameUpdate.setUndecorated(true);

        if (this.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            this.frameUpdate.getContentPane().setBackground(new Color(55, 62, 67));
        }
        this.frameUpdate.setVisible(true);
        this.frameUpdate.toFront();

        this.frameUpdate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseClickPoint = e.getPoint(); // update the position
            }

        });
        this.frameUpdate.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point newPoint = e.getLocationOnScreen();
                newPoint.translate(-mouseClickPoint.x, -mouseClickPoint.y); // Moves the point by given values from its location
                frameUpdate.setLocation(newPoint); // set the new location
            }
        });
    }

    private Point mouseClickPoint;

    private JSONObject getRpcResponse(String requestJson) {

        try {
            //URL url = new URL("http://" + this.settingsController.rpcbind + ":" + this.settingsController.rpcport + "/");
            URL url = new URL("http://127.0.0.1:8554");

            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout((int) TimeUnit.MINUTES.toMillis(0L));
            conn.setReadTimeout((int) TimeUnit.MINUTES.toMillis(0L));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((this.settingsController.auth.getBytes())));

            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json-rpc");
            conn.setDoOutput(true);
            conn.getOutputStream().write(requestJson.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().close();
            String jsonText = "";
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    jsonText = br.readLine();
                } catch (Exception ex) {
                    this.settingsController.logger.warning("Exception occured: " + ex.toString());
                }
                SettingsController.getInstance().debouncer = true;
                Object obj = JSONValue.parse(jsonText);
                return (JSONObject) obj;

            }

        } catch (IOException ioException) {
            SettingsController.getInstance().runTimer = !(ioException.getMessage().equals("Connection refused: connect") & SettingsController.getInstance().debouncer);
        }


        return new JSONObject();

    }

    public ObservableList<TransactionModel> getLocalTransactionList() {

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

                    //if (!(transAction.getTypeValue().equals("UtxosToAccount") | transAction.getTypeValue().equals("AccountToUtxos")))
                    // addBalanceModel(transAction);
                    if (transAction.getTypeValue().equals("Rewards") | transAction.getTypeValue().equals("Commission")){
                        addToPortfolioModel(transAction);
                    }

                    line = reader.readLine();
                }

                reader.close();
                return FXCollections.observableArrayList(transactionList);
            } catch (IOException e) {
                this.settingsController.logger.warning("Exception occured: " + e.toString());
            }
        }
        return FXCollections.observableArrayList(transactionList);
    }

    public String getTokenFromID(String id) {
        JSONObject jsonObject = getRpcResponse("{\"method\": \"gettoken\",\"params\":[" + id + "]}");
        jsonObject = (JSONObject) jsonObject.get("result");
        jsonObject = (JSONObject) jsonObject.get(id);

        return jsonObject.get("symbol").toString();
    }

    public String getBalance() {
        JSONObject jsonObject = getRpcResponse("{\"method\": \"getbalance\"}");
        return jsonObject.get("result").toString();
    }

    public JSONArray getTokenBalances() {
        JSONObject jsonObject = getRpcResponse("{\"method\": \"gettokenbalances\"}");
        JSONArray jsonArray = (JSONArray) jsonObject.get("result");
        return jsonArray;
    }

    public void addToPortfolioModel(TransactionModel transactionSplit) {

        String pool = getPoolPairFromId(transactionSplit.getPoolIDValue());

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
       if(!new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "/transactionData.portfolio").exists()){
           return 0;
       }
        if (transactionList.size() > 0) {

            return transactionList.get(transactionList.size() - 1).getBlockHeightValue();
        } else {
            return 0;
        }
    }

    public boolean updateTransactionData(int depth) {

        List<TransactionModel> transactionListNew = getListAccountHistoryRpc(depth);
        List<TransactionModel> updateTransactionList = new ArrayList<>();
        int counter = 0;
        for (int i = transactionListNew.size() - 1; i >= 0; i--) {
            if (transactionListNew.get(i).getBlockHeightValue() > this.localBlockCount) {
                this.transactionList.add(transactionListNew.get(i));
                updateTransactionList.add(transactionListNew.get(i));
                //if (!transactionListNew.get(i).getTypeValue().equals("UtxosToAccount") | !transactionListNew.get(i).getTypeValue().equals("AccountToUtxos"))
                // addBalanceModel(transactionListNew.get(i));
                //
                if (transactionListNew.get(i).getTypeValue().equals("Rewards") | transactionListNew.get(i).getTypeValue().equals("Commission"))
                    addToPortfolioModel(transactionListNew.get(i));

                if (this.settingsController.getPlatform().equals("mac")) {
                    try {
                        if (counter > 1000) {
                            FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                            myWriter.write(this.settingsController.translationList.getValue().get("PreparingData").toString() + Math.ceil((((double) transactionListNew.size() - i) / (double) transactionListNew.size()) * 100) + "%");
                            myWriter.close();
                            counter = 0;
                        }
                    } catch (IOException e) {
                        this.settingsController.logger.warning("Could not write to update.portfolio.");
                    }
                } else {
                    jl.setText(this.settingsController.translationList.getValue().get("PreparingData").toString() + Math.ceil((((double) transactionListNew.size() - i) / (double) transactionListNew.size()) * 100) + "%");
                }
                counter++;
            }
        }
        int i = 1;
        if (updateTransactionList.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(this.strTransactionData, true));
                String exportSplitter = ";";
                counter = 0;
                for (TransactionModel transactionModel : updateTransactionList) {
                    counter++;
                    StringBuilder sb = new StringBuilder();
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
                    if (this.settingsController.getPlatform().equals("mac")) {
                        try {
                            if (counter > 1000) {
                                FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                                myWriter.write(this.settingsController.translationList.getValue().get("SaveData").toString() + Math.ceil(((double) i / updateTransactionList.size()) * 100) + "%");
                                myWriter.close();
                                counter = 0;
                            }
                        } catch (IOException e) {
                            this.settingsController.logger.warning("Could not write to update.portfolio.");
                        }
                    } else {
                        jl.setText(this.settingsController.translationList.getValue().get("SaveData").toString() + Math.ceil(((double) i / updateTransactionList.size()) * 100) + "%");
                    }
                    i++;
                    writer.write(sb.toString());
                    sb = null;
                }
                writer.close();
                if (!this.settingsController.getPlatform().equals("mac")) this.frameUpdate.dispose();
                this.localBlockCount = this.transactionList.get(this.transactionList.size() - 1).getBlockHeightValue();
                stopServer();
                return true;
            } catch (IOException e) {
                this.settingsController.logger.warning("Exception occured: " + e.toString());
            }
        }
        if (!this.settingsController.getPlatform().equals("mac")) this.frameUpdate.dispose();
        return false;
    }

    public String getPoolPairFromId(String poolID) {
        String pool;
        switch (poolID) {
            case "0":
                pool = "DFI";
                break;
            case "1":
                pool = "ETH";
                break;
            case "2":
                pool = "BTC";
                break;
            case "3":
                pool = "USDT";
                break;
            case "4":
                pool = "ETH-DFI";
                break;
            case "5":
                pool = "BTC-DFI";
                break;
            case "6":
                pool = "USDT-DFI";
                break;
            case "7":
                pool = "DOGE";
                break;
            case "8":
                pool = "DOGE-DFI";
                break;
            case "9":
                pool = "LTC";
                break;
            case "10":
                pool = "LTC-DFI";
                break;
            case "11":
                pool = "BCH";
                break;
            case "12":
                pool = "BCH-DFI";
                break;
            default:
                pool = "-";
                break;
        }
        return pool;
    }

    public String getIdFromPoolPair(String poolID) {
        String pool;
        switch (poolID) {
            case "DFI":
                pool = "0";
                break;
            case "ETH":
                pool = "1";
                break;
            case "BTC":
                pool = "2";
                break;
            case "USDT":
                pool = "3";
                break;
            case "ETH-DFI":
                pool = "4";
                break;
            case "BTC-DFI":
                pool = "5";
                break;
            case "USDT-DFI":
                pool = "6";
                break;
            case "DOGE":
                pool = "7";
                break;
            case "DOGE-DFI":
                pool = "8";
                break;
            case "LTC":
                pool = "9";
                break;
            case "LTC-DFI":
                pool = "10";
                break;
            case "BCH":
                pool = "11";
                break;
            case "BCH-DFI":
                pool = "12";
                break;
            default:
                pool = "-";
                break;
        }
        return pool;
    }

    private void addBalanceModel(TransactionModel transactionModel) {

        if (!balanceList.containsKey(transactionModel.getCryptoCurrencyValue())) {
            balanceList.put(transactionModel.getCryptoCurrencyValue(), transactionModel.getCryptoValueValue());
        } else {
            double oldValue = balanceList.get(transactionModel.getCryptoCurrencyValue()).doubleValue();
            balanceList.put(getPoolPairFromId(transactionModel.getPoolIDValue()), transactionModel.getCryptoValueValue() + oldValue);
        }

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T23:59:59'");
        return dateFormat.format(date);
    }

    public double getTotalCoinAmount(List<TransactionModel> transactions, String coinName) {

        double amountCoin = 0;

        for (TransactionModel transaction : transactions) {

            if (!transaction.getTypeValue().equals("UtxosToAccount") & !transaction.getTypeValue().equals("AccountToUtxos")) {
                String[] CoinsAndAmounts = splitCoinsAndAmounts(transaction.getAmountValue());
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