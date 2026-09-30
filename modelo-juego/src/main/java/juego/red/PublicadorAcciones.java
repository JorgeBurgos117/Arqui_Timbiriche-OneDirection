package juego.red;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import juego.dominio.Accion;
import juego.modelo.ColaAcciones;

public class PublicadorAcciones {

    private final ColaAcciones cola;
    private final List<IObservadorRed> destinos = new CopyOnWriteArrayList<>();
    private Thread hilo;

    public PublicadorAcciones(ColaAcciones cola) {
        this.cola = cola;
    }

    public void agregarDestino(IObservadorRed destino) {
        destinos.add(destino);
    }

    public void quitarDestino(IObservadorRed destino) {
        destinos.remove(destino);
    }

    public synchronized void iniciar() {
        if (hilo != null) {
            return;
        }
        hilo = new Thread(this::despachar, "publicador-acciones");
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        if (hilo != null) {
            hilo.interrupt();
            hilo = null;
        }
    }

    private void despachar() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Accion accion = cola.tomar();
                for (IObservadorRed destino : destinos) {
                    try {
                        destino.update(accion);
                    } catch (RuntimeException e) {
                        System.err.println("[PUBLICADOR] Fallo al enviar " + accion + ": " + e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
