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
<<<<<<< Updated upstream
        stage.setTitle("DeFi App Portfolio V1.0");
        //stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png")); TODO:
=======

        final Delta dragDelta = new Delta();
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("DeFi-Portfolio " + SettingsController.getInstance().Version);
        stage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/icons/DefiIcon.png").toURI().toString()));
        Stage finalStage = stage;
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = finalStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = finalStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            finalStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            finalStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
>>>>>>> Stashed changes
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
<<<<<<< Updated upstream
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    Runtime.getRuntime().exec("cmd /c taskkill /f /im java.exe");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
=======
        stage.setOnCloseRequest(we -> {
           /* try {
                Runtime.getRuntime().exec("cmd /c taskkill /f /im java.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
>>>>>>> Stashed changes
        });

       // task.kill();

    }

    public static void main(String[] args) {
        launch(args);
    }
}