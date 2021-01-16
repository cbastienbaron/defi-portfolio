import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void convertTimeStampToString(Transaction[] transactions){
        Timestamp ts=new Timestamp(transactions[0].blockTime);
        Date date=new Date(ts.getTime());
        System.out.println(date);
    }


}
