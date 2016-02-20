import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pendulum extends JPanel {
    
    double x, y;
    double vx, vy;
    double cx, cy;
    double a, f;
    
    BufferedImage img;
    
    Pendulum() {
        setPreferredSize(new Dimension(800, 800));
        img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
        
        x = 300.0;
        y = 400.0;
        
        vx = 15.0;
        vy = 5.0;
        
        cx = 400.0;
        cy = 400.0;
        
        a = 1.0;
        f = 0.999;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        
        Pendulum pendulum = new Pendulum();
        frame.add(pendulum);
        frame.pack();
        frame.setVisible(true);
        
        pendulum.run();
    }
    
    void run() {
        new Timer().scheduleAtFixedRate(new Animator(), 0, 15);
    }
    
    class Animator extends TimerTask {
        
        double c;
        
        double ax, ay;
        double n;
        Graphics2D g;
        
        Animator() {
            c = 0;
            
            g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        @Override
        public void run() {
            ax = cx - x;
            ay = cy - y;
            n = a / Math.sqrt(ax * ax + ay * ay);
            ax *= n;
            ay *= n;
            
            vx = vx * f + ax;
            vy = vy * f + ay;

            g.draw(new Line2D.Double(x, y, x + vx, y + vy));
            repaint();
            
            x = x + vx;
            y = y + vy;
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}
