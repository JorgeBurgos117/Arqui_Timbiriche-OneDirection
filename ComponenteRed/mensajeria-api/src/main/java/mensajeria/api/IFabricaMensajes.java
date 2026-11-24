package mensajeria.api;

public interface IFabricaMensajes {
    IMensajeAdmon crearMensaje();
    void setRemitente(String remitente);
}
