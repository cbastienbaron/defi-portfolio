package sample;

import javafx.application.Platform;

import java.util.TimerTask;

public class TimerController extends TimerTask {
    ViewModel viewModel;
 public TimerController(ViewModel viewModel){
        this.viewModel = viewModel;
    }
    @Override
    public void run() {
        Platform.runLater(() ->    viewModel.strCurrentBlockOnBlockchain.set(viewModel.transactionController.getBlockCountRpc()));
    }
}
