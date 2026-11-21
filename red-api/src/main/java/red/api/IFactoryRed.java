package red.api;

public interface IFactoryRed {

    IDispatcher crearDispatcher();

    // Regresa el puerto en el que quedó escuchando (útil si se pidió el 0)
    int crearReceptor(int puerto, IReceptorExterno externo);

    void detener();
}
