package portfolio;

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
    public void start(Stage stage) {

        boolean showSplashScreen = true;
        Parent root;
        try{

            if(showSplashScreen) {
                // With Splashscreen
                root = FXMLLoader.load(getClass().getResource("views/SplashView.fxml"));
                stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
                Scene scene = new Scene(root);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();
            }else {
                // Without Spalshscreen
                root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("DeFi App Portfolio V1.0");
                stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
                stage.setScene(scene);
                stage.setMinHeight(700);
                stage.setMinWidth(1200);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       launch(args);
    }
}
