package vista.util;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import modelo.PaletaColor;

public final class Styles {

    private Styles() {
    }

    // ------------------------------------------------------------------ VENTANA
    public static final String TITULO_VENTANA = "Timbiriche";
    public static final int ANCHO_MINIMO_VENTANA = 820;
    public static final int ALTO_MINIMO_VENTANA = 720;
    public static final Color COLOR_FONDO = Color.WHITE;

    // ------------------------------------------------------------------ TABLERO
    public static final int FILAS_PUNTOS_DEFAULT = 11;
    public static final int COLUMNAS_PUNTOS_DEFAULT = 11;

    /** Distancia en pixeles entre dos puntos vecinos. */
    public static final int ESPACIADO_PUNTOS = 32;
    /** Margen entre el borde del tablero y la primera fila/columna de puntos. */
    public static final int MARGEN_TABLERO = 22;
    /** Padding alrededor del tablero dentro del area central. */
    public static final int PADDING_AREA_TABLERO = 24;

    public static final int DIAMETRO_PUNTO = 7;
    public static final int DIAMETRO_PUNTO_RESALTADO = 15;
    /** Area sensible al mouse de cada punto (cuadrado centrado en el punto). */
    public static final int TAM_AREA_PUNTO = 24;

    public static final int GROSOR_LINEA = 7;
    public static final int INSET_CUADRO = 4;
    public static final int ARCO_CUADRO = 6;

    public static final Color COLOR_PUNTO_NEUTRO = new Color(55, 55, 55);

    // ------------------------------------------------------------------ JUGADOR
    /** Alto de los paneles NORTE/SUR y ancho de los paneles ESTE/OESTE. */
    public static final int GROSOR_PANEL_JUGADOR = 160;
    public static final int DIAMETRO_AVATAR = 84;
    public static final int GROSOR_BORDE_AVATAR = 3;

    public static final String TEXTO_PUNTOS = "PUNTOS:";
    public static final Font FUENTE_NOMBRE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_PUNTOS_ETIQUETA = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FUENTE_PUNTOS_VALOR = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_INICIAL_CUADRO = new Font("Segoe UI", Font.BOLD, 13);

    // ------------------------------------------------------------------ MENU
    public static final int MARGEN_MENU = 14;
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final int RADIO_BOTON = 14;
    public static final Color COLOR_BOTON_FONDO = new Color(45, 95, 175);
    public static final Color COLOR_BOTON_TEXTO = Color.WHITE;
    public static final Color COLOR_BOTON_PELIGRO = new Color(215, 55, 55);
    public static final Color COLOR_BOTON_NEUTRO = new Color(105, 115, 130);

    // ------------------------------------------------------------------ DIALOGO
    public static final String TITULO_DLG_OPCIONES = "Opciones";
    public static final int ANCHO_DLG_OPCIONES = 300;
    public static final int MARGEN_DLG_OPCIONES = 20;
    public static final int SEPARACION_BOTONES_DLG = 12;

    // ------------------------------------------------------------------ PALETAS
    /** Factores para derivar las variantes a partir del color base. */
    public static final float FACTOR_PASTEL = 0.60f;
    public static final float FACTOR_CLARO = 0.84f;
    public static final float FACTOR_OSCURO = 0.22f;

    public static final List<PaletaColor> PALETAS = List.of(
            new PaletaColor("Verde", new Color(46, 175, 80)),
            new PaletaColor("Amarillo", new Color(200, 160, 10)),
            new PaletaColor("Rojo", new Color(215, 45, 45)),
            new PaletaColor("Azul", new Color(25, 110, 220)),
            new PaletaColor("Morado", new Color(140, 70, 200)),
            new PaletaColor("Naranja", new Color(235, 120, 25)),
            new PaletaColor("Turquesa", new Color(20, 165, 170)),
            new PaletaColor("Rosa", new Color(230, 80, 155))
    );
}