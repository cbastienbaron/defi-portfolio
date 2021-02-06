package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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
    public void start(Stage stage) throws Exception{
        Parent root = null;
        try{
            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        stage.setTitle("DeFi App Portfolio V1.0");
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();

       /* Parent root = FXMLLoader.load(getClass().getResource("SplashScreen.fxml"));
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();*/
    }

    public static void main(String[] args) {
       launch(args);
    }
}
