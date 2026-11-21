package mensajeria;

public interface IManejadorMensaje {

    void manejar(IMensajeAdmon mensaje);

    default void setReceptor(IReceptorMensajes receptor) {
    }
}
