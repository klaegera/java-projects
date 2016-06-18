import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, MouseListener {

    final int DIAM = 20;
    final double MOUSE = 0;
    final double FACTOR = 50;
    final double EXP = 100;

    ArrayList<Ball> balls = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Panel panel = new Panel();

        panel.setPreferredSize(new Dimension(800, 600));

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(panel).start();
        panel.addMouseListener(panel);
    }

    Panel() {
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < balls.size(); ++i) {
            Ball ball = balls.get(i);
            Color color = Color.BLACK;
            switch (ball.getCharge()) {
                case 1:
                    color = Color.RED;
                    break;
                case -1:
                    color = Color.BLUE;
                    break;
            }
            g.setColor(color);
            g.fillOval((int) ball.getX(), (int) ball.getY(), ball.getDiam(), ball.getDiam());
        }
    }

    @Override
    public void run() {
        while (true) {

            int mouseX = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
            int mouseY = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;

            for (int i = 0; i < balls.size(); ++i) {
                Ball first = balls.get(i);
                double vx = 0, vy = 0;

                for (int j = 0; j < balls.size(); ++j) {
                    if (i == j) {
                        continue;
                    }
                    Ball second = balls.get(j);
                    double dx, dy, dist;
                    second = balls.get(j);
                    dx = first.getX() - second.getX();
                    dy = first.getY() - second.getY();
                    dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist > 1) {
                        vx += dx / dist * Math.exp(-dist / EXP) * first.isPush(second);
                        vy += dy / dist * Math.exp(-dist / EXP) * first.isPush(second);
                    } else {
                        first.neutralize();
                        second.neutralize();
                    }
                }

                // walls
                vx += Math.exp(-first.getX() / EXP);
                vx -= Math.exp(-(getWidth() - DIAM - first.getX()) / EXP);
                vy += Math.exp(-first.getY() / EXP);
                vy -= Math.exp(-(getHeight() - DIAM - first.getY()) / EXP);

                first.add(vx, vy);
            }

            repaint();

            for (int i = 0; i < balls.size(); ++i) {
                Ball ball = balls.get(i);
                ball.apply();
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        switch (me.getButton()) {
            case 1:
                balls.add(new Ball(me.getX(), me.getY(), 20, 1));
                break;
            case 2:
                balls.add(new Ball(me.getX(), me.getY(), 20, 0));
                break;
            case 3:
                balls.add(new Ball(me.getX(), me.getY(), 20, -1));
                break;
        }
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
