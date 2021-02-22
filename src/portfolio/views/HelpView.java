package portfolio.views;

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

    public Button btnCopyHelp;
    public Label Contact;
    public Button btnWriteHelp;
    public Button btnClose;
    public AnchorPane anchorPane;
    public Label Contact1;
    HelpController helpController = HelpController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Contact.textProperty().bindBidirectional (helpController.strHelpText);
        btnClose.textProperty().bindBidirectional(helpController.strCloseText);
    }

    public void btnMailToCallback() throws IOException, URISyntaxException {
//        Desktop desktop;
//        if (Desktop.isDesktopSupported()
//                && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
//
//            URI mailto = new URI("mailto:defiportfoliomanagement@gmail.com?subject=DeFi-Portfolio-"+SettingsController.getInstance().Version);
//            desktop.mail(mailto);
//        }

        String mailto = "mailto:defiportfoliomanagement@gmail.com?subject=DeFi-Portfolio-"+SettingsController.getInstance().Version;

        //Run-Befehl je nach Betriebssystem erzeugen
        String cmd = "";
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            cmd = "cmd.exe /c start \"\" \"" + mailto + "\"";
        }
        else if (os.contains("osx")){
            cmd = "open " + mailto;
        }
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
            cmd = "xdg-open " + mailto;
        }
        //Mail-Client mit Parametern starten
        Runtime.getRuntime().exec(cmd);
    }
    public void btnCopyCallback(){
        String myString = "defiportfoliomanagement@gmail.com";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void Close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

}


