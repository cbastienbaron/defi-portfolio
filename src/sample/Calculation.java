package sample;

import java.util.List;
import java.util.Locale;

public class Calculation {
    public static double getTotalCoinAmount(List<Transaction> transactions, String coinName){

        double amountCoin=0;

        for(int iTransaction = 0;iTransaction<transactions.size();iTransaction++) {

            for(int i = 0 ;i<transactions.get(iTransaction).amounts.length; i++){


            String[] CoinsAndAmounts = TransactionMethods.splitCoinsAndAmounts(transactions.get(iTransaction).amounts[i].toString());

               if(coinName.equals(CoinsAndAmounts[1])) {
                   amountCoin += Double.parseDouble(CoinsAndAmounts[0]);
               }

            }

        }


        return amountCoin;
}}
