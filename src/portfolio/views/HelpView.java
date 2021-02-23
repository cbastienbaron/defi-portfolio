package portfolio.views;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import portfolio.controllers.DonateController;
import portfolio.controllers.HelpController;
import portfolio.controllers.SettingsController;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpView implements Initializable {

    public Button btnClose;
    public AnchorPane anchorPane;
    HelpController helpController = HelpController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClose.getTooltip().textProperty().bindBidirectional(helpController.strCloseText);
    }

    public void btnMailToCallback() throws IOException {

        String mailto = "mailto:defiportfoliomanagement@gmail.com?subject=DeFi-Portfolio-" + SettingsController.getInstance().Version;
        String cmd = "";
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            cmd = "cmd.exe /c start \"\" \"" + mailto + "\"";
        } else if (os.contains("osx")) {
            cmd = "open " + mailto;
        } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
            cmd = "xdg-open " + mailto;
        }
        Runtime.getRuntime().exec(cmd);
    }


    public void defichain() {
        try {
            Desktop.getDesktop().browse(new URL("https://defichain.com/").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void defichainwiki() {
        try {
            Desktop.getDesktop().browse(new URL("https://defichain-wiki.com/wiki/Main_Page").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public void github() {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/DeFi-PortfolioManagement/defi-portfolio/blob/master/README.md").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void btnTelegram()  {

        try {
            Desktop.getDesktop().browse(new URL("https://t.me/DeFiChainPortfolio").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }



    public void Close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

}


