package mensajeria;

public class ManejadorAplicacion implements IManejadorMensaje {

    private volatile IReceptorMensajes receptor;

    @Override
    public void setReceptor(IReceptorMensajes receptor) {
        this.receptor = receptor;
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        if (receptor != null) {
            receptor.recibir(mensaje);
        }
    }
}
