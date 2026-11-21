package mensajeria;

public interface ISerializador {

    String serializar(IMensajeAdmon mensaje) throws SerializacionException;

    IMensajeAdmon deserializar(String texto) throws SerializacionException;
}
