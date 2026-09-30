package juego.modelo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import juego.dominio.Accion;

public class ColaAcciones {

    private final BlockingQueue<Accion> cola = new LinkedBlockingQueue<>();

    public void add(Accion accion) {
        cola.add(accion);
    }

    public Accion tomar() throws InterruptedException {
        return cola.take();
    }

    public int tamanio() {
        return cola.size();
    }
}
