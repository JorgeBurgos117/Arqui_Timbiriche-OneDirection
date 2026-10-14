package modeloJuego.objetosNegocio;

import java.util.List;

public class PuntoSeleccionado {

    private final Punto punto;
    private final List<Punto> posibles;

    public PuntoSeleccionado(Punto punto, List<Punto> posibles) {
        this.punto = punto;
        this.posibles = List.copyOf(posibles);
    }

    public Punto getPunto() {
        return punto;
    }

    public List<Punto> getPosibles() {
        return posibles;
    }
}
