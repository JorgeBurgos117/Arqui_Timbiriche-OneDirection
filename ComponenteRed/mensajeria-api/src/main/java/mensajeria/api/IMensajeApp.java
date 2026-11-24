package mensajeria.api;

public interface IMensajeApp {
    String getRemitente();
    void setDestinatario(String destinatario);
    String getDestinatario();
    void setContenido(String contenido);
    String getContenido();
}
