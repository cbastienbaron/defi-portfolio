package portfolio.controllers;

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
