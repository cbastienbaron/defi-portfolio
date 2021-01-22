package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.ArrayList;

public class Update {


    public static CoinPrice updateCoinHistory(String strPathAppData)  {

        String strCoinHistoryData = strPathAppData + "coinHistory.portfolio";
        CoinPrice coinPrice = new CoinPrice();

        if (new File(strCoinHistoryData).exists()) {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strCoinHistoryData);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                coinPrice = (CoinPrice) in.readObject();

                in.close();
                file.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        var actualTimeStemp = new Timestamp(System.currentTimeMillis()).getTime() / 1000L;

        if (client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices().size() > 0) {

            //Update DFI
            coinPrice.dfiEurList.addAll(client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.dfiUsdList.addAll(client.getCoinMarketChartRangeById("defichain", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.dfiChfList.addAll(client.getCoinMarketChartRangeById("defichain", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.dfiBtcList.addAll(client.getCoinMarketChartRangeById("defichain", "btc", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            //Update BTC
            coinPrice.btcEurList.addAll(client.getCoinMarketChartRangeById("bitcoin", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.btcUsdList.addAll(client.getCoinMarketChartRangeById("bitcoin", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.btcChfList.addAll(client.getCoinMarketChartRangeById("bitcoin", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.btcBtcList.addAll(client.getCoinMarketChartRangeById("bitcoin", "btc", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            //Update ETH
            coinPrice.ethEurList.addAll(client.getCoinMarketChartRangeById("ethereum", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ethUsdList.addAll(client.getCoinMarketChartRangeById("ethereum", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ethChfList.addAll(client.getCoinMarketChartRangeById("ethereum", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ethBtcList.addAll(client.getCoinMarketChartRangeById("ethereum", "btc", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            //Update USDT
            coinPrice.usdtEurList.addAll(client.getCoinMarketChartRangeById("tether", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.usdtUsdList.addAll(client.getCoinMarketChartRangeById("tether", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.usdtChfList.addAll(client.getCoinMarketChartRangeById("tether", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            //Update LTC
            coinPrice.ltcEurList.addAll(client.getCoinMarketChartRangeById("litecoin", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ltcUsdList.addAll(client.getCoinMarketChartRangeById("litecoin", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ltcChfList.addAll(client.getCoinMarketChartRangeById("litecoin", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.ltcBtcList.addAll(client.getCoinMarketChartRangeById("litecoin", "btc", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            //Update BCH
            coinPrice.bchEurList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "eur", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.bchUsdList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "usd", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.bchChfList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "chf", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());
            coinPrice.bchBtcList.addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "btc", coinPrice.lastTimeSpamp, Long.toString(actualTimeStemp)).getPrices());

            coinPrice.lastTimeSpamp = Long.toString(actualTimeStemp);


            // Serialization
            try {
                //Saving of object in a file
                FileOutputStream file = new FileOutputStream(strCoinHistoryData);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(coinPrice);
                out.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return coinPrice;
    }

    public static List<Transaction> updatePortfolioData(String strPathAppData) {

        Gson gson = new Gson();
        List<Transaction> transactionList = new ArrayList<>();
        ;
        //String strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
        String strPortfolioData = strPathAppData + "data.portfolio";
        String strPathDefiCli = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
        File portFolioData = new File(strPortfolioData);
        int blockCount = 0;
        int latestBlock = 0;
        StringBuilder processOutput = new StringBuilder();
        Process p = null;

        if (portFolioData.exists()) {

            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strPortfolioData);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                transactionList = (List<Transaction>) in.readObject();

                in.close();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


           /* HttpURLConnection connection = null;

            try {
                //Create connection
                URL url = new URL("http://127.0.0.1:8555");
                String urlParameters = "getblockcount";
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/json-rpc");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParameters.getBytes().length));

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                var a =  response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }*/

            try {

                p = Runtime.getRuntime().exec(strPathDefiCli + " getblockcount");
            } catch (IOException e) {
                e.printStackTrace();
            }


            blockCount = 0;

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine + System.lineSeparator());
                }

                blockCount = Integer.parseInt(processOutput.toString().trim());

                p.waitFor();

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                p = Runtime.getRuntime().exec(strPathDefiCli + " accounthistorycount mine");
            } catch (IOException e) {
                e.printStackTrace();
            }

            blockCount = 0;

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));) {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null) {
                    processOutput.append(readLine + System.lineSeparator());
                }

                blockCount = Integer.parseInt(processOutput.toString().trim());

                p.waitFor();

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }


        int blockHeight = blockCount - latestBlock;
        ///this.strCurrentBlockOnChain.setText("Current block on Blockchain: " + blockCount );

        try {
            p = Runtime.getRuntime().exec(strPathDefiCli + " listaccounthistory mine {\\\"depth\\\":" + blockHeight + 1 + ",\\\"no_rewards\\\":" + false + ",\\\"limit\\\":" + (blockHeight + 1) * 3 + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(p.getInputStream()));) {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null) {
                processOutput.append(readLine + System.lineSeparator());
            }

            p.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        String jsonTransaction = processOutput.toString().trim();

        Transaction[] transactionsNew = gson.fromJson(jsonTransaction, Transaction[].class);

        if (transactionsNew != null) {
            List<Transaction> transactionListNew = Arrays.asList(transactionsNew);
            transactionListNew = new ArrayList<>(transactionListNew);


            for (int i = transactionListNew.size() - 1; i >= 0; i--) {
                if (transactionListNew.get(i).blockHeight > latestBlock) {
                    transactionList.add(0, transactionListNew.get(i));
                }
            }

            // Serialization
            try {
                //Saving of object in a file
                FileOutputStream file = new FileOutputStream(strPortfolioData);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(transactionList);
                out.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return transactionList;
    }
}