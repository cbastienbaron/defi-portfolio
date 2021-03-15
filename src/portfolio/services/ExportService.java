package portfolio.services;

import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import portfolio.controllers.MainViewController;
import portfolio.controllers.TransactionController;
import portfolio.models.PortfolioModel;
import portfolio.views.MainView;
import portfolio.models.PoolPairModel;
import portfolio.models.TransactionModel;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class ExportService {

    MainViewController mainViewController;

    public ExportService(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        File exportFile = new File(exportPath);
        this.mainViewController.settingsController.lastExportPath = exportFile.getParent();
        this.mainViewController.settingsController.saveSettings();
        if(exportFile.exists()) exportFile.delete();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(exportPath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        for (TableColumn column : this.mainViewController.mainView.rawDataTable.getColumns()
        ) {
            sb.append(column.getText()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
        }

        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());

        for (TransactionModel transaction : transactions) {
            sb = new StringBuilder();
            sb.append(this.mainViewController.transactionController.convertTimeStampToString(transaction.getBlockTimeValue())).append(exportSplitter);
            sb.append(transaction.getTypeValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", transaction.getCryptoValueValue())).append(exportSplitter);
            sb.append(transaction.getCryptoCurrencyValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(exportSplitter);
            sb.append(transaction.getFiatCurrencyValue()).append(exportSplitter);
            sb.append(transaction.getPoolIDValue()).append(exportSplitter);
            sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
            sb.append(transaction.getBlockHashValue()).append(exportSplitter);
            sb.append(transaction.getOwnerValue()).append(exportSplitter);
            sb.append(transaction.getTxIDValue());
            sb.append("\n");
            writer.write(sb.toString());
            sb = null;
        }
        writer.close();
        return true;

    }

    public boolean exportTransactionToExcelDaily(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        File exportFile = new File(exportPath);
        this.mainViewController.settingsController.lastExportPath = exportFile.getParent();
        this.mainViewController.settingsController.saveSettings();
        if (exportFile.exists()) exportFile.delete();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(exportPath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        for (TableColumn column : this.mainViewController.mainView.rawDataTable.getColumns()
        ) {
            sb.append(column.getText()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
        }

        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        TreeMap<String, TransactionModel> exportList = new TreeMap<>();
        String oldDate = "";
        for (TransactionModel transaction : transactions) {
            String newDate = this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(transaction.getBlockTimeValue());

            if(transaction.getTypeValue().equals("Commission") || transaction.getTypeValue().equals("Rewards")){

            if ((oldDate.equals("") || oldDate.equals(newDate)) ) {
                String key = this.mainViewController.transactionController.getPoolPairFromId(transaction.getPoolIDValue()) + transaction.getCryptoCurrencyValue() + transaction.getTypeValue();
                if (!exportList.containsKey(key)) {
                     exportList.put(key,new TransactionModel(transaction.getBlockTimeValue(),transaction.getOwnerValue(),transaction.getTypeValue(),transaction.getAmountValue(),transaction.getBlockHashValue(),transaction.getBlockHeightValue(),transaction.getPoolIDValue(),transaction.getTxIDValue(),this.mainViewController.transactionController));
                } else {
                    exportList.get(key).setCrypto(exportList.get(key).getCryptoValueValue() + transaction.getCryptoValueValue());
                    exportList.get(key).setFiatValue(exportList.get(key).getFiatValueValue() + transaction.getFiatValueValue());
                }
            } else {
                for (HashMap.Entry<String, TransactionModel> entry : exportList.entrySet()) {

                sb = new StringBuilder();
                sb.append(this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(entry.getValue().getBlockTimeValue())).append(exportSplitter);
                sb.append(entry.getValue().getTypeValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", entry.getValue().getCryptoValueValue())).append(exportSplitter);
                sb.append(entry.getValue().getCryptoCurrencyValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", entry.getValue().getFiatValueValue())).append(exportSplitter);
                sb.append(entry.getValue().getFiatCurrencyValue()).append(exportSplitter);
                sb.append(entry.getValue().getPoolIDValue()).append(exportSplitter);
                sb.append(entry.getValue().getBlockHeightValue()).append(exportSplitter);
                sb.append(entry.getValue().getBlockHashValue()).append(exportSplitter);
                sb.append(entry.getValue().getOwnerValue()).append(exportSplitter);
                sb.append(entry.getValue().getTxIDValue());
                sb.append("\n");
                writer.write(sb.toString());
                sb = null;

                }
                exportList = new TreeMap<>();
            }

            }else{
                sb = new StringBuilder();
                sb.append(this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(transaction.getBlockTimeValue())).append(exportSplitter);
                sb.append(transaction.getTypeValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getCryptoValueValue())).append(exportSplitter);
                sb.append(transaction.getCryptoCurrencyValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(exportSplitter);
                sb.append(transaction.getFiatCurrencyValue()).append(exportSplitter);
                sb.append(transaction.getPoolIDValue()).append(exportSplitter);
                sb.append(transaction.getBlockHeightValue()).append(exportSplitter);
                sb.append(transaction.getBlockHashValue()).append(exportSplitter);
                sb.append(transaction.getOwnerValue()).append(exportSplitter);
                sb.append(transaction.getTxIDValue());
                sb.append("\n");
                writer.write(sb.toString());
                sb = null;
            }

            oldDate = newDate;
        }

        for (HashMap.Entry<String, TransactionModel> entry : exportList.entrySet()) {

            sb = new StringBuilder();
            sb.append(this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(entry.getValue().getBlockTimeValue())).append(exportSplitter);
            sb.append(entry.getValue().getTypeValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", entry.getValue().getCryptoValueValue())).append(exportSplitter);
            sb.append(entry.getValue().getCryptoCurrencyValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", entry.getValue().getFiatValueValue())).append(exportSplitter);
            sb.append(entry.getValue().getFiatCurrencyValue()).append(exportSplitter);
            sb.append(entry.getValue().getPoolIDValue()).append(exportSplitter);
            sb.append(entry.getValue().getBlockHeightValue()).append(exportSplitter);
            sb.append(entry.getValue().getBlockHashValue()).append(exportSplitter);
            sb.append(entry.getValue().getOwnerValue()).append(exportSplitter);
            sb.append(entry.getValue().getTxIDValue());
            sb.append("\n");
            writer.write(sb.toString());
            sb = null;

        }
        writer.close();
        exportList.clear();
        return true;

    }

    public boolean exportPoolPairToExcel(List<PoolPairModel> poolPairModelList, String exportPath, String source, MainView mainView) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            Locale localeDecimal = Locale.GERMAN;
            if (this.mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                localeDecimal = Locale.US;
            }
            switch (mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Commissions":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
            switch (source) {
                case "Overview":
                case "Übersicht":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValue().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Rewards":
                case "Belohnungen":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;

                case "Commissions":
                case "Kommissionen":
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValueValue() + poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue1())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValueValue2())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
            }
            writer.write(sb.toString());
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}