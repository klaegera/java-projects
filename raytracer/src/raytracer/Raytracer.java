package raytracer;

import java.util.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class Raytracer {

    private final List<Camera> cams;
    private final Set<Shape> shapes;
    private Random rand;

    public Raytracer() {
        cams = new ArrayList<>();
        shapes = new HashSet<>();
    }

    public void addCam(Camera cam) {
        cams.add(cam);
    }

    public Camera getFirstCam() {
        return cams.get(0);
    }

    public int getCamCount() {
        return cams.size();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void animate(int frame) {
        for (Shape s : shapes) {
            if (s instanceof Animated) {
                ((Animated) s).animate(frame);
            }
        }
    }

    private Intersect intersectAll(Ray ray) {
        Intersect i = null;
        double t;
        for (Shape s : shapes) {
            t = s.intersect(ray);
            if (!Double.isNaN(t) && (i == null || i.getT() > t)) {
                i = new Intersect(s, t);
            }
        }
        return i;
    }

    private Vector getLight(Ray ray, int depth) {
        Intersect i = intersectAll(ray);
        if (i == null) {
            return Vector.ZERO;
        }

        Shape s = i.getShape();
        if (depth == 0) {
            return s.getMat().getEmission();
        }

        Vector intersectionPoint = ray.getPoint(i.getT());

        Vector n = s.normal(intersectionPoint);
        boolean inside = n.dot(ray.getD()) > 0;
        if (inside) {
            n = n.sMult(-1);
        }

        boolean reflected = false;
        Vector nextD = null;
        if (s.getMat().getRefIndex() > 0) {
            nextD = refract(s.getMat().getRefIndex(), inside, ray.getD(), n);
        }
        if (s.getMat().getRefIndex() == 0 || nextD == null) {
            if (s.getMat().getRefIndex() > 0) {
                nextD = reflect(ray.getD(), n);
                reflected = true;
            } else {
                double shiny = s.getMat().getShiny();
                if (shiny == 0) {
                    nextD = diffuse(n);
                } else {
                    if (new Random().nextDouble() < shiny) {
                        nextD = reflect(ray.getD(), n);
                        reflected = true;
                    } else {
                        nextD = diffuse(n);
                    }
                }
            }
        }

        Vector light = getLight(new Ray(intersectionPoint, nextD), depth - 1);
        if (!reflected) {
            light = light.colorMult(s.getMat().getColor());
        }

        return light.add(s.getMat().getEmission());
    }

    private Vector diffuse(Vector n) {
        double q = rand.nextDouble();
        Vector p = n.cross(new Vector(rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1)).normalize();
        return n.sMult(q).add(p.sMult(1 - q));
    }

    private Vector reflect(Vector v, Vector n) {
        return v.sub(n.sMult(v.dot(n) * 2));
    }

    private Vector refract(double n2, boolean inside, Vector v, Vector n) {
        double n1 = 1.0;
        double nQ = inside ? n2 / n1 : n1 / n2;
        double cosA = -n.dot(v);
        double cosB = Math.sqrt(1 - nQ * nQ * (1 - cosA * cosA));

        if (!Double.isNaN(cosB)) {
            double refOrth = (n1 * cosA - n2 * cosB) / (n1 * cosA + n2 * cosB);
            double refPara = (n2 * cosA - n1 * cosB) / (n2 * cosA + n1 * cosB);
            double reflectance = (refOrth * refOrth + refPara * refPara) / 2;

            if (reflectance < new Random().nextDouble()) {
                return v.sMult(nQ).add(n.sMult(nQ * cosA - cosB));
            }
        }
        return null;
    }

    public BufferedImage raytrace(int samples, int depth) {
        BufferedImage img = new BufferedImage(getFirstCam().getWidth() * cams.size(), getFirstCam().getHeight(), 5);

        for (int camNum = 0; camNum < cams.size(); camNum++) {
            Camera cam = cams.get(camNum);
            BufferedImage camImg = new BufferedImage(cam.getSSWidth(), cam.getSSHeight(), 5);
            Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
            for (int tNum = 0; tNum < threads.length; tNum++) {
                final int T_NUM = tNum;
                threads[tNum] = new Thread() {
                    @Override
                    public void run() {
                        rand = ThreadLocalRandom.current();
                        raytraceSection(camImg, cam, samples, depth, T_NUM, camImg.getHeight() / threads.length, (T_NUM == threads.length - 1 ? camImg.getHeight() % threads.length : 0));
                    }
                };
                threads[tNum].start();
            }
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                }
            }
            img.getGraphics().drawImage(cam.concentrate(camImg), getFirstCam().getWidth() * camNum, 0, null);
        }
        System.out.println("  Writing image...\n");
        return img;
    }

    private void raytraceSection(BufferedImage img, Camera cam, int samples, int depth, int num, int height, int extra) {
        long t0 = 0;
        for (int y = num * height; y < (num + 1) * height + extra; y++) {
            if (num == 0) {
                if (y == 5) {
                    t0 = System.currentTimeMillis();
                }
                String eta = "estimating";
                if (y > 5) {
                    int timePassed = (int) (System.currentTimeMillis() - t0);
                    int timePerLine = timePassed / (y - 5);
                    int secondsLeft = (timePerLine * (height - y)) / 1000;
                    eta = String.format("%d:%02d:%02d", secondsLeft / 3600, (secondsLeft % 3600) / 60, secondsLeft % 60);
                }
                System.out.printf("\r  [%-45s]  %d%%  (ETA: %s)      ",
                        new String(new char[(45 * y + 45) / height]).replace("\0", "="),
                        (100 * y + 100) / height,
                        eta);
            }
            for (int x = 0; x < cam.getSSWidth(); x++) {
                Vector average = Vector.ZERO;
                for (int i = samples; i > 0; i--) {
                    average = average.add(getLight(cam.getCamRay(x, y), depth));
                }
                average = average.sMult(1.0 / samples);
                img.setRGB(x, y, average.toRGB());
            }
        }
        if (num == 0) {
            System.out.print("\n  Waiting for threads to die...\n");
        }
    }
}
