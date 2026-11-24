package mensajeria.api;

public interface ISerializador {
    String serializar(IMensajeAdmon mensaje) throws Exception;
    IMensajeAdmon deserializar(String texto) throws Exception;
}
