package raytracer;

import java.awt.image.BufferedImage;

public class Camera {

    private final Ray cam;
    private final Vector ver, hor, pos;
    private final int width, height, ss;

    public Camera(Ray cam, Vector up, int width, int height, double distance, double pixelSize, int ss) {
        this.cam = cam;
        this.width = width * ss;
        this.height = height * ss;
        this.ss = ss;

        hor = cam.getD().cross(up).normalize().sMult(pixelSize / ss);
        ver = cam.getD().cross(hor).normalize().sMult(pixelSize / ss);
        pos = cam.getS()
                .add(cam.getD().sMult(distance))
                .sub(ver.sMult(this.height / 2))
                .sub(hor.sMult(this.width / 2));
    }

    public Ray getCamRay(int pX, int pY) {
        Vector pixel = pos.add(hor.sMult(pX)).add(ver.sMult(pY));
        return new Ray(pixel, pixel.sub(cam.getS()));
    }

    public BufferedImage concentrate(BufferedImage ssImg) {
        BufferedImage result = new BufferedImage(getWidth(), getHeight(), 5);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {

                int avgR = 0, avgG = 0, avgB = 0;
                for (int dy = 0; dy < ss; dy++) {
                    for (int dx = 0; dx < ss; dx++) {
                        int color = ssImg.getRGB(x * ss + dx, y * ss + dy);
                        avgR += (color >> 16) & 0xFF;
                        avgG += (color >> 8) & 0xFF;
                        avgB += color & 0xFF;
                    }
                }
                avgR /= ss * ss;
                avgG /= ss * ss;
                avgB /= ss * ss;
                result.setRGB(x, y, (avgR << 16) + (avgG << 8) + avgB);
            }
        }
        return result;
    }

    public int getWidth() {
        return width / ss;
    }

    public int getHeight() {
        return height / ss;
    }

    public int getSSWidth() {
        return width;
    }

    public int getSSHeight() {
        return height;
    }
}
