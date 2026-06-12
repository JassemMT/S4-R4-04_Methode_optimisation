package sae.src;



public class ImageFilter {

    
    // -------------------------------------------------------------------------
    // Génération d'un filtre moyenne (NxN)
    // -------------------------------------------------------------------------

    /**
     * Crée un filtre "flou par moyenne" de taille size x size.
     * Tous les coefficients valent 1/(size*size).
     *
     * @param size  taille du filtre (doit être impair, ex: 3, 5, 7)
     * @return      matrice de coefficients normalisée
     */
    public static double[][] createFilter(int size) {
        double[][] filter = new double[size][size];
        double value = 1.0 / (size * size);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                filter[i][j] = value;
        return filter;
    }


}