package juego.dominio;

import java.io.Serializable;

public sealed interface Accion extends Serializable {

    int idJugador();

    record SeleccionarPunto(int idJugador, Coordenada punto) implements Accion {
    }

    record CancelarSeleccion(int idJugador) implements Accion {
    }

    record TrazarLinea(int idJugador, Coordenada a, Coordenada b) implements Accion {
    }

    record Abandonar(int idJugador) implements Accion {
    }

    record ProponerFin(int idJugador) implements Accion {
    }

    record VotarFin(int idJugador, boolean acepta) implements Accion {
    }
}
