package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import modelo.DatosJugador;
import modelo.Ubicacion;
import vista.util.Styles;
import vista.componentes.PnlContenedorMenu;
import vista.componentes.PnlJugador;
import vista.componentes.tablero.PnlTablero;

//contenedor base

public class PnlContenido extends JLayeredPane {

    private final JPanel pnlJuego = new JPanel(new BorderLayout());
    private final PnlContenedorMenu pnlContenedorMenu;
    private final PnlTablero pnlTablero;
    private final Map<Integer, PnlJugador> panelesJugador = new LinkedHashMap<>();

    public PnlContenido(List<DatosJugador> jugadores, int idLocal, int filas, int columnas,
                        PnlContenedorMenu.EscuchaMenu escuchaMenu) {

        this.pnlTablero = new PnlTablero(filas, columnas);
        this.pnlContenedorMenu = new PnlContenedorMenu(escuchaMenu);

        pnlJuego.setBackground(Styles.COLOR_FONDO);
        pnlJuego.setOpaque(true);

        JPanel areaTablero = new JPanel(new GridBagLayout());
        areaTablero.setOpaque(false);
        areaTablero.setBorder(BorderFactory.createEmptyBorder(
                Styles.PADDING_AREA_TABLERO, Styles.PADDING_AREA_TABLERO,
                Styles.PADDING_AREA_TABLERO, Styles.PADDING_AREA_TABLERO));
        areaTablero.add(pnlTablero);
        pnlJuego.add(areaTablero, BorderLayout.CENTER);

        Map<Integer, Ubicacion> ubicaciones = Ubicacion.distribuir(jugadores, idLocal);
        for (DatosJugador jugador : jugadores) {
            Ubicacion ubicacion = ubicaciones.get(jugador.id());
            PnlJugador panel = new PnlJugador(jugador, ubicacion);
            panelesJugador.put(jugador.id(), panel);
            pnlJuego.add(panel, aBorderLayout(ubicacion));
        }

        setOpaque(false);
        add(pnlJuego, JLayeredPane.DEFAULT_LAYER);
        add(pnlContenedorMenu, JLayeredPane.PALETTE_LAYER);
    }

    private String aBorderLayout(Ubicacion ubicacion) {
        return switch (ubicacion) {
            case NORTE -> BorderLayout.NORTH;
            case SUR -> BorderLayout.SOUTH;
            case ESTE -> BorderLayout.EAST;
            case OESTE -> BorderLayout.WEST;
        };
    }

    @Override
    public void doLayout() {
        pnlJuego.setBounds(0, 0, getWidth(), getHeight());
        Dimension d = pnlContenedorMenu.getPreferredSize();
        pnlContenedorMenu.setBounds(Styles.MARGEN_MENU, Styles.MARGEN_MENU, d.width, d.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return pnlJuego.getPreferredSize();
    }

    public PnlTablero getPnlTablero() {
        return pnlTablero;
    }

    public Map<Integer, PnlJugador> getPanelesJugador() {
        return panelesJugador;
    }
}