package modeloPresentacion;

import dtos.FiguraDTO;
import dtos.JugadorDTO;
import dtos.TipoFigura;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import modeloJuego.objetosNegocio.Cuadro;
import modeloJuego.objetosNegocio.EstadoCuadro;
import modeloJuego.objetosNegocio.Jugador;
import modeloJuego.objetosNegocio.Linea;
import modeloJuego.objetosNegocio.Punto;

public class TraductorModeloJuego {

    public static final Color COLOR_JUGADOR_RETIRADO = new Color(200, 200, 205);
    private static final double TOLERANCIA_CLIC = 0.45;

    private final int tamanoCelda;
    private final int margen;
    private final Map<String, Color> colores = new HashMap<>();

    public TraductorModeloJuego(int tamanoCelda, int margen) {
        this.tamanoCelda = tamanoCelda;
        this.margen = margen;
    }

    public void asignarColor(String nombreJugador, Color color) {
        colores.put(nombreJugador, color);
    }

    public Color getColor(String nombreJugador) {
        return colores.getOrDefault(nombreJugador, COLOR_JUGADOR_RETIRADO);
    }

    public FiguraDTO aFiguraPunto(Punto punto, Color color) {
        return aFiguraPunto(punto, color, punto);
    }

    public FiguraDTO aFiguraPunto(Punto punto, Color color, Punto origen) {
        return new FiguraDTO(TipoFigura.PUNTO,
                aX(punto.getColumna()), aY(punto.getFila()),
                aX(origen.getColumna()), aY(origen.getFila()),
                color, null);
    }

    public FiguraDTO aFiguraLinea(Linea linea) {
        Jugador jugador = linea.getJugador();
        Color color = jugador.isActivo() ? getColor(jugador.getNombre()) : COLOR_JUGADOR_RETIRADO;
        return new FiguraDTO(TipoFigura.LINEA,
                aX(linea.getPunto1().getColumna()), aY(linea.getPunto1().getFila()),
                aX(linea.getPunto2().getColumna()), aY(linea.getPunto2().getFila()),
                color, null);
    }

    public FiguraDTO aFiguraCuadro(Cuadro cuadro) {
        Jugador jugador = cuadro.getJugador();
        boolean visible = cuadro.getEstado() == EstadoCuadro.RECLAMADO && jugador != null && jugador.isActivo();
        return new FiguraDTO(TipoFigura.CUADRO,
                aX(cuadro.getColumna()), aY(cuadro.getFila()),
                aX(cuadro.getColumna() + 1), aY(cuadro.getFila() + 1),
                visible ? getColor(jugador.getNombre()) : null,
                visible ? inicial(jugador.getNombre()) : null);
    }

    public JugadorDTO aJugadorDTO(Jugador jugador, boolean enTurno) {
        return new JugadorDTO(jugador.getNombre(), jugador.getAvatar(), getColor(jugador.getNombre()),
                jugador.getPuntaje(), enTurno);
    }

    public int aFila(int y) {
        return aIndice(y);
    }

    public int aColumna(int x) {
        return aIndice(x);
    }

    private int aIndice(int pixel) {
        int indice = (int) Math.round((pixel - margen) / (double) tamanoCelda);
        int centro = margen + indice * tamanoCelda;
        if (indice < 0 || Math.abs(pixel - centro) > tamanoCelda * TOLERANCIA_CLIC) {
            return -1;
        }
        return indice;
    }

    private int aX(int columna) {
        return margen + columna * tamanoCelda;
    }

    private int aY(int fila) {
        return margen + fila * tamanoCelda;
    }

    private static String inicial(String nombre) {
        return nombre == null || nombre.isBlank() ? "?" : nombre.substring(0, 1).toUpperCase();
    }

    public int getTamanoCelda() {
        return tamanoCelda;
    }

    public int getLadoTablero(int puntosPorLado) {
        return margen * 2 + tamanoCelda * (puntosPorLado - 1);
    }
}
