import sample.CoinPrice;
import sample.Update;

import java.io.*;
import java.util.*;

public class test {

	public static void main(String[] args)  {

		String strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";

		List transactionList = null;
		CoinPrice coinPriceHistory;

		String fiatCurrency = "EUR";
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

		coinPriceHistory = Update.updateCoinHistory(strPathAppData);
		transactionList = Update.updatePortfolioData(strPathAppData);

		int a =2;
		//Export to Excel call
		//Export.exportToExcel(transactionList, strPathAppData+ "\\transactionExport.csv",coinPriceHistory,fiatCurrency,localeDecimal,exportSplitter);

	}
}
