import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Minesweeper2 extends JFrame {

    int xTiles, yTiles;
    int mineCount;
    Color tileColor;
    int tileSize;

    int xPos = 0, yPos = 0;
    int rCount = 0;

    int progress;

    int gameTime = 0;
    Timer gameTimer;

    boolean modify = true, bot;

    Tile[][] grid;
    Panel panel;

    Minesweeper2(int xTiles, int yTiles, int mineCount, Color tileColor, int tileSize, boolean bot) {

        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.mineCount = mineCount;
        this.tileColor = tileColor;
        this.tileSize = tileSize;
        this.bot = bot;

        setMaximumSize(new Dimension(xTiles * tileSize + 17, yTiles * tileSize + 39));
        setSize(500, 500);

        setTitle("Minesweeper");

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new Panel();
        add(panel);
        this.addMouseListener(new Mouse());
        setVisible(true);
    }

    private void init(int x0, int y0) {

        gameTimer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                gameTime++;
                progress();
            }
        });
        gameTimer.start();

        grid = new Tile[xTiles][yTiles];

        // first click -> 9 tiles empty
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                grid[mod(x0 + dx, xTiles)][mod(y0 + dy, yTiles)] = new Tile(false);
            }
        }

        // mine distribution
        for (int m = 0; m < mineCount; m++) {
            int rx, ry;
            do {
                rx = (int) (Math.random() * xTiles);
                ry = (int) (Math.random() * yTiles);
            } while (grid[rx][ry] != null || rx == xTiles || ry == yTiles);
            grid[rx][ry] = new Tile(true);
        }

        // rest of the tiles
        for (int x = 0; x < xTiles; x++) {
            for (int y = 0; y < yTiles; y++) {
                if (grid[x][y] == null) {
                    grid[x][y] = new Tile(false);
                }
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        if (grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)] != null && grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].mine) {
                            grid[x][y].count++;
                        }
                    }
                }
            }
        }
    }

    private void reveal(int xm, int ym) {

        if (!grid[xm][ym].mine) {
            grid[xm][ym].hidden = false;
            if (grid[xm][ym].count == 0) {
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        if (grid[mod(xm + dx, xTiles)][mod(ym + dy, yTiles)].hidden) {
                            reveal(mod(xm + dx, xTiles), mod(ym + dy, yTiles));
                        }
                    }
                }
            }

        } else {
            for (int x = 0; x < xTiles; x++) {
                for (int y = 0; y < yTiles; y++) {
                    if (grid[x][y].mine) {
                        grid[x][y].hidden = false;
                    }
                }
            }
            finish(false);
        }
    }

    private void progress() {

        // progress calculation
        int revealed = 0;
        int minesRemaining = mineCount;

        for (int x = 0; x < xTiles; x++) {
            for (int y = 0; y < yTiles; y++) {
                if (!grid[x][y].hidden) {
                    revealed++;
                }
                if (grid[x][y].flagged) {
                    minesRemaining--;
                }
            }
        }
        progress = revealed * 100 / (xTiles * yTiles - mineCount);
        setTitle("Minesweeper - " + progress + "% - " + minesRemaining + " mines remaining - time played: " + gameTime);
        if (progress == 100) {
            finish(true);
        }
    }

    private void multrev(int xm, int ym) {
        boolean multrev = true;
        for (int x = 0; x < xTiles; x++) {
            for (int y = 0; y < yTiles; y++) {
                int flagCount = 0;
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        Tile n = grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)];
                        if (n.flagged) {
                            flagCount++;
                        }
                    }
                }
                if (flagCount > grid[x][y].count) {
                    multrev = false;
                }
            }
        }
        if (multrev) {
            int flagCount = 0;
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    Tile n = grid[mod(xm + dx, xTiles)][mod(ym + dy, yTiles)];
                    if (n.flagged) {
                        flagCount++;
                    }
                }
            }
            if (flagCount == grid[xm][ym].count) {
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        Tile n = grid[mod(xm + dx, xTiles)][mod(ym + dy, yTiles)];
                        if (!n.flagged) {
                            reveal(mod(xm + dx, xTiles), mod(ym + dy, yTiles));
                        }
                    }
                }
            }
        }
    }

    private void bot() {
        modify = false;
        boolean done;
        do {
            done = true;
            for (int x = 0; x < xTiles; x++) {
                for (int y = 0; y < yTiles; y++) {

                    if (!grid[x][y].hidden && grid[x][y].count > 0) {
                        int hiddenCount = 0;
                        int flagCount = 0;
                        for (int dx = -1; dx < 2; dx++) {
                            for (int dy = -1; dy < 2; dy++) {
                                if (grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].hidden) {
                                    hiddenCount++;
                                }
                                if (grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].flagged) {
                                    flagCount++;
                                }
                            }
                        }
                        if (flagCount < grid[x][y].count && hiddenCount == grid[x][y].count) {
                            for (int dx = -1; dx < 2; dx++) {
                                for (int dy = -1; dy < 2; dy++) {
                                    if (grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].hidden && !grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].flagged) {
                                        grid[mod(x + dx, xTiles)][mod(y + dy, yTiles)].flagged = true;
                                        done = false;
                                        panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException ex) {
                                        }
                                    }
                                }
                            }
                        } else if (hiddenCount > grid[x][y].count && flagCount == grid[x][y].count) {
                            multrev(x, y);
                            done = false;
                            panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                }
            }
        } while (!done);
        modify = true;
    }

    private void finish(boolean win) {
        if (modify) {
            gameTimer.stop();
            modify = false;
            new EndScreen(win, this);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
    }

    private class Panel extends JPanel {

        private Panel() {
            setBackground(Color.WHITE);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // set size
            Minesweeper2.this.setSize(Math.min(Minesweeper2.this.getWidth(), (int) Minesweeper2.this.getMaximumSize().getWidth()), Math.min(Minesweeper2.this.getHeight(), (int) Minesweeper2.this.getMaximumSize().getHeight()));

            g.setFont(new Font("Arial", Font.BOLD, tileSize));

            for (int xg = -xTiles * tileSize + xPos; xg < xTiles * tileSize + xPos + 1; xg += xTiles * tileSize) {
                for (int yg = -yTiles * tileSize + yPos; yg < yTiles * tileSize + yPos + 1; yg += yTiles * tileSize) {

                    // paint empty grid
                    if (grid == null) {
                        for (int x = 0; x < xTiles; x++) {
                            for (int y = 0; y < yTiles; y++) {
                                g.setColor(tileColor);
                                g.fillRect(x * tileSize + xg, y * tileSize + yg, tileSize, tileSize);
                                g.setColor(Color.BLACK);
                                g.drawRect(x * tileSize + xg, y * tileSize + yg, tileSize, tileSize);

                            }
                        }
                    } //
                    // paint grid
                    else {
                        for (int x = 0; x < xTiles; x++) {
                            for (int y = 0; y < yTiles; y++) {
                                if (grid[x][y].hidden) {
                                    g.setColor(tileColor);
                                    g.fillRect(x * tileSize + xg, y * tileSize + yg, tileSize, tileSize);
                                } else if (grid[x][y].count != 0 && !grid[x][y].mine) {
                                    g.setColor(Color.BLACK);
                                    if (grid[x][y].count == 1) {
                                        g.setColor(Color.decode("#4050bd"));
                                    }
                                    if (grid[x][y].count == 2) {
                                        g.setColor(Color.decode("#206503"));
                                    }
                                    if (grid[x][y].count == 3) {
                                        g.setColor(Color.decode("#aa0509"));
                                    }
                                    if (grid[x][y].count == 4) {
                                        g.setColor(Color.decode("#000080"));
                                    }
                                    if (grid[x][y].count == 5) {
                                        g.setColor(Color.decode("#800000"));
                                    }
                                    if (grid[x][y].count == 6) {
                                        g.setColor(Color.decode("#008080"));
                                    }
                                    if (grid[x][y].count == 7) {
                                        g.setColor(Color.decode("#B00000"));
                                    }
                                    if (grid[x][y].count == 8) {
                                        g.setColor(Color.decode("#B00000"));
                                    }

                                    g.drawString("" + grid[x][y].count, x * tileSize + 5 + xg, (y + 1) * tileSize - 2 + yg);
                                }
                                if (grid[x][y].mine && !grid[x][y].hidden) {
                                    g.setColor(Color.decode("#000000"));
                                    g.fillRect(x * tileSize + xg, y * tileSize + yg, tileSize, tileSize);
                                    g.setColor(Color.decode("#ff0000"));
                                    g.drawString("X", x * tileSize + 5 + xg, (y + 1) * tileSize - 3 + yg);
                                }
                                if (grid[x][y].flagged) {
                                    g.setColor(Color.RED);
                                    g.drawString("M", x * tileSize + 3 + xg, (y + 1) * tileSize - 3 + yg);
                                }
                                g.setColor(Color.BLACK);
                                g.drawRect(x * tileSize + xg, y * tileSize + yg, tileSize, tileSize);
                            }
                        }
                    }

                    g.setColor(Color.RED);
                    g.drawRect(xg, yg, xTiles * tileSize, yTiles * tileSize);
                }
            }
        }
    }

    private class Tile {

        boolean mine, hidden = true, flagged;
        int count;

        private Tile(boolean mine) {

            this.mine = mine;
        }
    }

    private class Mouse extends MouseAdapter {

        Timer timer;

        @Override
        public void mousePressed(MouseEvent evt) {

            int xm = mod((getMouseLocation().x - xPos), xTiles * tileSize) / tileSize;
            int ym = mod((getMouseLocation().y - yPos), yTiles * tileSize) / tileSize;

            if (grid == null && evt.getButton() == MouseEvent.BUTTON1) {
                init(xm, ym);
            }

            if (grid != null && modify) {
                if (grid[xm][ym].hidden) {
                    if (evt.getButton() == MouseEvent.BUTTON1 && !grid[xm][ym].flagged) {
                        reveal(xm, ym);
                    } else if (evt.getButton() == MouseEvent.BUTTON3) {
                        //flag(xm, ym);
                        grid[xm][ym].flagged = !grid[xm][ym].flagged;
                    }
                } else if (evt.getButton() == MouseEvent.BUTTON1 && modify) {
                    multrev(xm, ym);
                }
            }

            if (evt.getButton() == MouseEvent.BUTTON2) {
                timer = new Timer(40, new ActionListener() {

                    int prevX = getMouseLocation().x;
                    int prevY = getMouseLocation().y;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        xPos = mod(xPos + getMouseLocation().x - prevX, xTiles * tileSize);
                        yPos = mod(yPos + getMouseLocation().y - prevY, yTiles * tileSize);
                        prevX = getMouseLocation().x;
                        prevY = getMouseLocation().y;
                        repaint();
                    }
                });
                timer.start();
            }

            if (bot && modify) {
                bot();
            }
            progress();
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent evt
        ) {
            if (evt.getButton() == MouseEvent.BUTTON2) {
                timer.stop();
            }
        }

        Point getMouseLocation() {

            Point location = MouseInfo.getPointerInfo().getLocation();
            location.translate(-getLocationOnScreen().x - 8, -getLocationOnScreen().y - 30);
            return location;
        }
    }

    int mod(int a, int b) {

        int m = a % b;
        if (m < 0) {
            m += b;
        }
        return m;
    }
}
