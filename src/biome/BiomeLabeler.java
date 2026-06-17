package src.biome;

import src.distance.IDistance;

public class BiomeLabeler {
    private Biome[] biomesConnus;
    private IDistance calculateurDistance;

    public BiomeLabeler(Biome[] biomesConnus, IDistance calculateurDistance) {
        this.biomesConnus = biomesConnus;
        this.calculateurDistance = calculateurDistance;
    }

    /**
     * Associe chaque couleur moyenne trouvée par K-Means au biome le plus proche
     * (par exemple, si K-Means trouve du Bleu Foncé, on l'associe au Biome "Océan")
     */
    public Biome[] etiqueterCentroids(double[][] centroids) {
        Biome[] labels = new Biome[centroids.length];
        
        java.util.List<Biome> biomesDisponibles = new java.util.ArrayList<>(java.util.Arrays.asList(biomesConnus));
        
        for (int i = 0; i < centroids.length; i++) {
            double[] centroidCouleur = centroids[i]; 
            
            double distanceMin = Double.MAX_VALUE;
            Biome meilleurBiome = null;
            
            for (Biome b : biomesDisponibles) {
                double[] biomeCouleur = new double[] { b.getCouleurRGB()[0], b.getCouleurRGB()[1], b.getCouleurRGB()[2] };
                double dist = calculateurDistance.calculerDistance(centroidCouleur, biomeCouleur);
                
                if (dist < distanceMin) {
                    distanceMin = dist;
                    meilleurBiome = b;
                }
            }
            labels[i] = meilleurBiome;
            if (meilleurBiome != null) {
                biomesDisponibles.remove(meilleurBiome); // Assure l'unicité !
            }
        }
        return labels;
    }
}
