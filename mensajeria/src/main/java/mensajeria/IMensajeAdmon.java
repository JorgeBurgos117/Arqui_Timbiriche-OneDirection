package mensajeria;

import java.time.LocalDateTime;

public interface IMensajeAdmon extends IMensajeApp {

    String SERVIDOR = "servidor";

    // Sin enum: un mensaje es de administración si su contenido trae este prefijo reservado
    String PREFIJO = "#admon:";
    String REGISTRO = PREFIJO + "registro";
    String BAJA = PREFIJO + "baja";
    String PING = PREFIJO + "ping";
    String CONFIRMA_REGISTRO = PREFIJO + "confirma-registro";
    String CONFIRMA_BAJA = PREFIJO + "confirma-baja";
    String ELIMINAR = PREFIJO + "eliminar:";

    static boolean esAdministrativo(IMensajeApp mensaje) {
        return mensaje.getContenido() != null && mensaje.getContenido().startsWith(PREFIJO);
    }

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
}
