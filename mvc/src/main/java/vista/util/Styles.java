package vista.util;

import java.awt.Color;
import java.awt.Font;

public final class Styles {

    private Styles() {
    }

    public static final String TITULO_VENTANA = "Timbiriche";
    public static final Color COLOR_FONDO = Color.WHITE;

    public static final int LADO_AREA_TABLERO = 540;
    public static final int PADDING_AREA_TABLERO = 20;
    public static final Color COLOR_FONDO_TABLERO = new Color(246, 247, 249);
    public static final int ARCO_FONDO_TABLERO = 18;
    public static final Color COLOR_PUNTO_NEUTRO = new Color(55, 55, 55);
    public static final Color COLOR_LINEA_HUERFANA = new Color(200, 200, 205);

    public static final int GROSOR_PANEL_JUGADOR = 150;
    public static final int DIAMETRO_AVATAR = 80;
    public static final int GROSOR_BORDE_AVATAR = 3;

    public static final String TEXTO_PUNTOS = "PUNTOS:";
    public static final Font FUENTE_NOMBRE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_PUNTOS_ETIQUETA = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FUENTE_PUNTOS_VALOR = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_INICIAL_CUADRO = new Font("Segoe UI", Font.BOLD, 13);

    public static final int MARGEN_MENU = 14;
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final int RADIO_BOTON = 14;
    public static final Color COLOR_BOTON_FONDO = new Color(45, 95, 175);
    public static final Color COLOR_BOTON_TEXTO = Color.WHITE;
    public static final Color COLOR_BOTON_PELIGRO = new Color(215, 55, 55);
    public static final Color COLOR_BOTON_NEUTRO = new Color(105, 115, 130);

    public static final String TITULO_DLG_OPCIONES = "Opciones";
    public static final int ANCHO_DLG_OPCIONES = 300;
    public static final int MARGEN_DLG = 20;
    public static final int SEPARACION_BOTONES_DLG = 12;
    public static final Font FUENTE_DLG = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_DLG_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);

    public static final Font FUENTE_TITULO_MENU = new Font("Segoe UI", Font.BOLD, 40);
    public static final Font FUENTE_SUBTITULO_MENU = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FUENTE_OPCION_MENU = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FUENTE_NOTA_MENU = new Font("Segoe UI", Font.ITALIC, 12);
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(110, 115, 125);
    public static final int MARGEN_MENU_PRINCIPAL = 40;
}
