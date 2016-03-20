package raytracer;

public class Animated extends Shape {

    private final String type;
    private final Object[] shapeParams;
    private final Object[] animParams;

    private Shape shape;

    public Animated(String type, Object[] shapeParams, Object[] animParams) {
        super(null);

        this.type = type;
        this.shapeParams = shapeParams;
        this.animParams = animParams;
    }

    public void animate(int frame) {
        Object[] cP = new Object[shapeParams.length];
        for (int i = 0; i < shapeParams.length; i++) {
            Object sP = shapeParams[i];
            Object aP = animParams[i];
            if (sP instanceof Vector) {
                cP[i] = ((Vector) sP).add(((Vector) aP).sMult(frame));
            } else {
                cP[i] = ((double) sP) + ((double) aP) * frame;
            }
        }

        switch (type) {
            case "Sphere":
                shape = new Sphere((Vector) cP[0], (double) cP[1], new Material((Vector) cP[2], (Vector) cP[3], (double) cP[4], (double) cP[5]));
                break;
            case "Cuboid2":
                shape = new Cuboid((Vector) cP[0], (Vector) cP[1], (Vector) cP[2], new Material((Vector) cP[3], (Vector) cP[4], (double) cP[5], (double) cP[6]));
                break;
        }
    }

    @Override
    public Vector normal(Vector point) {
        return shape.normal(point);
    }

    @Override
    public double intersect(Ray ray) {
        return shape.intersect(ray);
    }

    @Override
    public Material getMat() {
        return shape.getMat();
    }

}
