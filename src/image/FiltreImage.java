package src.image;

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
