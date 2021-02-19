package portfolio;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    public String strPathDefidMac = System.getProperty("user.home") + "/../../Applications/defi-app.app/Contents/Resources/binary/mac/defid";

    @Override
    public void start(Stage stage) {

        Parent root = null;
        // Splashscreen
        Splash task = new Splash();
        Thread t = new Thread(task);
        t.start();
        System.out.println(getClass().getResource("views/MainView.fxml") );
        // Main Window
        try{
            root = FXMLLoader.load(new File(System.getProperty("user.home") + "/Desktop/Defi/out/production/defi-portfolio/portfolio/views/MainView.fxml").toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert root != null;
        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setTitle("DeFi App Portfolio V1.0");
        //stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png")); TODO:
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