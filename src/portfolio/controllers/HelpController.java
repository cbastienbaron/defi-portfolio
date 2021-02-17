package portfolio.controllers;

import javafx.scene.control.Button;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelpController {

    private static HelpController OBJ;

    static {
        OBJ = new HelpController();
    }
    public String strHelpText;
    public static HelpController getInstance() {
        return OBJ;
    }


}
