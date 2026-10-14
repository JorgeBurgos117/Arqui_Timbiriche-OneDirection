package juego.objetosNegocio;


import java.util.Objects;

public final class Punto {

    private final int fila;
    private final int columna;

    public Punto(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public boolean esAdyacente(Punto otro) {
        int df = Math.abs(fila - otro.fila);  // distancia de filas absoluta
        int dc = Math.abs(columna - otro.columna); // distancia de columnas absoluta
        return df + dc == 1; // si son contiguos en una cuadricula debe ser 1, de lo contrario no es.
    }

    public boolean esIgual(int fila, int columna) {
        return this.fila == fila && this.columna == columna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Punto otro)) return false;
        return fila == otro.fila && columna == otro.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}

