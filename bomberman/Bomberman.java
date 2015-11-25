import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

public class Bomberman extends JFrame {

    int port = 58246;
    int sx = 0, sy = 0, space = 0;
    private Player[] players;
    private List<Bomb> bombs = new ArrayList<>();
    private int[][] map;
    private int mapWidth, mapHeight, size = 64;
    private Point[] spawnLocations;

    public static void main(String[] args) {
        System.out.print("\n Hostname (Client) or blank (Server): ");
        String host = new Scanner(System.in).nextLine();
        if (host.equals("")) {
            new Bomberman();
        } else {
            new Bomberman(host);
        }
    }

    Bomberman() {
        // server

        initMap();
        startAnimator();
        System.out.println("\n Server started, waiting for connections...");

        try (ServerSocket server = new ServerSocket(port);) {
            int joined = 0;
            while (joined < 4) {
                Socket client = server.accept();
                Player newPlayer = new Player(client, spawnLocations[joined].x, spawnLocations[joined].y, size, map, bombs);
                newPlayer.out.println(mapWidth + "," + mapHeight + "," + size);
                players[joined] = newPlayer;
                joined++;
                System.out.println(" Player " + joined + " joined!");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    Bomberman(String ip) {
        // client

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(1920, 1080);
        setVisible(true);

        addKeyListener(new KeyListener());

        boolean connected = false;
        while (!connected) {
            try (
                    Socket socket = new Socket(ip, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                connected = true;

                String[] mapSpecs = in.readLine().split(",");
                Panel panel = new Panel(Integer.parseInt(mapSpecs[0]), Integer.parseInt(mapSpecs[1]), Integer.parseInt(mapSpecs[2]));
                panel.setSize(getWidth(), getHeight());
                add(panel);

                String msg;
                while ((msg = in.readLine()) != null) {
                    panel.draw(msg);

                    out.println(sx + "," + sy + "," + space);
                }

            } catch (IOException ex) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                }
            }
        }
    }

    private void initMap() {

        /*
         format: width,height,breakableRatio,map,playerCount,spawnLocations
        
         0: wall (solid)
         1: wall (breakable)

         2: empty

         3: bomb+   C
         4: radius+ P
         5: speed+  S
         6: kick    K
         7: throw   T

         */
        String mapString = "17,17,0.7,"
                + "00000000000000000"
                + "02211111111111220"
                + "02010101010101020"
                + "01111111111111110"
                + "01010101010101010"
                + "01111111111111110"
                + "01010101010101010"
                + "01111111111111110"
                + "01010101010101010"
                + "01111111111111110"
                + "01010101010101010"
                + "01111111111111110"
                + "01010101010101010"
                + "01111111111111110"
                + "02010101010101020"
                + "02211111111111220"
                + "00000000000000000"
                + ",4,1 1,15 15,15 1,1 15";
        String[] mapInfo = mapString.split(",");

        mapWidth = Integer.parseInt(mapInfo[0]);
        mapHeight = Integer.parseInt(mapInfo[1]);
        float breakableRatio = Float.parseFloat(mapInfo[2]);

        players = new Player[Integer.parseInt(mapInfo[4])];
        spawnLocations = new Point[Integer.parseInt(mapInfo[4])];

        for (int i = 0; i < spawnLocations.length; i++) {
            spawnLocations[i] = new Point(
                    Integer.parseInt(mapInfo[i + 5].split(" ")[0]) * size,
                    Integer.parseInt(mapInfo[i + 5].split(" ")[1]) * size);
        }

        map = new int[mapWidth][mapHeight];

        for (int xm = 0; xm < mapWidth; xm++) {
            for (int ym = 0; ym < mapHeight; ym++) {
                int type = Character.getNumericValue(
                        mapInfo[3].charAt(mapHeight * xm + ym));
                switch (type) {
                    case 1:
                        if (Math.random() > breakableRatio) {
                            type = 2;
                        } else {
                            type = 1;
                        }
                        break;
                }
                map[xm][ym] = type;
            }
        }
    }

    private void startAnimator() {
        new Timer().scheduleAtFixedRate(new Animator(), 0, 16);
    }

    private class Animator extends TimerTask {

        @Override
        public void run() {

            String drawString = generateDrawString();

            // player tick
            for (int i = 0; i < players.length; i++) {
                if (players[i] != null) {
                    if (!players[i].tick(drawString, Bomberman.this)) {
                        players[i] = null;
                        System.out.println(" Player " + (i + 1) + " disconnected!");
                    }
                }
            }

            // bomb tick
            for (int i = 0; i < bombs.size(); i++) {
                bombs.get(i).tick();
                if (bombs.get(i).fuse < -50) {
                    bombs.remove(i);
                }
            }
        }
    }

    int playerCount() {
        int playerCount = 0;
        for (Player player : players) {
            if (player != null) {
                playerCount++;
            }
        }
        return playerCount;
    }

    String generateDrawString() {
        String drawString = "";

        // fires
        int[][] fireMap = new int[mapWidth][mapHeight];
        for (Bomb bomb : bombs) {
            for (Point fire : bomb.fires) {
                fireMap[fire.x][fire.y] = 1;
            }
        }

        // map
        for (int ym = 0; ym < mapHeight; ym++) {
            for (int xm = 0; xm < mapWidth; xm++) {
                drawString += fireMap[xm][ym] == 1 ? 9 : map[xm][ym];
            }
        }

        // playerCount
        drawString += "," + playerCount();

        // players
        for (Player player : players) {
            if (player != null) {
                if (player.invulnerable > 0 && System.currentTimeMillis() % 300 < 100) {
                    drawString += "," + (-size) + " " + (-size);
                } else {
                    drawString += "," + player.x + " " + player.y;
                }
            }
        }

        // bombCount
        drawString += "," + bombs.size();

        // bombs
        for (Bomb bomb : bombs) {
            Point pos = bomb.getPos();
            drawString += "," + pos.x + " " + pos.y;
        }

        return drawString;
    }

    private void breakWall(int x, int y) {

        int r = (int) (Math.random() * 120);

        if (r < 40) {
            map[x][y] = 2;
        } else if (r < 50) {
            map[x][y] = 3;
        } else if (r < 60) {
            map[x][y] = 4;
        } else if (r < 70) {
            map[x][y] = 5;
        } else if (r < 75) {
            map[x][y] = 6;
        } else if (r < 80) {
            map[x][y] = 7;
        } else {
            map[x][y] = 2;
        }
    }

    void placeBomb(Player player) {

        // place bomb if not present, else throw
        Bomb presentBomb = checkBombCollision((player.x + size / 2) / size, (player.y + size / 2) / size);
        if (presentBomb == null) {
            int bombsRemaining = player.bombCount;
            for (Bomb bomb : bombs) {
                if (bomb.player == player) {
                    bombsRemaining--;
                }
            }
            if (bombsRemaining > 0) {
                bombs.add(new Bomb(player, size));
            }
        } else if ((player.sx == 0 ^ player.sy == 0) && player.bombThrow) {
            presentBomb.throwBomb(player);
        }
    }

    Bomb checkBombCollision(int x, int y) {
        for (Bomb bomb : bombs) {
            if (x == bomb.xm && y == bomb.ym) {
                return bomb;
            }
        }
        return null;
    }

    private int mod(int a, int b) {
        int m = a % b;
        return (m < 0) ? m + b : m;
    }

    public class Bomb {

        Player player;
        int xm, ym;
        int size;
        int fuse = 200;
        boolean thrown;
        Point moving, direction;
        List<Point> fires = new ArrayList<>();

        Bomb(Player player, int size) {
            this.size = size;
            xm = (player.x + size / 2) / size;
            ym = (player.y + size / 2) / size;
            this.player = player;
        }

        Point getPos() {
            if (moving != null) {
                return moving;
            }
            return new Point(xm * size, ym * size);
        }

        void tick() {

            fuse--;

            // move bomb
            if (moving != null) {

                for (int i = 0; i < 8; i++) {
                    if (moving.x % size == 0 && moving.y % size == 0) {
                        if (thrown) {
                            if (map[moving.x / size][moving.y / size] >= 2
                                    && checkBombCollision(moving.x / size, moving.y / size) == null) {

                                // stop bomb
                                xm = moving.x / size;
                                ym = moving.y / size;
                                moving = direction = null;
                                break;
                            }
                        } else {
                            int nextXM = moving.x / size + direction.x;
                            int nextYM = moving.y / size + direction.y;

                            // check collision
                            if (map[nextXM][nextYM] < 2 || checkBombCollision(nextXM, nextYM) != null) {
                                // stop bomb
                                xm = moving.x / size;
                                ym = moving.y / size;
                                moving = direction = null;
                                break;
                            }
                        }
                    }

                    // move bomb
                    moving = new Point(mod(moving.x + direction.x, mapWidth * size),
                            mod(moving.y + direction.y, mapHeight * size));
                    fuse = Math.max(fuse, 1);
                }
            }

            if (fuse == 0) {

                fires.add(new Point(xm, ym));
                Point[] directions = {
                    new Point(0, 1),
                    new Point(0, -1),
                    new Point(1, 0),
                    new Point(-1, 0)};

                for (Point dir : directions) {
                    for (int i = 1; i < player.bombRadius; i++) {

                        int dirXM = xm + (i * dir.x);
                        int dirYM = ym + (i * dir.y);

                        if (map[dirXM][dirYM] == 0) {
                            break;
                        } else if (map[dirXM][dirYM] == 1) {
                            fires.add(new Point(dirXM, dirYM));
                            breakWall(dirXM, dirYM);
                            break;
                        } else {
                            fires.add(new Point(dirXM, dirYM));

                            if (checkBombCollision(dirXM, dirYM) != null) {
                                checkBombCollision(dirXM, dirYM).fuse = 1;
                            }

                        }
                    }
                }

                xm = ym = -1;
            }
        }

        boolean kick(int sx, int sy) {

            thrown = false;

            // check if kick possible
            if (map[xm + sx][ym + sy] >= 2 && checkBombCollision(xm + sx, ym + sy) == null) {

                // start kick
                moving = new Point(xm * size, ym * size);
                direction = new Point(sx, sy);
                xm = ym = -1;
                return true;
            } else {
                return false;
            }
        }

        void throwBomb(Player player) {

            thrown = true;

            // start throw
            moving = new Point(xm * size + player.sx, ym * size + player.sy);
            direction = new Point(player.sx, player.sy);
            xm = ym = -1;
        }
    }

    class KeyListener extends KeyAdapter {

        boolean r, l, u, d;

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    space = 1;
                    break;
                // move
                case KeyEvent.VK_A:
                    l = true;
                    break;
                case KeyEvent.VK_D:
                    r = true;
                    break;
                case KeyEvent.VK_W:
                    u = true;
                    break;
                case KeyEvent.VK_S:
                    d = true;
                    break;
            }

            setSign();
        }

        @Override
        public void keyReleased(KeyEvent e) {

            switch (e.getKeyCode()) {
                // place bomb
                case KeyEvent.VK_SPACE:
                    space = 0;
                    break;
                // move
                case KeyEvent.VK_A:
                    l = false;
                    break;
                case KeyEvent.VK_D:
                    r = false;
                    break;
                case KeyEvent.VK_W:
                    u = false;
                    break;
                case KeyEvent.VK_S:
                    d = false;
                    break;
            }

            setSign();
        }

        private void setSign() {
            sx = sy = 0;
//            int pcxm = (players[0].x + size / 2) / size;
//            int pcym = (players[0].y + size / 2) / size;

            if (l) {
                sx--;
            }
            if (r) {
                sx++;
            }
            if (u) {
                sy--;
            }
            if (d) {
                sy++;
            }
        }
    }
}
