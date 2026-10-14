package juego.objetosNegocio;

import java.util.List;

public record PropuestaFin(int idProponente, List<Integer> pendientes) {

    public PropuestaFin {
        pendientes = List.copyOf(pendientes);
    }
}
