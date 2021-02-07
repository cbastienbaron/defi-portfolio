package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingView implements Initializable {
    @FXML
    private ComboBox<String> cmbLanguage, cmbPrefCurrency,cmbDecSeperator,cmbCSVSeperator;
    public Button btnCancel = new Button();
    SettingsController settingsController = SettingsController.getInstance();

    public void btnCancelPressed(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void btnSaveAndApplyPressed() throws IOException {
        this.settingsController.saveSettings();
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.cmbLanguage.getItems().addAll(this.settingsController.languages);
        this.cmbLanguage.valueProperty().bindBidirectional(this.settingsController.selectedLanguage);

        this.cmbPrefCurrency.getItems().addAll(this.settingsController.currencies);
        this.cmbPrefCurrency.valueProperty().bindBidirectional(this.settingsController.selectedFiatCurrency);

        this.cmbDecSeperator.getItems().addAll(this.settingsController.decSeperators);
        this.cmbDecSeperator.valueProperty().bindBidirectional(this.settingsController.selectedDecimal);

        this.cmbCSVSeperator.getItems().addAll(this.settingsController.csvSeperators);
        this.cmbCSVSeperator.valueProperty().bindBidirectional(this.settingsController.selectedSeperator);

    }
}


