package controlador;

import modelo.Coordenada;

public interface IControlJuego {

    void puntoPresionado(Coordenada coordenada);

    void seleccionCancelada();

    void opcionSeleccionada(String nombreOpcion);
}