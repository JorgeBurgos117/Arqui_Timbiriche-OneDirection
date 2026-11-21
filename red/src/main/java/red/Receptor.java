package red;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import red.api.IReceptorExterno;

public class Receptor {

    private final BlockingQueue<String> cola = new LinkedBlockingQueue<>();
    private IReceptorExterno receptorExterno;
    private Thread hilo;
    private volatile boolean activo;

    public void setReceptorExterno(IReceptorExterno receptorExterno) {
        this.receptorExterno = receptorExterno;
    }

    public void encolar(String mensaje) {
        cola.add(mensaje);
    }

    public void iniciar() {
        activo = true;
        hilo = new Thread(this::procesarCola, "red-receptor");
        hilo.setDaemon(true);
        hilo.start();
    }

    public void detener() {
        activo = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }

    private void procesarCola() {
        while (activo) {
            try {
                String mensaje = cola.take();
                try {
                    receptorExterno.recibir(mensaje);
                } catch (RuntimeException e) {
                    System.err.println("[red] El receptor externo falló: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
