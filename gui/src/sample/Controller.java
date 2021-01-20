package sample;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {


    @FXML private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse , anchorPanelExport;
    @FXML private ProgressBar progressBar;
    @FXML private Label strCurrentBlockLocally, strCurrentBlockOnChain, strUpToDate;
    @FXML private Button btnUpdateDatabse;
    @FXML private ComboBox cmbCoins;
    @FXML private ImageView imgViewObj;
    @FXML private DatePicker dateExpStart = new DatePicker();
    @FXML private DatePicker dateExpEnd = new DatePicker();
    @FXML private DatePicker dateAnalyseStart = new DatePicker();
    @FXML private DatePicker dateAnalyseEnd = new DatePicker();



    int blockCount;
    String strPathAppData;
    String strPathDefid;
    String strPathDefiCli;
    int latestBlock;
    Transaction[] transactions = new Transaction[0];
    Gson gson = new Gson();

    public void btnUpdatePressed(){
        System.out.println("updatePressder");
        this.AnchorPanelUpdateDatabase.toFront();
        this.progressBar.setProgress(0.3);

    }
    public void btnAnalysePressed(){
        System.out.println("analysePressder");
        this.anchorPanelAnalyse.toFront();
        this.progressBar.setProgress(0.6);
    }
    public void btnExportPressed(){
        System.out.println("exportPressder");
        this.anchorPanelExport.toFront();
        this.progressBar.setProgress(1);
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.progressBar.setProgress(1);

        File file = new File(System.getProperty("user.dir")+"\\src\\icons\\acceppt.png");
        Image image = new Image(file.toURI().toString() );
        this.imgViewObj.setImage(image);

        this.strCurrentBlockLocally.setText("Current Block locally: " + this.blockCount );
        this.strUpToDate.setText("Database up to date");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.AnchorPanelUpdateDatabase.toFront();

        this.strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
        this.strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
        this.strPathDefiCli = System.getProperty("user.dir")+"\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";

        this.cmbCoins.getItems().addAll("BTC","DFI","ETH","USTD");
        this.cmbCoins.getSelectionModel().select(1);
        // generate folder %appData%//defiPortfolio if no one exists
        File directory = new File(strPathAppData);
        if (! directory.exists()){
            directory.mkdir();
        }

        try (Reader reader = new FileReader(strPathAppData+ "portfolioData.json")) {
            // Convert JSON File to Java Object
            this.transactions = gson.fromJson(reader, Transaction[].class);
            this.latestBlock = this.transactions[0].blockHeight;
            this.strCurrentBlockLocally.setText("Current Block locally: " + this.latestBlock );

            this.dateExpStart.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) > 0 );

                }
            });
            this.dateExpEnd.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) > 0 );
                }
            });
            this.dateAnalyseStart.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) > 0 );
                }
            });
            this.dateAnalyseEnd.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) > 0 );
                }
            });

            /////////  functions on transactions   /////////////
            java.util.List<Transaction> transactionList = Arrays.asList(transactions);
            java.util.List transactionsInTime = TransactionMethods.getTransactionsInTime(transactionList, 1610050820, 1610750913);
            java.util.List transactionsOfTypeRewards = TransactionMethods.getTransactionsOfType(transactionList,"Rewards");
            java.util.List transactionsOfTypeCommision = TransactionMethods.getTransactionsOfType(transactionList,"Commission");
            List transactionsBetweenBlocks = TransactionMethods.getTransactionsBetweenBlocks(transactionList,575620, 575693);
            TransactionMethods.exportToExcel(transactionsInTime, strPathAppData);
            //createExcelFile(filteredTansactions,strPathAppData);
            ////////////////////////

            String line;
            Process p = Runtime.getRuntime().exec(strPathDefiCli + " getblockcount");

            StringBuilder processOutput = new StringBuilder();

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));)
            {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null)
                {
                    processOutput.append(readLine + System.lineSeparator());
                }

                p.waitFor();
            }

            this.blockCount = Integer.parseInt(processOutput.toString().trim());
            int blockHeight = this.blockCount-latestBlock;
            this.strCurrentBlockOnChain.setText("Current block on Blockchain: " + this.blockCount );


            p = Runtime.getRuntime().exec(strPathDefiCli+ " listaccounthistory mine {\\\"depth\\\":"+blockHeight+100+",\\\"limit\\\":"+(blockHeight+10)*3+"}");

            processOutput = new StringBuilder();

            try (BufferedReader processOutputReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));)
            {
                String readLine;

                while ((readLine = processOutputReader.readLine()) != null)
                {
                    processOutput.append(readLine + System.lineSeparator());
                }

                p.waitFor();
            }

            String jsonTransaction = processOutput.toString().trim();




            Transaction[] transactionsNew = gson.fromJson(jsonTransaction, Transaction[].class);

            //Loop for Blockheight




            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
            // Java objects to File
            try (FileWriter writer = new FileWriter(strPathAppData+ "test.json")) {
                gsonWriter.toJson(transactions, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
