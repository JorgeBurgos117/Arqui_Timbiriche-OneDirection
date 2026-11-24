package mensajeria.api;

import java.time.LocalDateTime;

public interface IMensajeAdmon {
    String getRemitente();
    void setRemitente(String remitente);
    LocalDateTime getFechaHora();
    void setFechaHora(LocalDateTime fechaHora);
    String getIpOrigen();
    void setIpOrigen(String ipOrigen);
    int getPuertoOrigen();
    void setPuertoOrigen(int puertoOrigen);
    String getIpDestino();
    void setIpDestino(String ipDestino);
    int getPuertoDestino();
    void setPuertoDestino(int puertoDestino);
    String getDestinatario();
    void setDestinatario(String destinatario);
    String getContenido();
    void setContenido(String contenido);
}
