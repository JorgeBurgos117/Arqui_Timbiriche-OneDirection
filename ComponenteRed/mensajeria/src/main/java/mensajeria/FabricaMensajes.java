package mensajeria;

import java.time.LocalDateTime;
import mensajeria.api.IFabricaMensajes;
import mensajeria.api.IMensajeAdmon;

public class FabricaMensajes implements IFabricaMensajes {
    private String remitente;
    private final String miIp;
    private final int miPuerto;

    public FabricaMensajes(String miIp, int miPuerto) {
        this.miIp = miIp;
        this.miPuerto = miPuerto;
    }

    @Override
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    @Override
    public IMensajeAdmon crearMensaje() {
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setFechaHora(LocalDateTime.now());
        mensaje.setIpOrigen(miIp);
        mensaje.setPuertoOrigen(miPuerto);
        return mensaje;
    }
}
