package vista.juego;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import vista.util.Styles;
import vista.util.Ubicacion;

/** Ventana de UN jugador: el tablero al centro y los jugadores alrededor. */
public class FrmJuego extends JFrame {

    public FrmJuego(String nombreJugador, VistaTablero vistaTablero, VistaJugadores vistaJugadores) {
        super(Styles.TITULO_VENTANA + " - " + nombreJugador);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Styles.COLOR_FONDO);

        int pad = Styles.PADDING_AREA_TABLERO;
        JPanel areaTablero = new JPanel(new GridBagLayout());
        areaTablero.setOpaque(false);
        areaTablero.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        areaTablero.add(vistaTablero);
        contenido.add(areaTablero, BorderLayout.CENTER);

        contenido.add(vistaJugadores.getPanel(Ubicacion.NORTE), BorderLayout.NORTH);
        contenido.add(vistaJugadores.getPanel(Ubicacion.SUR), BorderLayout.SOUTH);
        contenido.add(vistaJugadores.getPanel(Ubicacion.ESTE), BorderLayout.EAST);
        contenido.add(vistaJugadores.getPanel(Ubicacion.OESTE), BorderLayout.WEST);

        setContentPane(contenido);
    }

    /** indice: para que las ventanas de cada jugador no salgan una encima de otra. */
    public void mostrar(int indice) {
        pack();
        setLocation(40 + indice * 60, 20 + indice * 40);
        setVisible(true);
    }
}