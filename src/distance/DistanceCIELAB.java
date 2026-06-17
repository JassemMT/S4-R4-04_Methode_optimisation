package src.distance;

public class DistanceCIELAB implements IDistance {

    @Override
    public double calculerDistance(double[] a, double[] b) {
        // a et b sont attendus en format RGB [R, G, B]
        double[] labA = rgbToLab(a[0], a[1], a[2]);
        double[] labB = rgbToLab(b[0], b[1], b[2]);
        
        // Distance euclidienne classique mais sur l'espace LAB
        double dL = labA[0] - labB[0];
        double da = labA[1] - labB[1];
        double db = labA[2] - labB[2];
        
        return Math.sqrt(dL * dL + da * da + db * db);
    }

    /**
     * Convertit une couleur RGB en espace de couleur CIELAB.
     * Cette méthode imite la façon dont l'œil humain perçoit les différences de couleurs.
     */
    private double[] rgbToLab(double r, double g, double b) {
        // 1. Convertir RGB vers sRGB (de 0-255 vers 0-1)
        double sr = r / 255.0;
        double sg = g / 255.0;
        double sb = b / 255.0;

        sr = (sr > 0.04045) ? Math.pow((sr + 0.055) / 1.055, 2.4) : sr / 12.92;
        sg = (sg > 0.04045) ? Math.pow((sg + 0.055) / 1.055, 2.4) : sg / 12.92;
        sb = (sb > 0.04045) ? Math.pow((sb + 0.055) / 1.055, 2.4) : sb / 12.92;

        sr *= 100.0;
        sg *= 100.0;
        sb *= 100.0;

        // 2. Convertir sRGB vers XYZ
        double x = sr * 0.4124 + sg * 0.3576 + sb * 0.1805;
        double y = sr * 0.2126 + sg * 0.7152 + sb * 0.0722;
        double z = sr * 0.0193 + sg * 0.1192 + sb * 0.9505;

        // Référence de blanc (Illuminant D65)
        double ref_X = 95.047;
        double ref_Y = 100.000;
        double ref_Z = 108.883;

        x = x / ref_X;
        y = y / ref_Y;
        z = z / ref_Z;

        x = (x > 0.008856) ? Math.pow(x, 1.0 / 3.0) : (7.787 * x) + (16.0 / 116.0);
        y = (y > 0.008856) ? Math.pow(y, 1.0 / 3.0) : (7.787 * y) + (16.0 / 116.0);
        z = (z > 0.008856) ? Math.pow(z, 1.0 / 3.0) : (7.787 * z) + (16.0 / 116.0);

        // 3. Convertir XYZ vers CIELAB
        double L = (116.0 * y) - 16.0;
        double a_val = 500.0 * (x - y);
        double b_val = 200.0 * (y - z);

        return new double[]{L, a_val, b_val};
    }
}
