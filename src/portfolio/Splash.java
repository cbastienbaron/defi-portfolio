package portfolio;
import javafx.concurrent.Task;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Splash extends Task{
    static JFrame f;
    public int posX,posY;
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

        f.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                posX=e.getX();
                posY=e.getY();
            }
        });
        f.addMouseMotionListener(new MouseAdapter()
        {
            public void mouseDragged(MouseEvent evt)
            {
                //sets frame position when mouse dragged
                f.setLocation (evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);

            }
        });
        return null;
    }
}
