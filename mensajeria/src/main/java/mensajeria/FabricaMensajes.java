package mensajeria;

import java.time.LocalDateTime;

public class FabricaMensajes implements IFabricaMensajes {

    private volatile String remitente;
    private final String miIp;
    private final int miPuerto;

    public FabricaMensajes(String miIp, int miPuerto) {
        this.miIp = miIp;
        this.miPuerto = miPuerto;
    }

    @Override
    public IMensajeAdmon crearMensaje() {
        Mensaje m = new Mensaje();
        m.setRemitente(remitente);
        m.setFechaHora(LocalDateTime.now());
        m.setIpOrigen(miIp);
        m.setPuertoOrigen(miPuerto);
        return m;
    }

    @Override
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }
}
