# Diagrammes du Projet

## 1. Diagramme d'Architecture

Les différents modules (packages) du projet et leurs dépendances. L'interface utilisateur (`ui`) orchestre les autres modules pour le traitement d'image, le clustering et la classification des biomes.

```mermaid
graph TD
    UI[Package: ui<br>Interface Graphique & Orchestration]
    
    IMG[Package: image<br>Traitement et Utilitaires d'Image]
    CLUSTERING[Package: clustering<br>Algorithmes de Regroupement]
    BIOME[Package: biome<br>Modèle et Étiquetage de Biome]
    DISTANCE[Package: distance<br>Calculs de Distances]

    UI --> IMG
    UI --> CLUSTERING
    UI --> BIOME
    UI --> DISTANCE
    
    CLUSTERING --> DISTANCE
    BIOME --> DISTANCE
    
    classDef package fill:#f9f,stroke:#333,stroke-width:2px;
    class UI,IMG,CLUSTERING,BIOME,DISTANCE package;
```

## 2. Diagramme de Classe

```mermaid
classDiagram
    class MainApp {
        -BufferedImage imageOriginale
        -BufferedImage imageFloutee
        -int[] labelsKMeans
        -Biome[] biomesTrouves
        -TabPane tabs
        -Label status
        +start(Stage stage)
        -chargerImage(Stage stage)
        -appliquerFlou()
        -detecterBiomes()
        -detecterEcosystemes()
        +main(String[] args)$
    }

    namespace image {
        class FiltreImage {
            +creerFiltreMoyenne(int taille)$ double[][]
            +creerFiltreGaussien(int taille, double sigma)$ double[][]
            +appliquerFiltre(BufferedImage image, double[][] filtre)$ BufferedImage
        }

        class OutilCouleur {
            +getTabColor(int c)$ int[]
        }

        class SauvegardeImage {
            +chargerImage(String chemin)$ BufferedImage
            +sauvegarderImage(BufferedImage image, String format, String chemin)$ void
        }
    }

    namespace distance {
        class IDistance {
            <<interface>>
            +calculerDistance(double[] a, double[] b) double
        }

        class DistanceEuclidienne {
            +calculerDistance(double[] a, double[] b) double
        }

        class DistanceCIELAB {
            +calculerDistance(double[] a, double[] b) double
            -rgbToLab(double r, double g, double b) double[]
        }
    }

    namespace clustering {
        class IClustering {
            <<interface>>
            +clusteriser(double[][] donnees, int k) int[]
        }

        class KMeans {
            -IDistance distanceCalculator
            -int maxIterations
            -double[][] centroids
            +clusteriser(double[][] donnees, int k) int[]
            +getCentroids() double[][]
        }

        class DBSCAN {
            -double epsilon
            -int minPts
            -IDistance distance
            +clusteriser(double[][] donnees, int k_ignore) int[]
            -etendreCluster(...)
            -trouverVoisins(...) List~Integer~
        }
    }

    namespace biome {
        class Biome {
            -String nom
            -int[] couleurRGB
            +getNom() String
            +getCouleurRGB() int[]
        }

        class BiomeLabeler {
            -Biome[] biomesConnus
            -IDistance calculateurDistance
            +etiqueterCentroids(double[][] centroids) Biome[]
        }
    }

    %% Relations
    IDistance <|.. DistanceEuclidienne
    IDistance <|.. DistanceCIELAB
    IClustering <|.. KMeans
    IClustering <|.. DBSCAN
    
    KMeans --> "1" IDistance : utilise
    DBSCAN --> "1" IDistance : utilise
    BiomeLabeler --> "1" IDistance : utilise
    BiomeLabeler --> "*" Biome : biomesConnus
    
    MainApp --> FiltreImage : utilise
    MainApp --> KMeans : utilise
    MainApp --> DBSCAN : utilise
    MainApp --> BiomeLabeler : utilise
    MainApp --> Biome : gère
    MainApp --> OutilCouleur : utilise
```

## 3. Diagrammes de Séquence

### A. Flux de détection des Biomes (`detecterBiomes`)

Lorsque l'utilisateur clique sur le bouton "Détecter biomes".

```mermaid
sequenceDiagram
    actor Utilisateur
    participant UI as MainApp
    participant Outil as OutilCouleur
    participant DistEuc as DistanceEuclidienne
    participant KM as KMeans
    participant DistLab as DistanceCIELAB
    participant Labeler as BiomeLabeler

    Utilisateur->>UI: Clic sur "Détecter biomes"
    activate UI
    UI->>UI: Extraction des pixels (imageFloutee)
    loop Pour chaque pixel
        UI->>Outil: getTabColor(rgb)
        Outil-->>UI: int[] {r, g, b}
    end
    
    UI->>DistEuc: new DistanceEuclidienne()
    UI->>KM: new KMeans(DistanceEuclidienne)
    UI->>KM: clusteriser(pixels, K=5)
    activate KM
    KM-->>UI: int[] labelsKMeans
    deactivate KM
    
    UI->>KM: getCentroids()
    activate KM
    KM-->>UI: double[][] centroids
    deactivate KM
    
    UI->>DistLab: new DistanceCIELAB()
    UI->>Labeler: new BiomeLabeler(BIOMES, DistanceCIELAB)
    UI->>Labeler: etiqueterCentroids(centroids)
    activate Labeler
    Labeler-->>UI: Biome[] biomesTrouves
    deactivate Labeler
    
    UI->>UI: Création des images segmentées (onglets)
    UI->>Utilisateur: Affiche les onglets avec les biomes
    deactivate UI
```

### B. Flux de détection des Écosystèmes (`detecterEcosystemes`)

Interactions pour la sous-segmentation spatiale des clusters (écosystèmes).

```mermaid
sequenceDiagram
    actor Utilisateur
    participant UI as MainApp
    participant DistEuc as DistanceEuclidienne
    participant DB as DBSCAN

    Utilisateur->>UI: Clic sur "Détecter écosystèmes"
    activate UI
    loop Pour chaque cluster (Biome) K
        UI->>UI: Récupère les points spatiaux (x, y) du cluster
        UI->>DistEuc: new DistanceEuclidienne()
        UI->>DB: new DBSCAN(15.0, 10, DistanceEuclidienne)
        UI->>DB: clusteriser(points, 0)
        activate DB
        DB-->>UI: int[] labelsEcosystemes
        deactivate DB
        
        UI->>UI: Coloration aléatoire de chaque écosystème trouvé
        UI->>UI: Ajout d'un nouvel onglet pour ce Biome
    end
    UI->>Utilisateur: Affiche les onglets des écosystèmes
    deactivate UI
```
