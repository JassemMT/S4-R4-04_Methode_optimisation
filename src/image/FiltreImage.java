package src.image;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class FiltreImage {

    // -------------------------------------------------------------------------
    // Génération d'un filtre moyenne (NxN)
    // -------------------------------------------------------------------------

    /**
     * Crée un filtre "flou par moyenne" de taille taille x taille.
     * Tous les coefficients valent 1/(taille*taille).
     * Sert à préparer le filtre pour un tableau de taille 15 par exemple
     * On prépare la moyenne avec un tableau de correspondance pour que sa soit plus optimisé
     * Evite de diviser lorsqu'on fait la moyenne
     *
     * @param taille  taille du filtre (doit être impair, ex: 3, 5, 7)
     * @return        matrice de coefficients normalisée
     */
    public static double[][] creerFiltreMoyenne(int taille) {
        // Définit une fenetre de 9 pixels .
        // Pour chaque pixel , on utilisera cette fonction ( par exemple si on met 3 en taille)
        // Pour plus de précision il vaut mieux prendre une petite taille ( car on regarde moins loin)
        // Alors on va calculer pour chaque pixel ses 9 voisins et utiliser la moyenne pour déterminer une couleur
        double[][] filtre = new double[taille][taille];
        double valeur = 1.0 / (taille * taille);
        
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                filtre[i][j] = valeur;
            }
        }
        return filtre;
    }

    // -------------------------------------------------------------------------
    // Génération d'un filtre Gaussien (NxN)
    // -------------------------------------------------------------------------

    /**
     * Crée un filtre de flou Gaussien.
     * Donne plus de poids au centre et moins sur les bords.
     *
     * @param taille  taille du filtre (doit être impair)
     * @param sigma   étalement de la courbe de Gauss (ex: 1.0 ou 2.0)
     * @return        matrice de coefficients normalisée
     */
    public static double[][] creerFiltreGaussien(int taille, double sigma) {
        double[][] filtre = new double[taille][taille];
        int rayon = taille / 2;
        double sommeTotale = 0; // Pour la normalisation
        
        // 1. Calcul des valeurs de Gauss pour chaque case
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                // Distance par rapport au pixel central de la petite matrice
                int x = i - rayon;
                int y = j - rayon;
                
                // Formule mathématique de Gauss
                double valeur = (1.0 / (2.0 * Math.PI * sigma * sigma)) * Math.exp(-(x * x + y * y) / (2.0 * sigma * sigma));
                filtre[i][j] = valeur;
                sommeTotale += valeur; // On garde le total pour la suite
            }
        }
        
        // 2. Normalisation ! 
        // Il faut que la somme totale du tableau fasse EXACTEMENT 1. 
        // Sinon, l'image finale sera plus sombre ou plus claire que l'originale.
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                filtre[i][j] = filtre[i][j] / sommeTotale;
            }
        }
        
        return filtre;
    }

    // -------------------------------------------------------------------------
    // Application du filtre sur l'image
    // -------------------------------------------------------------------------

    /**
     * Parcourt tous les pixels de l'image et applique le filtre (la matrice).
     * Permet a un pixel de se mélanger avec x voisins ( taille du filtre )
     */
    public static BufferedImage appliquerFiltre(BufferedImage image, double[][] filtre) {
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        // On prépare une nouvelle image vide pour stocker le résultat
        BufferedImage nouvelleImage = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_3BYTE_BGR);
        
        int tailleFiltre = filtre.length;
        // Sert a récupérer la distance entre le centre & le bord
        int rayon = tailleFiltre / 2; // Si taille=3, rayon=1. On ignore les bords de 1 pixel.
        
        // Copie les pixels de bordure depuis l'image originale (ils ne peuvent pas être filtrés
        // car ils n'ont pas assez de voisins pour remplir la fenêtre du filtre)
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < rayon; y++)            nouvelleImage.setRGB(x, y, image.getRGB(x, y));
            for (int y = hauteur - rayon; y < hauteur; y++) nouvelleImage.setRGB(x, y, image.getRGB(x, y));
        }
        for (int y = rayon; y < hauteur - rayon; y++) {
            for (int x = 0; x < rayon; x++)            nouvelleImage.setRGB(x, y, image.getRGB(x, y));
            for (int x = largeur - rayon; x < largeur; x++) nouvelleImage.setRGB(x, y, image.getRGB(x, y));
        }

        // Applique le filtre sur les pixels intérieurs (ceux qui ont tous leurs voisins)
        for (int x = rayon; x < largeur - rayon; x++) {
            for (int y = rayon; y < hauteur - rayon; y++) {
                
                double sommeRouge = 0;
                double sommeVert = 0;
                double sommeBleu = 0;
                
                // On pose la petite grille du filtre sur les voisins au pixel (x,y)
                for (int i = 0; i < tailleFiltre; i++) {
                    for (int j = 0; j < tailleFiltre; j++) {
                        
                        // Calcul des coordonnées du voisin
                        // Méthode optimisée : evite de refaire les calculs a chaque fois
                        int voisinX = x + i - rayon;
                        int voisinY = y + j - rayon;
                        
                        // On récupère sa couleur et on la découpe
                        int pixelVoisin = image.getRGB(voisinX, voisinY);
                        int[] rgb = OutilCouleur.getTabColor(pixelVoisin);
                        
                        double poids = filtre[i][j]; 
                        
                        // On multiplie et on ajoute au total (notre "fausse" moyenne)
                        sommeRouge += rgb[0] * poids;
                        sommeVert  += rgb[1] * poids;
                        sommeBleu  += rgb[2] * poids;
                    }
                }
                
                // On re-fusionne les 3 couleurs et on met le pixel sur la nouvelle image
                Color nouvelleCouleur = new Color(
                    Math.min(255, Math.max(0, (int) sommeRouge)), // Clamp entre 0 et 255
                    Math.min(255, Math.max(0, (int) sommeVert)),
                    Math.min(255, Math.max(0, (int) sommeBleu))
                );
                
                nouvelleImage.setRGB(x, y, nouvelleCouleur.getRGB());
            }
        }
        
        return nouvelleImage;
    }

}
