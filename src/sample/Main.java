package sample;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    public void start(Stage stage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("SplashScreen.fxml"));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
       launch(args);
    }
}
