import java.util.ArrayList;
import java.util.HashSet;

public class Tetromino {

    char[][] map;
    ArrayList<Tetro> ts;

    Tetromino(int rows, int cols, String ts) {
        map = new char[rows][cols];
        this.ts = new ArrayList<>();
        for (String s : ts.split("")) {
            this.ts.add(Tetro.valueOf(s));
        }
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            new Tetromino(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]).start();
        } else {
            System.out.println("\n Usage: [rows] [cols] [tetrominoes]\n Tetrominoes: IOTJLSZ");
        }
        // 5, 8, "LJSZTTIIOO"
    }

    void start() {
        long t = System.nanoTime();
        boolean result = fit(0, 0, ts);
        t = System.nanoTime() - t;
        if (result) {
            output();
        } else {
            System.out.println("\n No solution found.");
        }
        System.out.printf("\n %.3f ms\n", t / 1e6);
    }

    boolean fit(int row, int col, ArrayList<Tetro> ts) {
        for (Tetro t : new HashSet<>(ts)) {
            ArrayList<Tetro> tsCopy = new ArrayList<>(ts);
            tsCopy.remove(t);
            for (TetroRot tr : t.rotations) {
                if (check(row, col, tr)) {
                    place(row, col, tr, true);
                    if (tsCopy.isEmpty()) {
                        return true;
                    }

                    findFree:
                    for (int mRow = row; mRow < map.length; ++mRow) {
                        for (int mCol = 0; mCol < map[0].length; ++mCol) {
                            if (map[mRow][mCol] == 0) {
                                if (fit(mRow, mCol, tsCopy)) {
                                    return true;
                                }
                                break findFree;
                            }
                        }
                    }
                    place(row, col, tr, false);
                }
            }
        }
        return false;
    }

    boolean check(int row, int col, TetroRot tr) {
        for (int tRow = 0; tRow < 4; ++tRow) {
            for (int tCol = 0; tCol < 6; ++tCol) {
                if (tr.cell[tRow][tCol] != 0) {
                    int mRow = row + tRow;
                    int mCol = col + tCol - 2;
                    if (mCol < 0
                            || mCol >= map[0].length
                            || mRow >= map.length
                            || map[mRow][mCol] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    void place(int row, int col, TetroRot tr, boolean set) {
        for (int tRow = 0; tRow < 4; ++tRow) {
            for (int tCol = 0; tCol < 6; ++tCol) {
                if (tr.cell[tRow][tCol] != 0) {
                    map[row + tRow][col + tCol - 2] = set ? tr.cell[tRow][tCol] : (char) 0;
                }
            }
        }
    }

    void output() {

        final char V = '|', H = '-', E = ' ', S = '+';

        char[][] printMap = new char[map.length * 2 + 1][map[0].length * 2 + 1];
        for (int row = 0; row < map.length; ++row) {
            for (int col = 0; col < map[0].length; ++col) {
                int pR = row * 2 + 1;
                int pC = col * 2 + 1;

                printMap[pR - 1][pC - 1] = S;
                printMap[pR - 1][pC + 1] = S;
                printMap[pR + 1][pC - 1] = S;
                printMap[pR + 1][pC + 1] = S;
                switch (map[row][col]) {
                    case '║':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '═':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╔':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '╗':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╝':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╚':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '╥':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '╡':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╨':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '╞':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = V;
                        break;
                    case '╦':
                        printMap[pR - 1][pC] = H;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╣':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = V;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╩':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = H;
                        printMap[pR][pC - 1] = E;
                        break;
                    case '╠':
                        printMap[pR - 1][pC] = E;
                        printMap[pR][pC + 1] = E;
                        printMap[pR + 1][pC] = E;
                        printMap[pR][pC - 1] = V;
                        break;
                }
            }
        }

        System.out.println();
        for (int row = 0; row < printMap.length; ++row) {
            System.out.print(" ");
            for (int col = 0; col < printMap[0].length; ++col) {
                System.out.print(printMap[row][col] != 0 ? printMap[row][col] : ' ');
            }
            System.out.println();
        }
    }

    enum Tetro {

        I(new TetroRot[]{TetroRot.Ia, TetroRot.Ib}),
        O(new TetroRot[]{TetroRot.Oa}),
        T(new TetroRot[]{TetroRot.Ta, TetroRot.Tb, TetroRot.Tc, TetroRot.Td}),
        J(new TetroRot[]{TetroRot.Ja, TetroRot.Jb, TetroRot.Jc, TetroRot.Jd}),
        L(new TetroRot[]{TetroRot.La, TetroRot.Lb, TetroRot.Lc, TetroRot.Ld}),
        S(new TetroRot[]{TetroRot.Sa, TetroRot.Sb}),
        Z(new TetroRot[]{TetroRot.Za, TetroRot.Zb});

        TetroRot[] rotations;

        Tetro(TetroRot[] rotations) {
            this.rotations = rotations;
        }
    }

    enum TetroRot {

        Ia("..╥...:..║...:..║...:..╨..."),
        Ib("..╞══╡:......:......:......"),
        Oa("..╔╗..:..╚╝..:......:......"),
        Ta("..╞╦╡.:...╨..:......:......"),
        Tb("..╥...:.╞╣...:..╨...:......"),
        Tc("..╥...:.╞╩╡..:......:......"),
        Td("..╥...:..╠╡..:..╨...:......"),
        Ja("..╥...:..║...:.╞╝...:......"),
        Jb("..╥...:..╚═╡.:......:......"),
        Jc("..╔╡..:..║...:..╨...:......"),
        Jd("..╞═╗.:....╨.:......:......"),
        La("..╥...:..║...:..╚╡..:......"),
        Lb("..╔═╡.:..╨...:......:......"),
        Lc("..╞╗..:...║..:...╨..:......"),
        Ld("..╥...:╞═╝...:......:......"),
        Sa("..╔╡..:.╞╝...:......:......"),
        Sb("..╥...:..╚╗..:...╨..:......"),
        Za("..╞╗..:...╚╡.:......:......"),
        Zb("..╥...:.╔╝...:.╨....:......");

        char[][] cell;

        TetroRot(String s) {
            cell = new char[4][6];

            String[] rows = s.split(":");
            for (int row = 0; row < 4; ++row) {
                for (int col = 0; col < 6; ++col) {
                    cell[row][col] = rows[row].charAt(col) == '.' ? (char) 0 : rows[row].charAt(col);
                }
            }
        }
    }
}
