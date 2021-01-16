import java.io.IOException;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.GsonBuilder;

public class test {

	public static void main(String[] args) {

	    String strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
	    String strFileLoadData = strPathAppData+ "\\loadData.bat";
	    String strFileBlockCount = strPathAppData+ "\\loadData.bat";
		Gson gson = new Gson();
		int latestBlock;
		Transaction[] transactions = new Transaction[0];

		try (Reader reader = new FileReader(strPathAppData+ "portfolioData.json")) {
				  // Convert JSON File to Java Object
			transactions = gson.fromJson(reader, Transaction[].class);
			latestBlock = transactions[0].blockHeight;

			String line;
			Process p = Runtime.getRuntime().exec("C:\\Users\\User\\Desktop\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe getblockcount");

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


			 p = Runtime.getRuntime().exec("C:\\Users\\User\\Desktop\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe listaccounthistory mine {\\\"depth\\\":"+blockHeight+100+",\\\"limit\\\":"+(blockHeight+10)*3+"}");

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
