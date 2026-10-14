package modeloJuego;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SujetoObservado {

    private final List<ObservadorModeloJuego> observadores = new CopyOnWriteArrayList<>();

    public void agregarObservador(ObservadorModeloJuego observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(ObservadorModeloJuego observador) {
        observadores.remove(observador);
    }

    protected void notificarObservadores(EventoModelo evento) {
        for (ObservadorModeloJuego observador : observadores) {
            observador.actualizar(evento);
        }
    }
}
