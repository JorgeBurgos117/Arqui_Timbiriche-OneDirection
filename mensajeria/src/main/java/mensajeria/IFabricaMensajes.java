package mensajeria;

public interface IFabricaMensajes {

    IMensajeAdmon crearMensaje();

    void setRemitente(String remitente);
}
