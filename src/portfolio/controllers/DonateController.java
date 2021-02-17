package portfolio.controllers;

public class DonateController {
    private static DonateController OBJ;

    static {
        OBJ = new DonateController();
    }
    public String strDonateText;
    public static DonateController getInstance() {
        return OBJ;
    }

}
