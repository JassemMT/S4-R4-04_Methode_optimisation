package src.clustering;

public interface IClustering {
    /**
     * Algorithme qui regroupe les objets similaires.
     * 
     * @param donnees Tableau en 2D où chaque ligne est un objet (ex: un pixel), et chaque colonne une caractéristique (ex: R, G, B)
     * @param k Le nombre de groupes (clusters) à trouver
     * @return Un tableau contenant le numéro du groupe pour chaque objet
     */
    int[] clusteriser(double[][] donnees, int k);
}
