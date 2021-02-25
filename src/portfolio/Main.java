package portfolio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import portfolio.controllers.SettingsController;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = null;
        // Splashscreen
        Splash task = new Splash();
        Thread t = new Thread(task);
        t.start();

        // Main Window
        try {
            root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        Scene scene = new Scene(root);
        stage = new Stage();

        final Delta dragDelta = new Delta();
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("DeFi-Portfolio " + SettingsController.getInstance().Version);


        stage.getIcons().add(new Image(new File( System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png").toURI().toString()));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        stage.setOnCloseRequest(we -> {
            if(SettingsController.getInstance().getPlatform().equals("win")){
                try {
                    Runtime.getRuntime().exec("cmd /c taskkill /f /im java.exe");
                } catch (IOException e) {
                    e.printStackTrace();
                }}
        });
        task.kill();

        // Disclaimer anzeigen
        if(SettingsController.getInstance().showDisclaim) {
            Parent rootDisclaimer = FXMLLoader.load(getClass().getResource("views/DisclaimerView.fxml"));
            Scene sceneDisclaimer = new Scene(rootDisclaimer);
            Stage stageDisclaimer = new Stage();
            stageDisclaimer.setTitle("DeFi-Portfolio Disclaimer");
            stageDisclaimer.setScene(sceneDisclaimer);
            stageDisclaimer.initStyle(StageStyle.UNDECORATED);
            sceneDisclaimer.setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = stageDisclaimer.getX() - mouseEvent.getScreenX();
                dragDelta.y = stageDisclaimer.getY() - mouseEvent.getScreenY();
            });
            sceneDisclaimer.setOnMouseDragged(mouseEvent -> {
                stageDisclaimer.setX(mouseEvent.getScreenX() + dragDelta.x);
                stageDisclaimer.setY(mouseEvent.getScreenY() + dragDelta.y);
            });
            stageDisclaimer.show();
            stageDisclaimer.setAlwaysOnTop(true);

            if (SettingsController.getInstance().selectedStyleMode.getValue().equals("Dark Mode")) {
                java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
                stageDisclaimer.getScene().getStylesheets().add(darkMode.toURI().toString());
            } else {
                java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
                stageDisclaimer.getScene().getStylesheets().add(lightMode.toURI().toString());
            }
        }
    }

    static class Delta { double x, y; }


    public static void main(String[] args) {
        launch(args);
    }
}