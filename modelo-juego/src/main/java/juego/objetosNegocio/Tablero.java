package juego.objetosNegocio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tablero {

    private final int filas;
    private final int columnas;
    private final List<Punto> puntos = new ArrayList<>();
    private final List<Linea> lineasMarcadas = new ArrayList<>();
    private final List<Cuadro> cuadros = new ArrayList<>();
    private final Set<Linea> indiceLineas = new HashSet<>();

    public Tablero(int filas, int columnas) {
        if (filas < 2 || columnas < 2) {
            throw new IllegalArgumentException("El tablero necesita al menos 2x2 puntos");
        }
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

    /** Punto en esa posición, o null si está fuera del tablero. */
    public Punto getPunto(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return puntos.get(fila * columnas + columna);
    }

    public boolean validaPosicion(Punto punto1, Punto punto2) {
        if (punto1 == null || punto2 == null || !punto1.esAdyacente(punto2)) {
            return false;
        }
        return !indiceLineas.contains(new Linea(punto1, punto2, null));
    }

    public boolean validaLleno() {
        for (Cuadro cuadro : cuadros) {
            if (cuadro.getEstado() != EstadoCuadro.RECLAMADO) {
                return false;
            }
        }
        return true;
    }

    public List<Punto> buscarPuntosDisponibles(Punto punto) {
        List<Punto> disponibles = new ArrayList<>(4);
        int f = punto.getFila();
        int c = punto.getColumna();
        agregarSiLibre(disponibles, punto, getPunto(f - 1, c));
        agregarSiLibre(disponibles, punto, getPunto(f + 1, c));
        agregarSiLibre(disponibles, punto, getPunto(f, c - 1));
        agregarSiLibre(disponibles, punto, getPunto(f, c + 1));
        return disponibles;
    }

    private void agregarSiLibre(List<Punto> lista, Punto origen, Punto destino) {
        if (destino != null && validaPosicion(origen, destino)) {
            lista.add(destino);
        }
    }

    public Linea marcarLinea(Punto punto1, Punto punto2, Jugador jugador) {
        Linea linea = new Linea(punto1, punto2, jugador);
        lineasMarcadas.add(linea);
        indiceLineas.add(linea);
        for (Cuadro cuadro : cuadrosVecinos(linea)) {
            if (cuadro.esLadoDe(linea)) {
                cuadro.agregarLinea(linea);
            }
        }
        return linea;
    }

    public List<Cuadro> verificarCuadro(Linea linea) {
        List<Cuadro> completados = new ArrayList<>(2);
        for (Cuadro cuadro : cuadrosVecinos(linea)) {
            if (cuadro.estaCompleto() && cuadro.getEstado() != EstadoCuadro.RECLAMADO) {
                completados.add(cuadro);
            }
        }
        return completados;
    }

    private List<Cuadro> cuadrosVecinos(Linea linea) {
        int f = linea.getPunto1().getFila();
        int c = linea.getPunto1().getColumna();
        List<Cuadro> vecinos = new ArrayList<>(2);
        if (linea.getPosicion() == Posicion.HORIZONTAL) {
            agregarCuadro(vecinos, f - 1, c);
            agregarCuadro(vecinos, f, c);
        } else {
            agregarCuadro(vecinos, f, c - 1);
            agregarCuadro(vecinos, f, c);
        }
        return vecinos;
    }

    private void agregarCuadro(List<Cuadro> lista, int fila, int columna) {
        if (fila >= 0 && fila < filas - 1 && columna >= 0 && columna < columnas - 1) {
            lista.add(cuadros.get(fila * (columnas - 1) + columna));
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Linea> getLineasMarcadas() {
        return List.copyOf(lineasMarcadas);
    }

    public List<Cuadro> getCuadros() {
        return List.copyOf(cuadros);
    }
}
