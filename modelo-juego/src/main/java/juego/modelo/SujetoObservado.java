package juego.modelo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SujetoObservado {

    private final List<ObservadorModeloJuego> observadores = new CopyOnWriteArrayList<>();

    public void agregarObservador(ObservadorModeloJuego observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void eliminarObservador(ObservadorModeloJuego observador) {
        observadores.remove(observador);
    }

    /** protected (#): solo la clase hija decide cuándo avisar. */
    protected void notificarObservadores(EventoModelo evento) {
        for (ObservadorModeloJuego o : observadores) {
            o.actualizar(evento);
        }
    }
}