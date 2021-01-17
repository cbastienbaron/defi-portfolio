import java.io.IOException;
import java.io.FileWriter;
import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.GsonBuilder;

public class test {

	public static void main(String[] args) throws IOException {

		String strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
		String strPathDefid = System.getenv("LOCALAPPDATA")+"\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
		String strPathDefiCli = System.getProperty("user.dir")+"\\defi-portfoliomanager\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
		Gson gson = new Gson();
		int latestBlock;
		Transaction[] transactions = new Transaction[0];


		// generate folder %appData%//defiPortfolio if no one exists
		File directory = new File(strPathAppData);
		if (! directory.exists()){
			directory.mkdir();
		}
		// run defid.exe
		// Runtime.getRuntime().exec(strPathDefid);


		try (Reader reader = new FileReader(strPathAppData+ "portfolioData.json")) {
			// Convert JSON File to Java Object
			transactions = gson.fromJson(reader, Transaction[].class);
			latestBlock = transactions[0].blockHeight;

			/////////  functions on transactions   /////////////
			List<Transaction> transactionList = Arrays.asList(transactions);
			List transactionsInTime = TransactionMethods.getTransactionsInTime(transactionList, 1610050820, 1610750913);
			List transactionsOfTypeRewards = TransactionMethods.getTransactionsOfType(transactionList,"Rewards");
			List transactionsOfTypeCommision = TransactionMethods.getTransactionsOfType(transactionList,"Commission");
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

			int blockCount = Integer.parseInt(processOutput.toString().trim());
			int blockHeight = blockCount-latestBlock;


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
