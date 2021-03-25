package portfolio.views;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import portfolio.controllers.HelpController;
import portfolio.controllers.SettingsController;

import java.awt.*;
import java.io.IOException;
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
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain.com/"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain.com/").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void defichainwiki() {
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain-wiki.com/wiki/DeFiChain-Portfolio"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain-wiki.com/wiki/DeFiChain-Portfolio").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
    public void github() {
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://github.com/DeFi-PortfolioManagement/defi-portfolio/blob/master/README.md"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/DeFi-PortfolioManagement/defi-portfolio/blob/master/README.md").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnTelegram()  {
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://t.me/DeFiChainPortfolio"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://t.me/DeFiChainPortfolio").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }



    public void Close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void defichainexplained() {
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain-explained.com/"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain-explained.com/").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}


