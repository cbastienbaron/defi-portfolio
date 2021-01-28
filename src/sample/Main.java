package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Timestamp;

/**
 * <h1>Test</h1>
 *Test Text
 *
 *
 * @author  Daniel Klaiber & Arthur Eisener
 * @version 0.1
 * @since   2021-02-02
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Defi-Portfolio Management");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
