package modelo;

import java.awt.Color;
import vista.util.Styles;

/**
 * Todas las variantes de color de un jugador, derivadas de un color base.
 * Asi agregar un color nuevo es una sola linea en Styles.PALETAS.
 */
public class PaletaColor {

    private final String nombre;
    private final Color fuerte;
    private final Color pastel;
    private final Color claro;
    private final Color oscuro;

    public PaletaColor(String nombre, Color base) {
        this.nombre = nombre;
        this.fuerte = base;
        this.pastel = mezclar(base, Color.WHITE, Styles.FACTOR_PASTEL);
        this.claro = mezclar(base, Color.WHITE, Styles.FACTOR_CLARO);
        this.oscuro = mezclar(base, Color.BLACK, Styles.FACTOR_OSCURO);
    }

    /** Mezcla dos colores; t=0 devuelve a, t=1 devuelve b. */
    public static Color mezclar(Color a, Color b, float t) {
        float u = Math.max(0f, Math.min(1f, t));
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * u);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * u);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * u);
        return new Color(r, g, bl);
    }

    public String getNombre() {
        return nombre;
    }

    public Color getFuerte() {
        return fuerte;
    }

    public Color getPastel() {
        return pastel;
    }

    public Color getClaro() {
        return claro;
    }

    public Color getOscuro() {
        return oscuro;
    }
}