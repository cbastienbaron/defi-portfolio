package portfolio;

import javafx.application.Platform;
import javafx.concurrent.Task;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TimerController;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionChecker extends Task {
    Timer timer = new Timer("Checker");

    public ConnectionChecker() {

    }

    @Override
    public Void call() throws IOException {
        while (true) {

            try{
                URL url = new URL("http://"+SettingsController.getInstance().rpcbind+":"+SettingsController.getInstance().rpcport);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.connect();
                if (!new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").exists()) {
                    new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").createNewFile();
                }

            } catch (IOException ignored) {
                if (new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").exists() & ignored.getMessage().equals("Connection refused: connect")) {
                    new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").delete();
                }

            }


        }
    }

}
