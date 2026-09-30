package modelo;

import juego.dominio.Coordenada;

public final class GeometriaTablero {

    public static final int MARGEN = 20;
    private static final double RADIO_CLICK = 0.45;

    private final int filas;
    private final int columnas;
    private final int lado;
    private final double espaciado;

    public GeometriaTablero(int filas, int columnas, int lado) {
        this.filas = filas;
        this.columnas = columnas;
        this.lado = lado;
        this.espaciado = (lado - 2.0 * MARGEN) / (Math.max(filas, columnas) - 1);
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getLado() {
        return lado;
    }

    public double getEspaciado() {
        return espaciado;
    }

    public double x(int columna) {
        return MARGEN + columna * espaciado;
    }

    public double y(int fila) {
        return MARGEN + fila * espaciado;
    }

    public Coordenada aCoordenada(int px, int py) {
        int columna = (int) Math.round((px - MARGEN) / espaciado);
        int fila = (int) Math.round((py - MARGEN) / espaciado);
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        double dx = px - x(columna);
        double dy = py - y(fila);
        double radio = espaciado * RADIO_CLICK;
        return dx * dx + dy * dy <= radio * radio ? new Coordenada(fila, columna) : null;
    }

    public double grosorLinea() {
        return limitar(espaciado * 0.16, 3, 8);
    }

    public double diametroPunto() {
        return limitar(espaciado * 0.14, 4, 8);
    }

    public double diametroResaltado() {
        return limitar(espaciado * 0.32, 8, 16);
    }

    public double insetCuadro() {
        return limitar(espaciado * 0.12, 2, 6);
    }

    public double arcoCuadro() {
        return limitar(espaciado * 0.18, 3, 8);
    }

    public float tamFuenteInicial() {
        return (float) limitar(espaciado * 0.38, 8, 20);
    }

    private static double limitar(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
    }
}
