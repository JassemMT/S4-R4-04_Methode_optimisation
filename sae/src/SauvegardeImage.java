package sae.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SauvegardeImage {

    public static BufferedImage chargerImage(String chemin) {
        try {
            return ImageIO.read(new File(chemin));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sauvegarderImage(BufferedImage image, String format, String chemin) {
        try {
            ImageIO.write(image, format, new File(chemin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
