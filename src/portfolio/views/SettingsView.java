package portfolio.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import portfolio.controllers.DonateController;
import portfolio.controllers.HelpController;
import portfolio.controllers.SettingsController;

public class SettingsView implements Initializable {
    public Button btnSaveAndApply;
    public Label labelLanguage;
    public Label CSV;
    public Label prefferedCurrency;
    public Label prefferedStyle;
    public Label labelDec;
    public AnchorPane anchorPane;
    @FXML
    private ComboBox<String> cmbLanguage, cmbPrefCurrency,cmbDecSeperator,cmbCSVSeperator,cmbPrefferedStyle;
    SettingsController settingsController = SettingsController.getInstance();

    public void btnSaveAndApplyPressed(){
        this.settingsController.saveSettings();
        Stage stage = (Stage) btnSaveAndApply.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

         btnSaveAndApply.setText(this.settingsController.translationList.getValue().get("Close").toString());
         labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
         CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
         prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
        labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
        this.cmbLanguage.getItems().addAll(this.settingsController.languages);
        this.cmbLanguage.valueProperty().bindBidirectional(this.settingsController.selectedLanguage);

        this.cmbPrefCurrency.getItems().addAll(this.settingsController.currencies);
        this.cmbPrefCurrency.valueProperty().bindBidirectional(this.settingsController.selectedFiatCurrency);

        this.cmbDecSeperator.getItems().addAll(this.settingsController.decSeperators);
        this.cmbDecSeperator.valueProperty().bindBidirectional(this.settingsController.selectedDecimal);

        this.cmbCSVSeperator.getItems().addAll(this.settingsController.csvSeperators);
        this.cmbCSVSeperator.valueProperty().bindBidirectional(this.settingsController.selectedSeperator);

        this.cmbPrefferedStyle.getItems().addAll(this.settingsController.styleModes);
        this.cmbPrefferedStyle.valueProperty().bindBidirectional(this.settingsController.selectedStyleMode);

    }

    public void changeLanguage(ActionEvent actionEvent) {
        this.btnSaveAndApply.textProperty().setValue(this.settingsController.translationList.getValue().get("Close").toString());
        this.labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
        this.CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
        this.prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
        labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
    }

}


