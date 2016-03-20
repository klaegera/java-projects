package raytracer;

public class Material {

    private Vector color;
    private Vector emission;
    private double shiny, refIndex;

    public Material(Vector color, Vector emission, double shiny, double refIndex) {
        this.color = color;
        this.emission = emission;
        this.shiny = shiny;
        this.refIndex = refIndex;
    }

    public Vector getColor() {
        return color;
    }

    public Vector getEmission() {
        return emission;
    }

    public double getShiny() {
        return shiny;
    }

    public double getRefIndex() {
        return refIndex;
    }
}
