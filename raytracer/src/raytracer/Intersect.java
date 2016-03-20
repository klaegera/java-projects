package raytracer;

public class Intersect {

    private final Shape shape;
    private final double t;

    public Intersect(Shape shape, double t) {
        this.shape = shape;
        this.t = t;
    }

    public Shape getShape() {
        return shape;
    }

    public double getT() {
        return t;
    }
}
