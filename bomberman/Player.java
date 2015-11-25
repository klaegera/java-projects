import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

class Player {

    PrintWriter out;
    BufferedReader in;

    int[][] map;
    List<Bomberman.Bomb> bombs;

    int sx = 0, sy = 0, space = 0;

    int hp;
    int x, y, size;
    int speed, bombCount, bombRadius;
    boolean bombKick, bombThrow;
    int invulnerable = 0;
    boolean dead;

    Player(Socket socket, int x, int y, int size, int[][] map, List<Bomberman.Bomb> bombs) {

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.x = x;
        this.y = y;
        this.size = size;
        this.map = map;
        this.bombs = bombs;

        hp = 3;
        speed = 3;
        bombCount = 1;
        bombRadius = 2;
        bombKick = false;
        bombThrow = false;
    }

    boolean tick(String drawString, Bomberman bm) {
        try {
            out.println(drawString);

            String[] input = in.readLine().split(",");
            sx = Integer.parseInt(input[0]);
            sy = Integer.parseInt(input[1]);
            int nextSpace = Integer.parseInt(input[2]);
            if (space == 1 && nextSpace == 0 && !dead) {
                bm.placeBomb(this);
            }
            space = nextSpace;
        } catch (IOException ex) {
            return false;
        }

        if (!dead) {
            move();
        }
        if (invulnerable > 0) {
            invulnerable--;
        }
        return true;
    }

    void move() {

        if (sx != 0 || sy != 0) {

            // check and move one pixel at a time
            boolean moved = false;
            for (int i = 0; i < speed; i++) {
                if (sx != 0 && !checkCollision(sx, 0, bombs)) {
                    x += sx;
                    moved = true;
                }
                if (sy != 0 && !checkCollision(0, sy, bombs)) {
                    y += sy;
                    moved = true;
                }

                // slide
                int xm = x / size;
                int ym = y / size;
                if ((sx == 0 ^ sy == 0) && !moved && !(x % size == 0 && y % size == 0) && space == 0) {
                    if (map[xm + sx][ym + sy] >= 2) {
                        if (sx == 0) {
                            x--;
                        } else {
                            y--;
                        }
                    } else {
                        if (sx == 0) {
                            if (map[xm + 1][ym + sy] >= 2) {
                                x++;
                            }
                        } else {
                            if (map[xm + sx][ym + 1] >= 2) {
                                y++;
                            }
                        }
                    }
                }
            }

            // check and pick up powerups
            int cxm = (x + size / 2) / size;
            int cym = (y + size / 2) / size;
            if (map[cxm][cym] > 2) {
                switch (map[cxm][cym]) {
                    case 3:
                        bombCount++;
                        break;
                    case 4:
                        bombRadius++;
                        break;
                    case 5:
                        speed = Math.min(++speed, 8);
                        break;
                    case 6:
                        bombKick = true;
                        break;
                    case 7:
                        bombThrow = true;
                        break;
                }
                map[cxm][cym] = 2;
            }
        }

        // fire collision
        Rectangle currentPos = new Rectangle(
                x + (int) Math.ceil(size / 10.0), y + (int) Math.ceil(size / 10.0),
                size - (int) Math.ceil(size / 5.0), size - (int) Math.ceil(size / 5.0));
        for (Bomberman.Bomb bomb : bombs) {
            for (Point fire : bomb.fires) {
                if (currentPos.intersects(fire.x * size, fire.y * size, size, size)) {
                    burn();
                }
            }
        }
    }

    private boolean checkCollision(int sx, int sy, List<Bomberman.Bomb> bombs) {
        Rectangle currentPos = new Rectangle(x, y, size, size);
        Rectangle nextPos = new Rectangle(x + sx, y + sy, size, size);

        // bomb collision
        for (Bomberman.Bomb bomb : bombs) {
            if (nextPos.intersects(bomb.xm * size, bomb.ym * size, size, size)
                    && !currentPos.intersects(bomb.xm * size, bomb.ym * size, size, size)) {

                if ((sx == 0 ^ sy == 0) && bombKick) {
                    return !bomb.kick(sx, sy);
                } else {
                    return true;
                }
            }
        }

        // wall collision
        for (int xm = 0; xm < map.length; xm++) {
            for (int ym = 0; ym < map[0].length; ym++) {
                if (map[xm][ym] < 2 && nextPos.intersects(xm * size, ym * size, size, size)) {
                    return true;
                }

            }
        }

        // else
        return false;
    }

    private void burn() {
        if (invulnerable == 0) {
            invulnerable = 200;
            if (--hp <= 0) {
                dead = true;
                x = -size;
                y = -size;
            }
        }
    }
}
