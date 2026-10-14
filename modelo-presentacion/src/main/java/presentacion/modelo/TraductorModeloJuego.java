package presentacion.modelo;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import juego.objetosNegocio.*;
import presentacion.dtos.FiguraDTO;
import presentacion.dtos.JugadorDTO;
import presentacion.dtos.TipoFigura;

public class TraductorModeloJuego {

    private static final double RADIO_CLICK = 0.45;

    private final int tamanoCelda;
    private final int margen;
    private final Map<Integer, PaletaColor> paletas = new HashMap<>();

    public void asignarColor(int idJugador, PaletaColor paleta) {
        paletas.put(idJugador, paleta);
    }

    public Color colorDe(Jugador jugador) {            // líneas, cuadros, punto seleccionado
        PaletaColor p = jugador == null ? null : paletas.get(jugador.getId());
        return p == null ? Color.GRAY : p.getFuerte();
    }

    public Color colorClaroDe(Jugador jugador) {       // puntos posibles
        PaletaColor p = jugador == null ? null : paletas.get(jugador.getId());
        return p == null ? Color.LIGHT_GRAY : p.getPastel();
    }

    public TraductorModeloJuego(int tamanoCelda, int margen) {
        this.tamanoCelda = tamanoCelda;
        this.margen = margen;
    }

    //Fila del punto más cercano al clic, o -1 si el clic no cayó cerca de ningún punto.
    public int aFila(int x, int y) {
        return cercaDeUnPunto(x, y) ? Math.round((float) (y - margen) / tamanoCelda) : -1;
    }

    public int aColumna(int x, int y) {
        return cercaDeUnPunto(x, y) ? Math.round((float) (x - margen) / tamanoCelda) : -1;
    }

    private boolean cercaDeUnPunto(int x, int y) {
        int col = Math.round((float) (x - margen) / tamanoCelda);
        int fila = Math.round((float) (y - margen) / tamanoCelda);
        double dx = x - x(col), dy = y - y(fila), radio = tamanoCelda * RADIO_CLICK;
        return dx * dx + dy * dy <= radio * radio;
    }

    //juego -> DTOs (para el receptor)

    public FiguraDTO aFiguraPunto(Punto punto, Color color) {
        int px = x(punto.getColumna()), py = y(punto.getFila());
        return new FiguraDTO(TipoFigura.PUNTO, px, py, px, py, color);
    }

    public FiguraDTO aFiguraLinea(Linea linea) {
        return new FiguraDTO(TipoFigura.LINEA,
                x(linea.getPunto1().getColumna()), y(linea.getPunto1().getFila()),
                x(linea.getPunto2().getColumna()), y(linea.getPunto2().getFila()),
                colorDe(linea.getJugador()));
    }

    public FiguraDTO aFiguraCuadro(Cuadro cuadro) {
        return new FiguraDTO(TipoFigura.CUADRO,
                x(cuadro.getColumna()), y(cuadro.getFila()),
                x(cuadro.getColumna() + 1), y(cuadro.getFila() + 1),
                colorDe(cuadro.getJugador()));
    }

    public JugadorDTO aJugadorDTO(Jugador jugador, boolean enTurno) {
        return new JugadorDTO(jugador.getNombre(), jugador.getAvatar(),
                colorDe(jugador), jugador.getPuntaje(), enTurno);
    }

    private int x(int columna) {
        return margen + columna * tamanoCelda;
    }
    private int y(int fila) {
        return margen + fila * tamanoCelda;
    }
}