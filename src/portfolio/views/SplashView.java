package portfolio.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashView implements Initializable {

@FXML
private AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
new SplassScreen().start();
    }

    class SplassScreen extends Thread{
        @Override
        public  void run(){
            try{
                Thread.sleep(3000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Parent root = null;
                        try{
                            root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("DeFi App Portfolio V1.0");
                        stage.getIcons().add(new Image("file:///" + System.getProperty("user.dir") + "/src/icons/DefiIcon.png"));
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1200);

                        stage.show();

                        rootPane.getScene().getWindow().hide();
                    }
                });


            }catch (InterruptedException ex){

            }
        }
    }
}
