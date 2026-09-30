package vista.juego;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import controlador.IControlVista;
import modelo.Aviso;
import modelo.IModeloVista;
import modelo.IObservadorVista;
import modelo.VotoPendiente;
import vista.util.Styles;

public class FrmJuego extends JFrame implements IObservadorVista {

    private final IModeloVista modelo;
    private final PnlTablero pnlTablero;
    private final PnlContenido pnlContenido;
    private IControlVista control;

    private int ultimoAvisoMostrado = 0;
    private boolean votoEnPantalla = false;
    private boolean finMostrado = false;

    public FrmJuego(IModeloVista modelo) {
        super(Styles.TITULO_VENTANA);
        this.modelo = modelo;
        this.pnlTablero = new PnlTablero(modelo);
        this.pnlContenido = new PnlContenido(modelo.getJugadores(), pnlTablero, this::abrirOpciones);

        setContentPane(pnlContenido);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
        registrarEscape();
    }

    public void setControl(IControlVista control) {
        this.control = control;
        pnlTablero.setControl(control);
    }

    public void mostrar() {
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
        update();
    }

    @Override
    public void update() {
        pnlContenido.actualizarJugadores(modelo.getJugadores());
        pnlTablero.repaint();
        revisarAvisos();
        revisarVoto();
        revisarFin();
    }

    private void revisarAvisos() {
        for (Aviso aviso : modelo.getAvisos()) {
            if (aviso.numero() > ultimoAvisoMostrado) {
                ultimoAvisoMostrado = aviso.numero();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, aviso.texto(), Styles.TITULO_VENTANA,
                                JOptionPane.INFORMATION_MESSAGE));
            }
        }
    }

    private void revisarVoto() {
        if (votoEnPantalla || finMostrado) {
            return;
        }
        VotoPendiente voto = modelo.getVotoPendiente();
        if (voto == null) {
            return;
        }
        votoEnPantalla = true;
        SwingUtilities.invokeLater(() -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    voto.proponente() + " propone terminar la partida.\n¿" + voto.votante() + " está de acuerdo?",
                    "Terminar partida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            votoEnPantalla = false;
            if (control != null) {
                control.votarFin(voto.idVotante(), respuesta == JOptionPane.YES_OPTION);
            }
        });
    }

    private void revisarFin() {
        if (finMostrado || !modelo.isPartidaTerminada()) {
            return;
        }
        finMostrado = true;
        SwingUtilities.invokeLater(() -> {
            new DlgResultados(this, modelo.getTextoFin(), modelo.getResultados()).setVisible(true);
            if (control != null) {
                control.salirAlMenu();
            }
        });
    }

    private void abrirOpciones() {
        if (control != null && !modelo.isPartidaTerminada()) {
            new DlgOpciones(this, modelo, control).setVisible(true);
        }
    }

    private void confirmarSalida() {
        if (modelo.isPartidaTerminada()) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Salir de la partida y volver al menú principal?",
                Styles.TITULO_VENTANA, JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION && control != null) {
            control.salirAlMenu();
        }
    }

    private void registrarEscape() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancelarSeleccion");
        getRootPane().getActionMap().put("cancelarSeleccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (control != null) {
                    control.cancelarSeleccion();
                }
            }
        });
    }
}
