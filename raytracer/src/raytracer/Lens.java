package raytracer;

public class Lens extends Shape {

    private final Sphere s1, s2;
    private final Vector c, dir;
    private final double r;

    private final double ESV = 1e-8;

    public Lens(Vector pos, Vector rad, Vector dirU, double d, Material mat) {
        super(mat);

        c = pos;
        dir = dirU.normalize();
        double r1 = rad.dot(Vector.UX);
        double r2 = rad.dot(Vector.UY);
        double t = r1 + r2 + d;
        if (d > 0) {
            r = Math.min(Math.min(rad.dot(Vector.UZ), r1), r2);
        } else {
            r = Math.sqrt(r1 * r1 - Math.pow((r1 * r1 - r2 * r2 + t * t) / (2 * t), 2));
        }
        double x = (t * t - r2 * r2 + r1 * r1) / (2 * t);
        s1 = new Sphere(c.add(dir.sMult(x)), r1, null);
        s2 = new Sphere(c.sub(dir.sMult(t - x)), r2, null);
    }

    @Override
    public Vector normal(Vector point) {
        if (Math.abs(point.sub(s1.getC()).mag() - s1.getR()) < ESV) {
            return s1.normal(point);
        } else {
            return s2.normal(point);
        }
    }

    @Override
    public double intersect(Ray ray) {
        double tMin = Double.MAX_VALUE;
        double t1 = s1.intersect(ray);
        double t2 = s2.intersect(ray);
        if (t1 < tMin) {
            tMin = t1;
        }
        if (t2 < tMin) {
            tMin = t2;
        }
        if (tMin == Double.MAX_VALUE) {
            return Double.NaN;
        }
        Vector p = ray.getPoint(tMin + ESV);
        if (p.sub(c).mag() <= r) {
            return tMin;
        }
        ray = new Ray(p, ray.getD());
        double tMin2 = Double.MAX_VALUE;
        t1 = s1.intersect(ray);
        t2 = s2.intersect(ray);
        if (t1 < tMin2) {
            tMin2 = t1;
        }
        if (t2 < tMin2) {
            tMin2 = t2;
        }
        if (tMin2 == Double.MAX_VALUE) {
            return Double.NaN;
        }
        if (ray.getPoint(tMin2).sub(c).mag() <= r) {
            return tMin + tMin2 + ESV;
        }
        return Double.NaN;
    }
}
