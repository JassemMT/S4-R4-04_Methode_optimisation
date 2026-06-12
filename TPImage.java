import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TPImage {

    public static void main(String[] args) {
        
        File fichierEntree = new File("image_test.jpg");
        File fichierSortie = new File("copie_image.png"); 

        try {
            System.out.println("Chargement de l'image...");
            BufferedImage image = ImageIO.read(fichierEntree);

            if (image == null) {
                System.out.println("Erreur : l'image n'a pas été trouvée. Vérifie le nom et l'emplacement du fichier.");
                return;
            }

            System.out.println("Sauvegarde de l'image...");
            ImageIO.write(image, "PNG", fichierSortie);
            
            System.out.println("Succès ! La copie a été créée.");

        } catch (IOException e) {
            System.out.println("Une erreur s'est produite lors de la lecture ou de l'écriture du fichier.");
            e.printStackTrace();
        }
    }
}
