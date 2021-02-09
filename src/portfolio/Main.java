package portfolio;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * <h1>Main</h1>
 *
 *
 *
 * @author  Daniel Klaiber & Arthur Eisener
 * @version 1.0
 * @since   2021-02-06
 */

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Parent root = null;
        // Splashscreen
        splash task = new splash();
        Thread t = new Thread(task);
        t.start();

       // Main Window
        try{
            root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setTitle("DeFi App Portfolio V1.0");
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);

        // Stop Splashsccreen
        task.kill();
    }

    public static void main(String[] args) {
       launch(args);
    }
}
