package vista;

import dtos.JugadorDTO;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modeloPresentacion.EventoPresentacion;
import modeloPresentacion.IConsultaModeloPresentacion;
import modeloPresentacion.ObservadorPresentacion;
import vista.componentes.PnlJugador;
import vista.util.Ubicacion;

public class VistaJugadores implements ObservadorPresentacion {

    private IConsultaModeloPresentacion modelo;
    private List<JugadorDTO> jugadores = List.of();
    private final String jugadorLocal;
    private final Map<String, PnlJugador> paneles = new LinkedHashMap<>();

    public VistaJugadores(String jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
    }

    public void setModelo(IConsultaModeloPresentacion modelo) {
        this.modelo = modelo;
    }

    @Override
    public void actualizar(EventoPresentacion evento) {
        if (evento != EventoPresentacion.JUGADORES_ACTUALIZADOS) {
            return;
        }
        List<JugadorDTO> nuevos = modelo.getJugadores();
        if (nuevos.isEmpty()) {
            return;
        }
        jugadores = List.copyOf(nuevos);
        pintarJugadores();
    }

    private void pintarJugadores() {
        if (paneles.isEmpty()) {
            List<String> nombres = new ArrayList<>();
            jugadores.forEach(j -> nombres.add(j.getNombre()));
            Ubicacion.distribuir(nombres, jugadorLocal)
                    .forEach((nombre, ubicacion) -> paneles.put(nombre, new PnlJugador(ubicacion)));
        }
        Map<String, JugadorDTO> porNombre = new LinkedHashMap<>();
        jugadores.forEach(j -> porNombre.put(j.getNombre(), j));
        paneles.forEach((nombre, panel) -> {
            JugadorDTO jugador = porNombre.get(nombre);
            if (jugador == null) {
                panel.marcarAusente();
            } else {
                panel.actualizar(jugador);
            }
        });
    }

    public Map<String, PnlJugador> getPaneles() {
        return paneles;
    }

    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    public JugadorDTO getJugadorEnTurno() {
        for (JugadorDTO j : jugadores) {
            if (j.isEnTurno()) {
                return j;
            }
        }
        return null;
    }

    public void setVisible(boolean visible) {
        paneles.values().forEach(p -> p.setVisible(visible));
    }
}
