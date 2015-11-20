import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class Lagrange extends JFrame {

    List<Point> points = new ArrayList<>();
    BufferedImage img;
    int baseFunc = 0;

    public static void main(String[] args) {
        new Lagrange().interpolate();
    }

    Lagrange() {
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(3);
        setVisible(true);

        addMouseListener(new Mouse());
        addKeyListener(new Keyboard());
    }

    void interpolate() {
        img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = points.size();
        int prevY = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += points.get(i).y * polynomial(n, i, x);
            }
            g.drawLine(x - 1, prevY, x, (int) sum);
            prevY = (int) sum;
        }

        // node markers
        for (int i = 0; i < n; i++) {
            for (int dx = -2; dx < 3; dx++) {
                for (int dy = -2; dy < 3; dy++) {
                    img.setRGB(points.get(i).x + dx, points.get(i).y + dy, 0xFF0000);
                }
            }
        }

        if (baseFunc > 0 && baseFunc <= n) {
            g.setColor(Color.BLUE);
            g.drawLine(0, 200, img.getWidth(), 200);
            g.drawLine(0, 100, img.getWidth(), 100);
            g.setColor(Color.YELLOW);
            prevY = 0;
            for (int x = 0; x < img.getWidth(); x++) {
                int y = (int) (polynomial(n, baseFunc-1, x)*100+100);
                g.drawLine(x - 1, prevY, x, y);
                prevY = y;
            }
        }

        repaint();
    }

    double polynomial(int n, int i, int t) {
        double result = 1;
        for (int j = 0; j < n; j++) {
            if (j != i) {
                result *= (t - points.get(j).x) / (double) (points.get(i).x - points.get(j).x);
            }
        }
        return result;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, this);
    }

    class Mouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON1) {
                points.add(new Point(me.getX(), me.getY()));
            } else {
                points.clear();
            }
            interpolate();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }
    }

    class Keyboard implements KeyListener {

        @Override
        public void keyTyped(KeyEvent ke) {
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            if (Character.isDigit(ke.getKeyChar())) {
                baseFunc = Character.getNumericValue(ke.getKeyChar());
                interpolate();
            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }

    }
}
