package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML private Pane AnchorPanelUpdateDatabase, anchorPanelAnalyse , anchorPanelExport;
    @FXML private ProgressBar progressBar;



    public void btnUpdatePressed(){
        System.out.println("updatePressder");
        this.AnchorPanelUpdateDatabase.toFront();
        this.progressBar.setProgress(0.3);

    }
    public void btnAnalysePressed(){
        System.out.println("analysePressder");
        this.anchorPanelAnalyse.toFront();
        this.progressBar.setProgress(0.6);
    }
    public void btnExportPressed(){
        System.out.println("exportPressder");
        this.anchorPanelExport.toFront();
        this.progressBar.setProgress(1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
