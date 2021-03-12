package portfolio.controllers;

import javafx.application.Platform;

import java.io.File;
import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    MainViewController mainViewController;

    public CheckConnection(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (SettingsController.getInstance().runCheckTimer) {
                        if (!this.mainViewController.transactionController.checkRpc()) {
                            if (SettingsController.getInstance().errorBouncer < 30) {
                                System.out.println("Try to connect to Server");
                                SettingsController.getInstance().errorBouncer++;
                            } else {
                                SettingsController.getInstance().runCheckTimer = false;
                                SettingsController.getInstance().errorBouncer = 0;
                                File file = new File(System.getProperty("user.dir") + "/PortfolioData/"+"update.portfolio");
                                if(file.exists())file.delete();
                            }
                        } else {
                            SettingsController.getInstance().runCheckTimer = false;
                            SettingsController.getInstance().errorBouncer = 0;
                            this.mainViewController.btnUpdateDatabasePressed();
                            this.mainViewController.plotUpdate(this.mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getText());
                            File file = new File(System.getProperty("user.dir") + "/PortfolioData/"+"update.portfolio");
                            if(file.exists())file.delete();
                        }
                    }
                }
        );
    }
}
