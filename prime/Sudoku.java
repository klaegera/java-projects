public class Sudoku {

    int[] input = new int[81];
    int[] current = new int[81];
    int[] banned = new int[81];
    int[] perm = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    boolean progress;

    Sudoku(String input, boolean progress) {
        this.progress = progress;
        if (!input.matches("\\d{81}")) {
            if (input == "generate") {
                generate();
            } else {
                System.out.println(" Input error, 81 digits expected.");
            }
        } else {
            long t = System.nanoTime();
            for (int i = 0; i < 81; i++) {
                this.input[i] = current[i] = Character.getNumericValue(input.charAt(i));
            }
            if (solve(0)) {
                output();
            } else {
                System.out.println(" No solution found.");
            }
            System.out.println(" " + ((double) (System.nanoTime() - t) / 1000000) + " ms");
        }
    }

    boolean solve(int i) {
        int permc = -1;
        if (input[i] == 0) {
            while (permc < 8) {
                permc++;
                current[i] = perm[permc];
                if (progress) {
                    progress();
                }
                if (current[i] != banned[i] && valid(i)) {
                    if (i == 80) {
                        return true;
                    }
                    if (solve(i + 1)) {
                        return true;
                    }
                }
            }
            current[i] = 0;
            return false;
        }
        if (i == 80) {
            return true;
        }
        return solve(i + 1);
    }

    void generate() {
        for (int i = 0; i < 9; i++) {
            int j = (int) (Math.random() * (i + 1));
            int temp = perm[j];
            perm[j] = perm[i];
            perm[i] = temp;
        }
        solve(0);
        int[] solution = current;
        input = current;
    }

    void output() {
        String output = "\n ";
        for (int i = 0; i < 81; i++) {
            output += current[i];
            if (i % 27 == 26 && i != 80) {
                output += "\n";
            }
            if (i % 9 == 8) {
                output += "\n ";
            } else if (i % 3 == 2) {
                output += " ";
            }
        }
        System.out.printf((progress ? "\n" : "") + " " + output + "\n");
    }

    void progress() {
        String s = "        ";
        for (int i = 0; i < 81; i++) {
            s += current[i] == input[i] ? " " : current[i];
        }
        System.out.print(s + "\r");
        try {
            Thread.sleep(1);
        } catch (Exception e) {
        }
    }

    boolean valid(int i) {
        int x = i % 9;
        int y = i / 9;

        // row
        for (int c = 0; c < 9; c++) {
            if (c == x) {
                continue;
            }
            if (current[i + c - x] == current[i]) {
                return false;
            }
        }

        // column
        for (int r = 0; r < 9; r++) {
            if (r == y) {
                continue;
            }
            if (current[r * 9 + x] == current[i]) {
                return false;
            }
        }

        // box
        int box = (i / 27) * 27 + (x / 3) * 3;
        for (int dx = 0; dx < 3; dx++) {
            for (int dy = 0; dy < 3; dy++) {
                int di = box + dy * 9 + dx;
                if (di == i) {
                    continue;
                }
                if (current[di] == current[i]) {
                    return false;
                }
            }
        }

        return true;
    }
}
