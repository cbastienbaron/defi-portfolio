import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionMethods {

    public static List getTransactionsInTime(List<Transaction> transactions, long startTime, long endTime){
        List<Transaction> filteredTransactions = new ArrayList<>();
        for(int ilist = 1;ilist < transactions.size();ilist++){
            if(transactions.get(ilist).blockTime >= startTime && transactions.get(ilist).blockTime <= endTime){
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static List getTransactionsOfType(List<Transaction> transactions, String type){
        List<Transaction> filteredTransactions = new ArrayList<>();
        for(int ilist = 1;ilist < transactions.size();ilist++){
            if(transactions.get(ilist).type.equals(type) ){
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }
    public static List getTransactionsOfOwner(List<Transaction> transactions, String owner){
        List<Transaction> filteredTransactions = new ArrayList<>();
        for(int ilist = 1;ilist < transactions.size();ilist++){
            if(transactions.get(ilist).type.equals(owner) ){
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }
    public static List getTransactionsBetweenBlocks(List<Transaction> transactions, long startBlock, long endBlock){
        List<Transaction> filteredTransactions = new ArrayList<>();
        for(int ilist = 1;ilist < transactions.size();ilist++){
            if(transactions.get(ilist).blockHeight >= startBlock && transactions.get(ilist).blockHeight <= endBlock){
                filteredTransactions.add(transactions.get(ilist));
            }
        }
        return filteredTransactions;
    }

    public static String convertTimeStampToString(long timeStamp){
        Date date = new Date(timeStamp*1000L);
        DateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY HH:MM:SS");
        String strDate = dateFormat.format(date);
        return strDate;

    }

    public static void exportToExcel(List<Transaction> transactions, String exportPath){
        try{
            PrintWriter writer = new PrintWriter(new File(exportPath + "\\transactionExport.csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("Date;Time;Type;Amount;Coin \n");

            for(int iTransaction = 0;iTransaction<transactions.size();iTransaction++) {
                sb.append(TransactionMethods.convertTimeStampToString(transactions.get(iTransaction).blockTime) + ";");
                sb.append(transactions.get(iTransaction).type + ";");

                for(int i = 0 ;i<transactions.get(iTransaction).amounts.length; i++){
                    String[] CoinsAndAmounts = TransactionMethods.splitCoinsAndAmounts(transactions.get(iTransaction).amounts[i].toString());
                    sb.append(CoinsAndAmounts[0]+ ";");
                    sb.append(CoinsAndAmounts[1]+ ";");
                }
                sb.append("\n");

            }
            writer.write(sb.toString());
            writer.close();
        }catch (FileNotFoundException e){
        }

    }

    public static String[] splitCoinsAndAmounts(String amountAndCoin){
        String[] splittedamountAndCoin = amountAndCoin.split("@");
        return splittedamountAndCoin;
    }


}
