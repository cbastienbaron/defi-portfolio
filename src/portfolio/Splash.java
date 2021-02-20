package portfolio;
import javafx.concurrent.Task;
import javax.swing.*;

public class Splash extends Task{
    static JFrame f;
    static boolean isKilled = false;
    public Splash(){

    }
    public void kill(){
        isKilled = true;
        if(this.f != null) {
            this.f.setVisible(false);
        }
    }
    @Override
    public Void call() {
        if(isKilled) return null;
        this.f = new JFrame();
        Icon imgIcon = new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/defi-flag.gif");
        JLabel label = new JLabel(imgIcon);
        f.setUndecorated(true);
        f.getContentPane().add(label);
        f.setSize(600, 330);
        f.setLocationRelativeTo(null);
        f.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png").getImage());
        f.setAlwaysOnTop(true);
        f.setVisible(!isKilled);
        return null;
    }
}
