package portfolio.views;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import portfolio.controllers.DonateController;
import portfolio.controllers.HelpController;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
    HelpController helpController = HelpController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Contact.setText(helpController.strHelpText);
    }

    public void btnMailToCallback() throws IOException, URISyntaxException {
        Desktop desktop;
        if (Desktop.isDesktopSupported()
                && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
            URI mailto = new URI("mailto:defiportfoliomanagement@gmail.com?subject=");
            desktop.mail(mailto);
        }
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


