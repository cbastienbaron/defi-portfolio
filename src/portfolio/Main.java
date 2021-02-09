package portfolio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Parent root = null;
        // Splashscreen
        Splash task = new Splash();
        Thread t = new Thread(task);
        t.start();

        // Main Window
        try{
            root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setTitle("DeFi App Portfolio V1.0");
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        task.kill();
    }

    public static void main(String[] args) {
        launch(args);
    }
}