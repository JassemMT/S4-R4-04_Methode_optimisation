package src.distance;

public interface IDistance {
    /**
     * Calcule la distance (la ressemblance) entre deux points ou deux couleurs.
     * @param a Premier point/couleur (ex: [R, G, B])
     * @param b Deuxième point/couleur (ex: [R, G, B])
     * @return Plus le résultat est proche de 0, plus a et b se ressemblent.
     */
    double calculerDistance(double[] a, double[] b);
}
