package src.clustering;

import src.distance.IDistance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMeans implements IClustering {
    
    private IDistance distanceCalculator;
    private int maxIterations = 100;
    
    // Le tableau des centres (les "moyennes" de chaque groupe)
    private double[][] centroids;
    
    public KMeans(IDistance distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public int[] clusteriser(double[][] donnees, int k) {
        int nombreDonnees = donnees.length;
        int dimensions = donnees[0].length;
        int[] labels = new int[nombreDonnees];
        centroids = new double[k][dimensions];
        
        // 1. Initialisation aléatoire des centroïdes (on prend des points au hasard dans les données)
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            int randomIdx = random.nextInt(nombreDonnees);
            centroids[i] = Arrays.copyOf(donnees[randomIdx], dimensions);
        }
        
        boolean hasChanged = true;
        int iterations = 0;
        
        // 2. Boucle principale de K-Means
        while (hasChanged && iterations < maxIterations) {
            hasChanged = false;
            
            // Étape A : Pour chaque point, trouver le centroïde le plus proche
            for (int i = 0; i < nombreDonnees; i++) {
                int meilleurCluster = 0;
                double distanceMin = Double.MAX_VALUE;
                
                for (int c = 0; c < k; c++) {
                    double dist = distanceCalculator.calculerDistance(donnees[i], centroids[c]);
                    if (dist < distanceMin) {
                        distanceMin = dist;
                        meilleurCluster = c;
                    }
                }
                
                if (labels[i] != meilleurCluster) {
                    labels[i] = meilleurCluster;
                    hasChanged = true;
                }
            }
            
            // Étape B : Recalculer la position des centroïdes (faire la moyenne des points de chaque groupe)
            double[][] nouveauxCentroids = new double[k][dimensions];
            int[] compteurs = new int[k];
            
            for (int i = 0; i < nombreDonnees; i++) {
                int cluster = labels[i];
                for (int d = 0; d < dimensions; d++) {
                    nouveauxCentroids[cluster][d] += donnees[i][d];
                }
                compteurs[cluster]++;
            }
            
            for (int c = 0; c < k; c++) {
                if (compteurs[c] > 0) {
                    for (int d = 0; d < dimensions; d++) {
                        centroids[c][d] = nouveauxCentroids[c][d] / compteurs[c];
                    }
                }
            }
            
            iterations++;
        }
        
        return labels;
    }
    
    // Pour récupérer les couleurs (ou positions) moyennes trouvées à la fin
    public double[][] getCentroids() {
        return centroids;
    }
}
