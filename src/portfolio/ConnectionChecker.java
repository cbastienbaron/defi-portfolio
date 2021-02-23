package portfolio;

import javafx.application.Platform;
import javafx.concurrent.Task;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TimerController;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionChecker extends Task {
    Timer timer = new Timer("Checker");

    public ConnectionChecker() {

    }

    @Override
    public Void call() {
        while (true) {

            try (Socket ignored = new Socket(SettingsController.getInstance().rpcbind, Integer.parseInt(SettingsController.getInstance().rpcport))) {
                if (!new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").exists()) {
                    new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").createNewFile();
                }
            } catch (IOException ignored) {
                if (new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").exists()) {
                    new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + ".cookie").delete();
                }
            }


        }
    }

}
