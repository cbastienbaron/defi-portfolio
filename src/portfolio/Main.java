package portfolio;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

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
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("DeFi App Portfolio V1.0");
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png"));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    Runtime.getRuntime().exec("cmd /c taskkill /f /im java.exe");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        task.kill();

    }

    public static void main(String[] args) {
        launch(args);
    }
}