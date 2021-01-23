package sample;

import java.io.*;
import java.util.*;

public class test {

	public static void main(String[] args)  {

		String strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
String strTransactions = strPathAppData + "data.portfolio";
		List transactionList = null;
		CoinPriceModel coinPriceHistory = null;

		String fiatCurrency = FiatCurrency.EUR.name();
		String decimalLetter = ","; // , or . for decimal and . or , for thousands
		String exportSplitter = ",";
		Locale localeDecimal = Locale.GERMAN;

		double DFI_Amount = 0;
		if(!decimalLetter.equals(",")) {
			 localeDecimal = Locale.US;
		}

		// generate folder %appData%//defiPortfolio if no one exists
		File directory = new File(strPathAppData);
		if (! directory.exists())
			directory.mkdir();

		
var upt= new TransactionController();

		var test2 = upt.updateTransactionData(strPathAppData,1000);

		//Export to Excel call
		ExportService.exportTransactionToExcel(transactionList, strPathAppData+ "\\transactionExport.csv",coinPriceHistory,fiatCurrency,localeDecimal,exportSplitter);

	}
}
