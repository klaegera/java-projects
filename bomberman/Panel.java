import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Panel extends JPanel {

    private HashMap<String, Image> images = new HashMap<>();
    private int mapWidth;
    private int mapHeight;
    private int size;
    private int[][] map;
    private ArrayList<Point> bombs = new ArrayList<>();
    private ArrayList<Point> players = new ArrayList<>();

    Panel(int mapWidth, int mapHeight, int size) {

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.size = size;
        map = new int[mapWidth][mapHeight];

        setBackground(Color.BLACK);

        // load images
        String imgList
                = "Bomb.png,"
                + "Breakable.png,"
                + "C.png,"
                + "Floor.png,"
                + "K.png,"
                + "Lava.png,"
                + "P.png,"
                + "Player0.png,"
                + "Player1.png,"
                + "Player2.png,"
                + "Player3.png,"
                + "S.png,"
                + "T.png,"
                + "Unbreakable.png";
        try {
            for (String img : imgList.split(",")) {

                images.put(
                        img,
                        ImageIO.read(getClass().getClassLoader().getResourceAsStream("resources/" + img))
                        .getScaledInstance(size, size, Image.SCALE_FAST));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void draw(String drawString) {
        // format: map,playerCount,playerLocation(,...),bombCount(,bombLocation,...)
        String[] draw = drawString.split(",");

        // map
        for (int ym = 0; ym < mapHeight; ym++) {
            for (int xm = 0; xm < mapWidth; xm++) {
                map[xm][ym] = Character.getNumericValue(draw[0].charAt(ym * mapWidth + xm));
            }
        }

        // players
        players.clear();
        int playerCount = Integer.parseInt(draw[1]);
        for (int i = 0; i < playerCount; i++) {
            players.add(new Point(Integer.parseInt(draw[2 + i].split(" ")[0]), Integer.parseInt(draw[2 + i].split(" ")[1])));
        }

        // bombs
        bombs.clear();
        int bombCount = Integer.parseInt(draw[2 + playerCount]);
        for (int i = 0; i < bombCount; i++) {
            bombs.add(new Point(Integer.parseInt(draw[3 + playerCount + i].split(" ")[0]), Integer.parseInt(draw[3 + playerCount + i].split(" ")[1])));
        }

        repaint();// paintImmediately(0, 0, getWidth(), getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ((Graphics2D) g).addRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        // center map
        g.translate(getWidth() / 2 - mapWidth * size / 2, getHeight() / 2 - mapHeight * size / 2);

        // draw map
        for (int xm = 0; xm < mapWidth; xm++) {
            for (int ym = 0; ym < mapHeight; ym++) {

                if (map[xm][ym] <= 2 || map[xm][ym] == 9) {

                    switch (map[xm][ym]) {
                        case 0:
                            g.drawImage(images.get("Unbreakable.png"),
                                    xm * size, ym * size, null);
                            break;
                        case 1:
                            g.drawImage(images.get("Breakable.png"),
                                    xm * size, ym * size, null);
                            break;
                        case 2:
                            g.drawImage(images.get("Floor.png"),
                                    xm * size, ym * size, null);
                            break;
                        case 9:
                            g.drawImage(images.get("Lava.png"),
                                    xm * size, ym * size, null);
                            break;
                    }
                } else {
                    String pU = "BLEIGH!!!!";

                    g.drawImage(images.get("Floor.png"),
                            xm * size, ym * size, null);

                    switch (map[xm][ym]) {
                        case 3:
                            pU = "C";
                            break;
                        case 4:
                            pU = "P";
                            break;
                        case 5:
                            pU = "S";
                            break;
                        case 6:
                            pU = "K";
                            break;
                        case 7:
                            pU = "T";
                            break;
                    }
                    g.drawImage(images.get(pU + ".png"),
                            xm * size, ym * size, null);
                }
            }
        }

        // draw bombs
        for (Point bomb : bombs) {
            g.drawImage(images.get("Bomb.png"),
                    bomb.x, bomb.y, null);
        }

        // draw players
        for (int i = 0; i < players.size(); i++) {
            g.drawImage(images.get("Player" + i + ".png"),
                    players.get(i).x, players.get(i).y, null);
        }
    }
}
