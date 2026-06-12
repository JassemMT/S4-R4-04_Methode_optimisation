package src.image;

public class OutilCouleur {

    /**
     * Prend la couleur "compressée" (un seul gros chiffre) donnée par l'image
     * et la découpe en 3 vraies valeurs de couleurs (Rouge, Vert, Bleu).
     */
    public static int[] getTabColor(int c) {
        int bleu = c & 0xff;
        int vert = (c & 0xff00) >> 8;
        int rouge = (c & 0xff0000) >> 16;
        
        return new int[]{rouge, vert, bleu};
    }
}
