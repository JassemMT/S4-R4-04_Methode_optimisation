package src.image;

public class FiltreImage {

    // -------------------------------------------------------------------------
    // Génération d'un filtre moyenne (NxN)
    // -------------------------------------------------------------------------

    /**
     * Crée un filtre "flou par moyenne" de taille taille x taille.
     * Tous les coefficients valent 1/(taille*taille).
     * 
     *
     * @param taille  taille du filtre (doit être impair, ex: 3, 5, 7)
     * @return        matrice de coefficients normalisée
     */
    public static double[][] creerFiltreMoyenne(int taille) {
        double[][] filtre = new double[taille][taille];
        double valeur = 1.0 / (taille * taille);
        
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                filtre[i][j] = valeur;
            }
        }
        return filtre;
    }

}
