package src.biome;

public class Biome {
    private String nom;
    private int[] couleurRGB;

    public Biome(String nom, int[] couleurRGB) {
        this.nom = nom;
        this.couleurRGB = couleurRGB;
    }

    public String getNom() { return nom; }
    public int[] getCouleurRGB() { return couleurRGB; }
}
