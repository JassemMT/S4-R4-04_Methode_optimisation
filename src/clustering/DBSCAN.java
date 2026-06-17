package src.clustering;

import src.distance.IDistance;
import java.util.ArrayList;
import java.util.List;

public class DBSCAN implements IClustering {
    private double epsilon;
    private int minPts;
    private IDistance distance;

    public DBSCAN(double epsilon, int minPts, IDistance distance) {
        this.epsilon = epsilon;
        this.minPts = minPts;
        this.distance = distance;
    }

    @Override
    public int[] clusteriser(double[][] donnees, int k_ignore) {
        int n = donnees.length;
        int[] labels = new int[n]; // 0 = non visité, -1 = bruit, -2 = en file d'attente, >0 = cluster ID
        int clusterId = 0;

        for (int i = 0; i < n; i++) {
            if (labels[i] != 0) continue; // Déjà visité

            List<Integer> voisins = trouverVoisins(donnees, i);
            if (voisins.size() < minPts) {
                labels[i] = -1; // Considéré comme du Bruit
            } else {
                clusterId++;
                etendreCluster(donnees, labels, i, voisins, clusterId);
            }
        }
        return labels;
    }

    private void etendreCluster(double[][] donnees, int[] labels, int pointId, List<Integer> voisins, int clusterId) {
        labels[pointId] = clusterId;

        for (int i = 0; i < voisins.size(); i++) {
            int voisinId = voisins.get(i);
            
            if (labels[voisinId] == -1) {
                labels[voisinId] = clusterId; // Le bruit devient une bordure d'écosystème
            }
            
            if (labels[voisinId] == 0 || labels[voisinId] == -2) {
                labels[voisinId] = clusterId;
                List<Integer> nouveauxVoisins = trouverVoisins(donnees, voisinId);
                if (nouveauxVoisins.size() >= minPts) {
                    for (Integer nv : nouveauxVoisins) {
                        if (labels[nv] == 0) {
                            voisins.add(nv);
                            labels[nv] = -2; // En file d'attente
                        }
                    }
                }
            }
        }
    }

    private List<Integer> trouverVoisins(double[][] donnees, int pointId) {
        List<Integer> voisins = new ArrayList<>();
        for (int i = 0; i < donnees.length; i++) {
            // Le point ne se compte pas lui-même comme voisin (cf. cours CM Clustering)
            if (i != pointId && distance.calculerDistance(donnees[pointId], donnees[i]) <= epsilon) {
                voisins.add(i);
            }
        }
        return voisins;
    }
}
