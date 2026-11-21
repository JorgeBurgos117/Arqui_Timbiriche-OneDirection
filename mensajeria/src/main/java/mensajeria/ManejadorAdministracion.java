package mensajeria;

public class ManejadorAdministracion implements IManejadorMensaje {

    private volatile IReceptorMensajes receptor;
    private final PingAlive pingAlive;
    private final IDirectorio directorio;

    public ManejadorAdministracion(PingAlive pingAlive, IDirectorio directorio) {
        this.pingAlive = pingAlive;
        this.directorio = directorio;
    }

    @Override
    public void setReceptor(IReceptorMensajes receptor) {
        this.receptor = receptor;
    }

    @Override
    public void manejar(IMensajeAdmon mensaje) {
        String contenido = mensaje.getContenido();
        if (contenido.equals(IMensajeAdmon.CONFIRMA_REGISTRO)) {
            pingAlive.iniciar();
            avisarBuzon(true);
        } else if (contenido.equals(IMensajeAdmon.CONFIRMA_BAJA)) {
            pingAlive.detener();
            avisarBuzon(false);
        } else if (contenido.startsWith(IMensajeAdmon.ELIMINAR)) {
            String nombre = contenido.substring(IMensajeAdmon.ELIMINAR.length());
            if (!nombre.equals(IMensajeAdmon.SERVIDOR)) {
                directorio.eliminar(nombre);
            }
        }
    }

    private void avisarBuzon(boolean activo) {
        if (receptor != null) {
            receptor.estadoBuzon(activo);
        }
    }
}
