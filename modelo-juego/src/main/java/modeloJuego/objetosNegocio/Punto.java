package modeloJuego.objetosNegocio;

import java.util.Objects;

public class Punto {

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
        return otro != null && Math.abs(fila - otro.fila) + Math.abs(columna - otro.columna) == 1;
    }

    public boolean esIgual(int fila, int columna) {
        return this.fila == fila && this.columna == columna;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Punto p && p.fila == fila && p.columna == columna;
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
