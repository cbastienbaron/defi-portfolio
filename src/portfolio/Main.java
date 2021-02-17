package portfolio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        stage.setTitle("DeFi App Portfolio V1.0");
        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png"));
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
        stage.setScene(scene);

        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        stage.setOnCloseRequest(we -> {
            try {
                Runtime.getRuntime().exec("cmd /c taskkill /f /im java.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        task.kill();

    }

    static class Delta { double x, y; }

    public static void main(String[] args) {
        launch(args);
    }
}