package juego.dominio;

import java.util.List;
import java.util.Map;

public record EstadoJuego(
        int filas,
        int columnas,
        List<JugadorInfo> jugadores,
        Integer idEnTurno,
        Map<Arista, Integer> lineas,
        Map<Coordenada, Integer> cuadros,
        Coordenada seleccion,
        List<Coordenada> candidatos,
        EstadoPartida estado,
        MotivoFin motivoFin,
        Integer idGanador,
        PropuestaFin propuestaFin,
        int propuestasRechazadas) {

    public EstadoJuego {
        jugadores = List.copyOf(jugadores);
        lineas = Map.copyOf(lineas);
        cuadros = Map.copyOf(cuadros);
        candidatos = List.copyOf(candidatos);
    }

    public JugadorInfo jugador(int id) {
        for (JugadorInfo j : jugadores) {
            if (j.id() == id) {
                return j;
            }
        }
        return null;
    }

    public boolean esActivo(int id) {
        JugadorInfo j = jugador(id);
        return j != null && j.activo();
    }

    public boolean enCurso() {
        return estado == EstadoPartida.EN_CURSO;
    }
}
