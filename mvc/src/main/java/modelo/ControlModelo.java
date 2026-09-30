package modelo;

import java.util.HashSet;
import java.util.Map;
import juego.dominio.Coordenada;
import juego.modelo.IControlJuego;

public class ControlModelo implements IControlModelo {

    private final DatosVisuales datos;
    private final IControlJuego controlJuego;

    public ControlModelo(DatosVisuales datos, IControlJuego controlJuego) {
        this.datos = datos;
        this.controlJuego = controlJuego;
    }

    @Override
    public void validar(int x, int y) {
        Coordenada punto = datos.getGeometria().aCoordenada(x, y);
        Integer id = datos.idActuante();
        if (punto != null && id != null) {
            controlJuego.validar(punto, id);
        }
    }

    @Override
    public void moverCursor(int x, int y) {
        datos.setHover(datos.getGeometria().aCoordenada(x, y));
    }

    @Override
    public void salirDelTablero() {
        datos.setHover(null);
    }

    @Override
    public void cancelarSeleccion() {
        Integer id = datos.idActuante();
        if (id != null) {
            controlJuego.cancelarSeleccion(id);
        }
    }

    @Override
    public boolean cambiarColores(Map<Integer, PaletaColor> paletas) {
        if (paletas.containsValue(null) || new HashSet<>(paletas.values()).size() != paletas.size()) {
            return false;
        }
        datos.setPaletas(paletas);
        return true;
    }

    @Override
    public void abandonar() {
        Integer id = datos.idActuante();
        if (id != null) {
            controlJuego.abandonar(id);
        }
    }

    @Override
    public void proponerFin() {
        Integer id = datos.idActuante();
        if (id != null) {
            controlJuego.proponerFin(id);
        }
    }

    @Override
    public void votarFin(int idVotante, boolean acepta) {
        if (datos.esLocal(idVotante)) {
            controlJuego.votarFin(idVotante, acepta);
        }
    }
}
