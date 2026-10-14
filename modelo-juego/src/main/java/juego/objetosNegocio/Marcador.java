package juego.objetosNegocio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Marcador {

    private final List<Jugador> jugadores;

    public Marcador(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public List<Jugador> calcularPosiciones() {
        List<Jugador> posiciones = new ArrayList<>(jugadores);
        posiciones.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());
        return posiciones;
    }

    public Jugador getGanador() {
        List<Jugador> posiciones = calcularPosiciones();
        if (posiciones.isEmpty()) {
            return null;
        }
        boolean empate = posiciones.size() > 1
                && posiciones.get(0).getPuntaje() == posiciones.get(1).getPuntaje();
        return empate ? null : posiciones.get(0);
    }
}

