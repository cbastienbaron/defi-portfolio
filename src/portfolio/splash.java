package portfolio;
import javafx.concurrent.Task;
import javax.swing.*;

public class splash extends Task{
    JFrame f;
    public splash(){

    }
    public void kill(){
        this.f.setVisible(false);
    }
    @Override
    public Void call() {
        this.f = new JFrame();
        Icon imgIcon = new ImageIcon(System.getProperty("user.dir") + "/src/icons/defi-flag.gif");
        JLabel label = new JLabel(imgIcon);
        f.setUndecorated(true);
        f.getContentPane().add(label);
        f.setSize(600, 330);
        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.setVisible(true);
        return null;
    }
}
