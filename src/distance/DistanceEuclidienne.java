package src.distance;

public class DistanceEuclidienne implements IDistance {

    @Override
    public double calculerDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Les deux tableaux doivent avoir la même taille.");
        }
        
        double somme = 0;
        for (int i = 0; i < a.length; i++) {
            double difference = a[i] - b[i];
            somme += difference * difference;
        }
        return Math.sqrt(somme);
    }
}
