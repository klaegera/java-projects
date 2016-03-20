package raytracer;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) throws IOException {

        long tStart = System.currentTimeMillis();

        String scene = args.length > 0 ? args[0] : "scene.txt";

        BufferedReader br = new BufferedReader(new FileReader(new File(scene)));

        String name = read(br);
        int frames = readI(br);
        int[] res = readIP(br);
        int[] snd = readIP(br);
        int ss = readI(br);

        Raytracer rt = new Raytracer();

        for (String type; (type = read(br)) != null;) {
            switch (type) {
                case "Camera":
                    rt.addCam(new Camera(new Ray(readV(br), readV(br)), readV(br), res[0], res[1], readD(br), readD(br), ss));
                    break;
                case "Sphere":
                    rt.addShape(new Sphere(readV(br), readD(br), readM(br)));
                    break;
                case "Cuboid":
                    rt.addShape(new Cuboid(readV(br), readV(br), readV(br), readM(br)));
                    break;
                case "Lens":
                    rt.addShape(new Lens(readV(br), readV(br), readV(br), readD(br), readM(br)));
                    break;
                case "Animated":
                    rt.addShape(readA(br));
                    break;
            }
        }

        // fancy design
        System.out.printf(
                new String(new char[100]).replace("\0", "\n")
                + "\n   _____             _                               "
                + "\n  |  __ \\           | |                             "
                + "\n  | |__) |__ _ _   _| |_ _ __ __ _  ___ ___ _ __     "
                + "\n  |  _  // _` | | | | __| '__/ _` |/ __/ _ \\ '__|   "
                + "\n  | | \\ \\ (_| | |_| | |_| | | (_| | (_|  __/ |     "
                + "\n  |_|  \\_\\__,_|\\__, |\\__|_|  \\__,_|\\___\\___|_|"
                + "\n                __/ |                                "
                + "\n  Lau & Adrian |___/                                 "
                + "\n                                                     "
                + "\n     _________________________________________       "
                + "\n   //                                         \\\\   "
                + "\n  ||   Input:       %-26s ||"
                + "\n  ||   Output:      %-26s ||"
                + "\n  ||   Frames:      %-26d ||"
                + "\n  ||   Resolution:  %-26s ||"
                + "\n  ||   Samples:     %-26d ||"
                + "\n  ||   Depth:       %-26d ||"
                + "\n  ||   Supersample: %-26s ||"
                + "\n   \\\\_________________________________________//   "
                + "\n\n\n",
                scene, name, frames, res[0] + " x " + res[1], snd[0], snd[1], ss * ss + "x"
        );

        new File(name).mkdir();
        for (int frame = 0; frame < frames; frame++) {
            System.out.printf("  Frame: %d/%d%n", frame + 1, frames);
            rt.animate(frame);
            ImageIO.write(rt.raytrace(snd[0], snd[1]), "PNG", new File(name + "/" + frame + "_" + name));
        }

        int seconds = (int) ((System.currentTimeMillis() - tStart) / 1000);
        System.out.printf("  Done! Duration: %d:%02d:%02d%n", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        Desktop.getDesktop().open(new File(name + "/0_" + name));
    }

    private static String read(BufferedReader br) {
        try {
            String line = br.readLine();
            if (line != null) {
                line = line.split(" ")[0].split("\t")[0];
                if (!line.isEmpty()) {
                    return line;
                }
                return read(br);
            }
        } catch (IOException ex) {
            System.out.println("Read failed: " + ex.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static int readI(BufferedReader br) {
        return Integer.parseInt(read(br));
    }

    private static int[] readIP(BufferedReader br) {
        String tmp = read(br);
        return new int[]{Integer.parseInt(tmp.split(",")[0]), Integer.parseInt(tmp.split(",")[1])};
    }

    private static Double readD(BufferedReader br) {
        return Double.parseDouble(read(br));
    }

    private static Vector readV(BufferedReader br) {
        return new Vector(read(br));
    }

    private static Material readM(BufferedReader br) {
        return new Material(readV(br), readV(br), readD(br), readD(br));
    }

    private static Animated readA(BufferedReader br) {
        String type = read(br);
        String animBlock = "";
        for (String line; !(line = read(br)).equals(">>>");) {
            animBlock += line + ";;";
        }

        String[] shapeParamsString = animBlock.split(";;---;;")[0].split(";;");
        String[] animParamsString = animBlock.split(";;---;;")[1].split(";;");
        Object[] shapeParams = new Object[shapeParamsString.length];
        Object[] animParams = new Object[shapeParamsString.length];

        for (int i = 0; i < shapeParams.length; i++) {
            String param = shapeParamsString[i];
            if (param.contains(",")) {
                shapeParams[i] = new Vector(param);
            } else {
                shapeParams[i] = Double.parseDouble(param);
            }

            param = animParamsString[i];
            if (param.contains(",")) {
                animParams[i] = new Vector(param);
            } else {
                animParams[i] = Double.parseDouble(param);
            }
        }
        return new Animated(type, shapeParams, animParams);
    }
}
