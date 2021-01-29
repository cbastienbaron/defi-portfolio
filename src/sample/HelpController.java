package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.util.Duration;


public class HelpController {

    @FXML
    private Button btnMailTo, btnCopy;

    public HelpController() throws MalformedURLException {

    }
    public void btnMailToCallback() throws IOException, URISyntaxException {
        System.out.println("asdsad");
        Desktop desktop;
        if (Desktop.isDesktopSupported()
                && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
            URI mailto = new URI("mailto:defiportfoliomanagement@gmail.com?subject=");
            desktop.mail(mailto);
        } else {
            // TODO fallback to some Runtime.exec(..) voodoo?
            throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
        }
    }
    public void btnCopyCallback(){
        String myString = "defiportfoliomanagement@gmail.com";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
