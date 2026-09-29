package vista.util;
import vista.util.Styles;

//traduce coordenadas lógicas a pixeles en el tablero
public final class Geometria {

    private Geometria() {
    }

    public static int px(int columna) {
        return Styles.MARGEN_TABLERO + columna * Styles.ESPACIADO_PUNTOS;
    }

    public static int py(int fila) {
        return Styles.MARGEN_TABLERO + fila * Styles.ESPACIADO_PUNTOS;
    }

    public static int anchoTablero(int columnas) {
        return Styles.MARGEN_TABLERO * 2 + (columnas - 1) * Styles.ESPACIADO_PUNTOS;
    }

    public static int altoTablero(int filas) {
        return Styles.MARGEN_TABLERO * 2 + (filas - 1) * Styles.ESPACIADO_PUNTOS;
    }
}