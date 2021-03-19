package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.net.URL;
import java.util.ResourceBundle;

public class DisclaimerView implements Initializable {
    public Button btnClose;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblDisclaimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String strDisclaimer = "Disclaimer: \nThe content provided is for informational purposes only. \nWe do not guarantee the accuracy of the data. \n" +
                "\nNote: To update the data the defi-app/ defi-wallet \nand the defid must be closed.";
        lblDisclaimer.setText(strDisclaimer);
    }

    public void btnClose() {
        SettingsController.getInstance().showDisclaim = !hCheckBox.isSelected();
        SettingsController.getInstance().saveSettings();

        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}


