package mensajeria;

public interface IMensajeria {

    IMensajeApp crearMensaje();

    void enviar(IMensajeApp mensaje);

    void registrar(String nombre);

    void darseDeBaja();

    void setReceptor(IReceptorMensajes receptor);
}
