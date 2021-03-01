package portfolio.controllers;

import javafx.application.Platform;

import java.util.TimerTask;

public class TimerController extends TimerTask {
    MainViewController mainViewController;

    public TimerController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (SettingsController.getInstance().runTimer) {
                        mainViewController.strCurrentBlockOnBlockchain.set(mainViewController.transactionController.getBlockCount());
                        mainViewController.bDataBase.set(mainViewController.strCurrentBlockOnBlockchain.getValue().equals("No connection"));
                    }
                }
        );
    }
}
