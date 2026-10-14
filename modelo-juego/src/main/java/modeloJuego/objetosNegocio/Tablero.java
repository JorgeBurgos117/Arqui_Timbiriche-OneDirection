package modeloJuego.objetosNegocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tablero {

    private final int filas;
    private final int columnas;
    private final List<Punto> puntos = new ArrayList<>();
    private final List<Linea> lineasMarcadas = new ArrayList<>();
    private final List<Cuadro> cuadros = new ArrayList<>();

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                puntos.add(new Punto(f, c));
            }
        }
        for (int f = 0; f < filas - 1; f++) {
            for (int c = 0; c < columnas - 1; c++) {
                cuadros.add(new Cuadro(f, c));
            }
        }
    }

    public Punto getPunto(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return puntos.get(fila * columnas + columna);
    }

    public boolean validaPosicion(Punto punto1, Punto punto2) {
        return punto1 != null && punto2 != null && punto1.esAdyacente(punto2) && !existeLinea(punto1, punto2);
    }

    public boolean validaLleno() {
        for (Cuadro cuadro : cuadros) {
            if (!cuadro.estaCompleto()) {
                return false;
            }
        }
        return true;
    }

    public List<Punto> buscarPuntosDisponibles(Punto punto) {
        List<Punto> disponibles = new ArrayList<>(4);
        int f = punto.getFila();
        int c = punto.getColumna();
        Punto[] vecinos = {getPunto(f - 1, c), getPunto(f + 1, c), getPunto(f, c - 1), getPunto(f, c + 1)};
        for (Punto vecino : vecinos) {
            if (validaPosicion(punto, vecino)) {
                disponibles.add(vecino);
            }
        }
        return disponibles;
    }

    public Linea marcarLinea(Punto punto1, Punto punto2, Jugador jugador) {
        Linea linea = new Linea(punto1, punto2, jugador);
        lineasMarcadas.add(linea);
        for (Cuadro cuadro : cuadros) {
            if (cuadro.esLadoDe(linea)) {
                cuadro.agregarLinea(linea);
            }
        }
        return linea;
    }

    public List<Cuadro> verificarCuadro(Linea linea) {
        List<Cuadro> completados = new ArrayList<>(2);
        for (Cuadro cuadro : cuadros) {
            if (cuadro.esLadoDe(linea) && cuadro.estaCompleto() && cuadro.getEstado() != EstadoCuadro.RECLAMADO) {
                completados.add(cuadro);
            }
        }
        return completados;
    }

    private boolean existeLinea(Punto punto1, Punto punto2) {
        for (Linea linea : lineasMarcadas) {
            if (linea.une(punto1, punto2)) {
                return true;
            }
        }
        return false;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Linea> getLineasMarcadas() {
        return Collections.unmodifiableList(lineasMarcadas);
    }

    public List<Cuadro> getCuadros() {
        return Collections.unmodifiableList(cuadros);
    }
}
