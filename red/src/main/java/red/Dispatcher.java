package red;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import red.api.IDispatcher;

public class Dispatcher implements IDispatcher {

    private final BlockingQueue<Paquete> cola = new LinkedBlockingQueue<>();
    private ClienteTCP clienteTCP;
    private int reintentos = 3;
    private long esperaEntreReintentosMs = 500;
    private Thread hilo;
    private volatile boolean activo;

    public void setClienteTCP(ClienteTCP clienteTCP) {
        this.clienteTCP = clienteTCP;
    }

    public void setReintentos(int reintentos) {
        this.reintentos = reintentos;
    }

    @Override
    public void enviar(String mensaje, String ip, int puerto) {
        cola.add(new Paquete(mensaje, ip, puerto));
    }

    public void iniciar() {
        activo = true;
        hilo = new Thread(this::procesarCola, "red-dispatcher");
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
                enviarConReintentos(cola.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void enviarConReintentos(Paquete paquete) throws InterruptedException {
        for (int intento = 1; intento <= reintentos; intento++) {
            try {
                clienteTCP.enviar(paquete);
                return;
            } catch (IOException e) {
                if (intento == reintentos) {
                    System.err.println("[red] No se pudo enviar a " + paquete.ip() + ":" + paquete.puerto()
                            + " -> " + e.getMessage());
                    return;
                }
                Thread.sleep(esperaEntreReintentosMs);
            }
        }
    }
}
