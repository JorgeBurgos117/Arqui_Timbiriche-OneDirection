package mensajeria.api;

public interface IReceptorMensajes {
    void recibir(IMensajeApp mensaje);
    void estadoBuzon(boolean activo);
}
