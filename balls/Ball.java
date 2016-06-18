public class Ball {

    private double x, y;
    private double vx, vy;
    private int charge;
    private int diam;

    Ball(int x, int y, int diam, int charge) {
        this.x = x;
        this.y = y;
        this.diam = diam;
        this.charge = charge;
    }

    double dist(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    void add(Ball ball) {

    }

    void add(double x, double y) {
        vx += x;
        vy += y;
    }

    void apply() {
        x += vx;
        y += vy;
        vx = 0;
        vy = 0;
    }

    int isPush(Ball ball) {
        if (ball.getCharge() == 0) {
            return 1;
        }
        return (int) Math.signum(charge * ball.getCharge()) * 3;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    int getCharge() {
        return charge;
    }

    void neutralize() {
        charge = 0;
    }

    int getDiam() {
        return diam;
    }

}
