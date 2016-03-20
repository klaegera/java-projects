package raytracer;

public class Ray {

    private final Vector s, d;

    public Ray(Vector start, Vector dir) {
        this.s = start;
        this.d = dir.normalize();
    }

    public Vector getPoint(double t) {
        return s.add(d.sMult(t));
    }

    public Vector getS() {
        return s;
    }

    public Vector getD() {
        return d;
    }
}
