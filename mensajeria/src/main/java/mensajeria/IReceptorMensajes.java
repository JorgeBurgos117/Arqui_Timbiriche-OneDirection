package mensajeria;

public interface IReceptorMensajes {

    void recibir(IMensajeApp mensaje);

    void estadoBuzon(boolean activo);
}
