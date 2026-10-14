package vista.util;

import java.awt.Color;

/**
 * Variaciones de un color para pintar (mismo cálculo que tenía PaletaColor).
 * Es solo estética: la vista recibe UN color en el DTO y de ahí saca los tonos.
 */
public final class Tonos {

    private Tonos() {
    }

    public static Color pastel(Color base) {
        return mezclar(base, Color.WHITE, 0.60f);
    }

    public static Color claro(Color base) {
        return mezclar(base, Color.WHITE, 0.84f);
    }

    public static Color oscuro(Color base) {
        return mezclar(base, Color.BLACK, 0.22f);
    }

    private static Color mezclar(Color a, Color b, float t) {
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }
}