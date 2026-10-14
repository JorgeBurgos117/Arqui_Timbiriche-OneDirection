package juego.modelo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SujetoObservado {

    private final List<IObservadorModeloJuego> observadores = new CopyOnWriteArrayList<>();

    public void agregarObservador(IObservadorModeloJuego observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void eliminarObservador(IObservadorModeloJuego observador) {
        observadores.remove(observador);
    }

    protected void notificarObservadores(EventoModelo evento) {
        for (IObservadorModeloJuego o : observadores) {
            o.actualizar(evento);
        }
    }
}