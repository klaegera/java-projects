package raytracer;

public abstract class Shape {

    private final Material mat;

    Shape(Material mat) {
        this.mat = mat;
    }

    public abstract Vector normal(Vector point);

    public abstract double intersect(Ray ray);

    public Material getMat() {
        return mat;
    }
}
