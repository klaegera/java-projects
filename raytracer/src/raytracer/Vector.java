package raytracer;

public class Vector {

    private final double x, y, z;
    public static final Vector ZERO = new Vector(0.0, 0.0, 0.0);
    public static final Vector UX = new Vector(1.0, 0.0, 0.0);
    public static final Vector UY = new Vector(0.0, 1.0, 0.0);
    public static final Vector UZ = new Vector(0.0, 0.0, 1.0);

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(String s) {
        String[] arr = s.split(",");
        x = Double.parseDouble(arr[0]);
        y = Double.parseDouble(arr[1]);
        z = Double.parseDouble(arr[2]);
    }

    public Vector add(Vector b) {
        return new Vector(
                x + b.x,
                y + b.y,
                z + b.z);
    }

    public Vector sub(Vector b) {
        return new Vector(
                x - b.x,
                y - b.y,
                z - b.z);
    }

    public Vector sMult(double a) {
        return new Vector(
                x * a,
                y * a,
                z * a);
    }

    public double dot(Vector b) {
        return x * b.x + y * b.y + z * b.z;
    }

    public Vector cross(Vector b) {
        return new Vector(
                y * b.z - z * b.y,
                z * b.x - x * b.z,
                x * b.y - y * b.x);
    }

    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector normalize() {
        return sMult(1.0 / mag());
    }

    public Vector colorMult(Vector b) {
        return new Vector(x * b.x, y * b.y, this.z * b.z);
    }

    public int toRGB() {
        return ((int) (clamp(x) * 255) << 16) + ((int) (clamp(y) * 255) << 8) + (int) (clamp(z) * 255);
    }

    private double clamp(double d) {
        if (d >= 1) {
            return 1.0;
        } else if (d <= 0) {
            return 0.0;
        } else {
            return d;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
