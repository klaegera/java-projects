package raytracer;

public class Cuboid extends Shape {

    private final Vector a, b, c, d, e, f, g, h, nx, ny, nz;

    private final double ESV = 1e-8;

    public Cuboid(Vector pos, Vector size, Vector rot, Material mat) {
        super(mat);

        double ar = Math.toRadians(rot.dot(Vector.UX));
        double br = Math.toRadians(rot.dot(Vector.UY));
        double gr = Math.toRadians(rot.dot(Vector.UZ));
        double sa = Math.sin(ar), ca = Math.cos(ar);
        double sb = Math.sin(br), cb = Math.cos(br);
        double sg = Math.sin(gr), cg = Math.cos(gr);

        nx = new Vector(cb * cg, cb * sg, -sb);
        ny = new Vector(sa * sb * cg - ca * sg, sa * sb * sg + ca * cg, sa * cb);
        nz = new Vector(ca * sb * cg + sa * sg, ca * sb * sg - sa * cg, ca * cb);
        Vector x = nx.sMult(size.dot(Vector.UX));
        Vector y = ny.sMult(size.dot(Vector.UY));
        Vector z = nz.sMult(size.dot(Vector.UZ));

        a = pos.sub(x.sMult(0.5)).sub(y.sMult(0.5)).sub(z.sMult(0.5));
        b = a.add(x);
        c = b.add(y);
        d = a.add(y);
        e = a.add(z);
        f = e.add(x);
        g = f.add(y);
        h = e.add(y);
    }

    @Override
    public Vector normal(Vector point) {
        Vector aP = point.sub(a);
        Vector gP = point.sub(g);
        if (aP.dot(nx) <= ESV && aP.dot(nx) > -ESV) {
            return nx.sMult(-1);
        }
        if (aP.dot(ny) <= ESV && aP.dot(ny) > -ESV) {
            return ny.sMult(-1);
        }
        if (aP.dot(nz) <= ESV && aP.dot(nz) > -ESV) {
            return nz.sMult(-1);
        }
        if (gP.dot(nx) <= ESV && gP.dot(nx) > -ESV) {
            return nx;
        }
        if (gP.dot(ny) <= ESV && gP.dot(ny) > -ESV) {
            return ny;
        }
        if (gP.dot(nz) <= ESV && gP.dot(nz) > -ESV) {
            return nz;
        }
        return Vector.ZERO;
    }

    @Override
    public double intersect(Ray ray) {
        double[] i = new double[6];
        i[0] = intersectPlane(ray, ny, a, b, e);
        i[1] = intersectPlane(ray, nz, a, d, b);
        i[2] = intersectPlane(ray, nx, a, d, e);
        i[3] = intersectPlane(ray, ny.sMult(-1), g, c, h);
        i[4] = intersectPlane(ray, nz.sMult(-1), g, h, f);
        i[5] = intersectPlane(ray, nx.sMult(-1), g, c, f);

        double tMin = Double.MAX_VALUE;
        for (double t : i) {
            if (!Double.isNaN(t) && t < tMin && t > ESV) {
                tMin = t;
            }
        }
        if (tMin == Double.MAX_VALUE) {
            return Double.NaN;
        }
        return tMin;
    }

    private double intersectPlane(Ray ray, Vector n, Vector p, Vector ca, Vector cb) {
        if (ray.getD().dot(n) > ESV || ray.getD().dot(n) < -ESV) {
            double t = (p.sub(ray.getS()).dot(n)
                    / ray.getD().dot(n));

            if (contained(p, ca, cb, ray.getPoint(t))) {
                return t;
            }
        }
        return Double.NaN;
    }

    private boolean contained(Vector p, Vector ca, Vector cb, Vector i) {
        return p.sub(ca).dot(i.sub(ca)) >= -ESV
                && p.sub(cb).dot(i.sub(cb)) >= -ESV
                && ca.sub(p).dot(i.sub(p)) >= -ESV
                && cb.sub(p).dot(i.sub(p)) >= -ESV;
    }
}
