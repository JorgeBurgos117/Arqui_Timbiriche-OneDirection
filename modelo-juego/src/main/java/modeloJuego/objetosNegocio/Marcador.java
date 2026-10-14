package modeloJuego.objetosNegocio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Marcador {

    private final List<Jugador> jugadores;

    public Marcador(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public List<Jugador> calcularPosiciones() {
        List<Jugador> activos = new ArrayList<>();
        List<Jugador> retirados = new ArrayList<>();
        for (Jugador j : jugadores) {
            (j.isActivo() ? activos : retirados).add(j);
        }
        activos.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());
        activos.addAll(retirados);
        return activos;
    }

    public Jugador getGanador() {
        Jugador ganador = null;
        boolean empate = false;
        for (Jugador j : jugadores) {
            if (!j.isActivo()) {
                continue;
            }
            if (ganador == null || j.getPuntaje() > ganador.getPuntaje()) {
                ganador = j;
                empate = false;
            } else if (j.getPuntaje() == ganador.getPuntaje()) {
                empate = true;
            }
        }
        return empate ? null : ganador;
    }
}
