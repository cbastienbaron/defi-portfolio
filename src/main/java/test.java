import java.io.IOException;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class test {

	public static void main(String[] args) {

	    String strPathAppData = System.getenv("APPDATA")+ "\\defi-portfolio\\";
	    String strFileLoadData = strPathAppData+ "\\loadData.bat";
	    String strFileBlockCount = strPathAppData+ "\\loadData.bat";
	    	
	      try {
	    	  
//	    	  FileWriter myWriter = new FileWriter(strFileBlockCount);
//	    	  myWriter.write("C:\\Users\\User\\Desktop\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe getblockcount > " +strPathAppData + "blockCount.txt");
//	    	  myWriter.close();
//
//	          Runtime.getRuntime().exec(new String[] {"cmd","/k", "Start "+strFileBlockCount});
//
//
//
//	    	  FileReader f = new FileReader(strPathAppData + "blockCount.txt");
//	    	  int c;
//	    	  String strBlockCount="";
//	    	  while ((c = f.read()) != -1) {
//
//	    		  strBlockCount = strBlockCount+(char)c;
//	    		         }
//	    	  strBlockCount = strBlockCount.replace("\n","");
//	    	  strBlockCount = strBlockCount.replace("\r","");
//	    	  strBlockCount = "100";
//
//	    	  int blockCount = Integer.parseInt(strBlockCount);
//
//	    	  myWriter = new FileWriter(strFileLoadData);
//	          myWriter.write("C:\\Users\\User\\Desktop\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe listaccounthistory mine {\\\"limit\\\":"+strBlockCount+"} >" +strPathAppData+ "walletData.json");
//	          myWriter.close();
//
//	          Runtime.getRuntime().exec(new String[] {"cmd","/k", "Start "+strFileLoadData});

			  Gson gson = new Gson();

			  try (Reader reader = new FileReader(strPathAppData+ "walletData.json")) {

				  // Convert JSON File to Java Object
				  Transaction[] transactions = gson.fromJson(reader, Transaction[].class);

				  // print staff
				  System.out.println(transactions);

			  } catch (IOException e) {
				  e.printStackTrace();
			  }







			  //Transaction[] founderArray = gson.fromJson(jsonReader, Transaction[].class);
				int a=2;



		  } catch (Exception e) {
	  			e.printStackTrace();
	  		}

	
		
	}

}
