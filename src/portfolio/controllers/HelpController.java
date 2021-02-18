package portfolio.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HelpController {

    private static HelpController OBJ;

    static {
        OBJ = new HelpController();
    }
    public StringProperty strHelpText = new SimpleStringProperty();
    public StringProperty strCloseText= new SimpleStringProperty();
    public static HelpController getInstance() {
        return OBJ;
    }


}
