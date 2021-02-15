package portfolio.controllers;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelpController {

    public HelpController() {

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
}
