package modelo;

import java.awt.Color;
import java.util.List;

public final class PaletaColor {

    public static final float FACTOR_PASTEL = 0.60f;
    public static final float FACTOR_CLARO = 0.84f;
    public static final float FACTOR_OSCURO = 0.22f;

    public static final List<PaletaColor> DISPONIBLES = List.of(
            new PaletaColor("Verde", new Color(46, 175, 80)),
            new PaletaColor("Amarillo", new Color(200, 160, 10)),
            new PaletaColor("Rojo", new Color(215, 45, 45)),
            new PaletaColor("Azul", new Color(25, 110, 220)),
            new PaletaColor("Morado", new Color(140, 70, 200)),
            new PaletaColor("Naranja", new Color(235, 120, 25)),
            new PaletaColor("Turquesa", new Color(20, 165, 170)),
            new PaletaColor("Rosa", new Color(230, 80, 155))
    );

    private final String nombre;
    private final Color fuerte;
    private final Color pastel;
    private final Color claro;
    private final Color oscuro;

    public PaletaColor(String nombre, Color base) {
        this.nombre = nombre;
        this.fuerte = base;
        this.pastel = mezclar(base, Color.WHITE, FACTOR_PASTEL);
        this.claro = mezclar(base, Color.WHITE, FACTOR_CLARO);
        this.oscuro = mezclar(base, Color.BLACK, FACTOR_OSCURO);
    }

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

    @Override
    public String toString() {
        return nombre;
    }
}
