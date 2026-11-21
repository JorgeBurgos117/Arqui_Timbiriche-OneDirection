package mensajeria;

import red.api.IFactoryRed;

public interface IFactoryMensajeria {

    IMensajeria crearMensajeriaCliente(IFactoryRed red, String miIp, int miPuerto, Nodo servidor);

    IMensajeria crearMensajeriaServidor(IFactoryRed red, String miIp, int miPuerto);

    void detener();
}
