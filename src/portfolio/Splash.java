package portfolio;
import javafx.concurrent.Task;
import javax.swing.*;

public class Splash extends Task{
    JFrame f;
    public Splash(){

    }
    public void kill(){
        this.f.setVisible(false);
    }
    @Override
    public Void call() {
        this.f = new JFrame();
        Icon imgIcon = new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/defi-flag.gif");
        JLabel label = new JLabel(imgIcon);
        f.setUndecorated(true);
        f.getContentPane().add(label);
        f.setSize(600, 330);
        f.setLocationRelativeTo(null);
        f.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png").getImage());
        f.setAlwaysOnTop(true);
        f.setVisible(true);
        return null;
    }
}
