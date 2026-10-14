package vista.juego;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import presentacion.dtos.JugadorDTO;
import presentacion.modelo.EventoPresentacion;
import presentacion.modelo.IConsultaModeloPresentacion;
import presentacion.modelo.IObservadorPresentacion;
import vista.util.Styles;
import vista.util.Ubicacion;

/**
 * Observa al Modelo de Presentación y pinta a los jugadores en su lado del tablero.
 * Tiene un PnlJugador por cada lado; la ventana (FrmJuego) los acomoda alrededor del tablero.
 * El primer JugadorDTO que llega es el jugador local, y siempre va abajo (SUR).
 */
public class VistaJugadores implements IObservadorPresentacion {

    private final IConsultaModeloPresentacion modelo;
    private final Map<Ubicacion, PnlJugador> paneles = new EnumMap<>(Ubicacion.class);
    private List<JugadorDTO> jugadores = List.of();

    public VistaJugadores(IConsultaModeloPresentacion modelo) {
        this.modelo = modelo;
        for (Ubicacion u : Ubicacion.values()) {
            PnlJugador panel = new PnlJugador(u);
            panel.setVisible(false);          // se muestra cuando llega un jugador para ese lado
            paneles.put(u, panel);
        }
    }

    public PnlJugador getPanel(Ubicacion ubicacion) {
        return paneles.get(ubicacion);
    }

    @Override
    public void actualizar(EventoPresentacion evento) {
        switch (evento) {
            case JUGADORES_ACTUALIZADOS -> {
                List<JugadorDTO> nuevos = modelo.getJugadores();   // saca lo que hay en la cola
                if (!nuevos.isEmpty()) {
                    jugadores = nuevos;
                    pintarJugadores();
                }
            }
            case PARTIDA_TERMINADA -> mostrarFin();
            default -> { }
        }
    }

    private void pintarJugadores() {
        List<Ubicacion> lados = Ubicacion.paraJugadores(jugadores.size());
        boolean aparecioAlguno = false;
        for (int i = 0; i < jugadores.size(); i++) {
            PnlJugador panel = paneles.get(lados.get(i));
            panel.actualizar(jugadores.get(i));
            if (!panel.isVisible()) {
                panel.setVisible(true);
                aparecioAlguno = true;
            }
        }
        // La primera vez que aparecen los jugadores, la ventana se reacomoda a su tamaño completo
        Window ventana = SwingUtilities.getWindowAncestor(paneles.get(Ubicacion.SUR));
        if (aparecioAlguno && ventana != null) {
            ventana.pack();
        }
    }

    private void mostrarFin() {
        StringBuilder texto = new StringBuilder("La partida terminó.\n\n");
        for (JugadorDTO j : jugadores) {
            texto.append(j.getNombre()).append(": ").append(j.getPuntaje()).append(" puntos\n");
        }
        JOptionPane.showMessageDialog(paneles.get(Ubicacion.SUR), texto.toString(),
                Styles.TITULO_VENTANA, JOptionPane.INFORMATION_MESSAGE);
    }
}