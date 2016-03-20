package raytracer;

public class Sphere extends Shape {

    private final Vector c;
    private final double r;

    private final double ESV = 1e-8;

    public Sphere(Vector c, double r, Material mat) {
        super(mat);

        this.c = c;
        this.r = r;
    }

    @Override
    public double intersect(Ray ray) {
        Vector v = ray.getS().sub(c);

        double binB = ray.getD().dot(v);
        double binC = v.dot(v) - r * r;

        double disc = binB * binB - binC;

        if (disc >= 0) {
            double root = Math.sqrt(disc);

            double t = (-binB - root);
            if (t >= ESV) {
                return t;
            }

            t = t + 2 * root;
            if (t >= ESV) {
                return t;
            }
        }
        return Double.NaN;
    }

    @Override
    public Vector normal(Vector point) {
        return point.sub(c).sMult(1 / r);
    }

    public Vector getC() {
        return c;
    }

    public double getR() {
        return r;
    }
}
