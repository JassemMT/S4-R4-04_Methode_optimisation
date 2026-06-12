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
        
        // On parcourt chaque pixel de l'image (sauf l'extrême bord pour pas déborder , car par exemple
        // le pixel en haut a gauche n'a pas 9 voisins si taille = 3 donc impossible
        // On refuse ainsi de se positionner sur un pixel qui n'a pas tous ses voisins
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
                    Math.min(255, Math.max(0, (int) sommeRouge)), // Sécurité pour pas dépasser 255 (au minimum 255)
                    Math.min(255, Math.max(0, (int) sommeVert)),
                    Math.min(255, Math.max(0, (int) sommeBleu))
                );
                
                nouvelleImage.setRGB(x, y, nouvelleCouleur.getRGB());
            }
        }
        
        return nouvelleImage;
    }

}
