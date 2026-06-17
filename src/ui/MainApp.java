package src.ui;

import src.image.*;
import src.clustering.*;
import src.distance.*;
import src.biome.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class MainApp extends Application {

    private BufferedImage imageOriginale;
    private BufferedImage imageFloutee;
    private int[]   labelsKMeans;
    private Biome[] biomesTrouves;
    private final TabPane tabs = new TabPane();
    private final Label   status = new Label("Chargez une image pour commencer");
    private static final int K = 5;

    private static final Biome[] BIOMES = {
        new Biome("Toundra",         new int[]{71,  70,  61}),
        new Biome("Taïga",           new int[]{43,  50,  35}),
        new Biome("Forêt tempérée",  new int[]{59,  66,  43}),
        new Biome("Forêt tropicale", new int[]{46,  64,  34}),
        new Biome("Savane",          new int[]{84, 106,  70}),
        new Biome("Prairie",         new int[]{104, 95,  82}),
        new Biome("Désert",          new int[]{152, 140, 120}),
        new Biome("Glacier",         new int[]{200, 200, 200}),
        new Biome("Eau peu profonde",new int[]{49,  83, 100}),
        new Biome("Eau profonde",    new int[]{12,  31,  47}),
    };

    @Override
    public void start(Stage stage) {
        Button btnCharger     = new Button("Charger image");
        Button btnFlou        = new Button("Flou Gaussien");
        Button btnBiomes      = new Button("Détecter biomes");
        Button btnEcosystemes = new Button("Détecter écosystèmes");

        btnCharger.setOnAction(e -> chargerImage(stage));
        btnFlou.setOnAction(e -> appliquerFlou());
        btnBiomes.setOnAction(e -> new Thread(this::detecterBiomes).start());
        btnEcosystemes.setOnAction(e -> new Thread(this::detecterEcosystemes).start());

        BorderPane root = new BorderPane();
        root.setTop(new HBox(10, btnCharger, btnFlou, btnBiomes, btnEcosystemes, status));
        root.setCenter(tabs);

        stage.setTitle("Détection de biomes");
        stage.setScene(new Scene(root, 1000, 750));
        stage.show();
    }

    private void ajouterOnglet(String titre, BufferedImage img) {
        WritableImage wi = new WritableImage(img.getWidth(), img.getHeight());
        PixelWriter pw = wi.getPixelWriter();
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                pw.setArgb(x, y, img.getRGB(x, y));
        Platform.runLater(() -> {
            ImageView iv = new ImageView(wi);
            iv.setPreserveRatio(true);
            iv.setFitWidth(900);
            Tab t = new Tab(titre, new ScrollPane(iv));
            tabs.getTabs().add(t);
            tabs.getSelectionModel().select(t);
        });
    }

    private void chargerImage(Stage stage) {
        File f = new FileChooser().showOpenDialog(stage);
        if (f == null) return;
        try {
            imageOriginale = ImageIO.read(f);
            imageFloutee   = imageOriginale;
            tabs.getTabs().clear();
            ajouterOnglet("Originale", imageOriginale);
            status.setText(f.getName());
        } catch (Exception ex) {
            status.setText("Erreur de chargement");
        }
    }

    private void appliquerFlou() {
        if (imageOriginale == null) return;
        imageFloutee = FiltreImage.appliquerFiltre(imageOriginale, FiltreImage.creerFiltreGaussien(7, 2.0));
        ajouterOnglet("Floutée", imageFloutee);
        status.setText("Flou appliqué");
    }

    private BufferedImage fondClair(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage fond = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int[] c = OutilCouleur.getTabColor(src.getRGB(x, y));
                fond.setRGB(x, y, new Color(
                    (int) Math.round(c[0] + 0.75 * (255 - c[0])),
                    (int) Math.round(c[1] + 0.75 * (255 - c[1])),
                    (int) Math.round(c[2] + 0.75 * (255 - c[2]))
                ).getRGB());
            }
        return fond;
    }

    private void detecterBiomes() {
        if (imageFloutee == null) return;
        int w = imageFloutee.getWidth(), h = imageFloutee.getHeight();

        double[][] pixels = new double[w * h][3];
        int idx = 0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int[] rgb = OutilCouleur.getTabColor(imageFloutee.getRGB(x, y));
                pixels[idx][0] = rgb[0]; pixels[idx][1] = rgb[1]; pixels[idx][2] = rgb[2];
                idx++;
            }

        KMeans km = new KMeans(new DistanceEuclidienne());
        labelsKMeans  = km.clusteriser(pixels, K);
        biomesTrouves = new BiomeLabeler(BIOMES, new DistanceCIELAB()).etiqueterCentroids(km.getCentroids());

        BufferedImage[] imgs = new BufferedImage[K];
        for (int c = 0; c < K; c++) imgs[c] = fondClair(imageOriginale);

        idx = 0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                imgs[labelsKMeans[idx]].setRGB(x, y, imageOriginale.getRGB(x, y));
                idx++;
            }

        for (int c = 0; c < K; c++) {
            String nom = biomesTrouves[c] != null ? biomesTrouves[c].getNom() : "Inconnu";
            ajouterOnglet("Biome: " + nom, imgs[c]);
        }
        Platform.runLater(() -> status.setText("Biomes détectés"));
    }

    private void detecterEcosystemes() {
        if (labelsKMeans == null) return;
        int w = imageFloutee.getWidth(), h = imageFloutee.getHeight();
        int step = 5;
        Random rand = new Random();

        for (int cluster = 0; cluster < K; cluster++) {
            List<double[]> pts    = new ArrayList<>();
            List<int[]>    coords = new ArrayList<>();
            for (int y = 0; y < h; y += step)
                for (int x = 0; x < w; x += step)
                    if (labelsKMeans[y * w + x] == cluster) {
                        pts.add(new double[]{x, y});
                        coords.add(new int[]{x, y});
                    }

            int[] eco = new DBSCAN(15.0, 10, new DistanceEuclidienne())
                    .clusteriser(pts.toArray(new double[0][0]), 0);

            BufferedImage img = fondClair(imageOriginale);
            Map<Integer, Color> couleurs = new HashMap<>();
            for (int i = 0; i < eco.length; i++) {
                if (eco[i] <= 0) continue;
                couleurs.computeIfAbsent(eco[i], id -> new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                int[] c = coords.get(i);
                int rgb = couleurs.get(eco[i]).getRGB();
                for (int dy = 0; dy < step && c[1] + dy < h; dy++)
                    for (int dx = 0; dx < step && c[0] + dx < w; dx++)
                        img.setRGB(c[0] + dx, c[1] + dy, rgb);
            }
            String nom = biomesTrouves[cluster] != null ? biomesTrouves[cluster].getNom() : "Inconnu";
            ajouterOnglet("Eco: " + nom, img);
        }
        Platform.runLater(() -> status.setText("Écosystèmes détectés"));
    }

    public static void main(String[] args) { launch(args); }
}
