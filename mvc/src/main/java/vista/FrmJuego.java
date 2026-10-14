package vista;

import controlador.ControlEjercerTurno;
import controlador.ControlOpcionesPartida;
import dtos.VotacionDTO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import modeloPresentacion.EventoPresentacion;
import modeloPresentacion.IConsultaModeloPresentacion;
import modeloPresentacion.ObservadorPresentacion;
import vista.componentes.BtnRedondeado;
import vista.componentes.PnlJugador;
import vista.util.Dialogos;
import vista.util.Styles;
import vista.util.Ubicacion;

public class FrmJuego extends JFrame implements ObservadorPresentacion {

    private final VistaJugadores vistaJugadores;
    private final IConsultaModeloPresentacion modelo;
    private final ControlEjercerTurno controlTurno;
    private final ControlOpcionesPartida controlOpciones;
    private final JPanel pnlJuego = new JPanel(new BorderLayout());
    private final JPanel pnlBotonOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private boolean terminada = false;
    private boolean votoEnPantalla = false;
    private boolean teniaTurno = false;

    public FrmJuego(String titulo, VistaTablero vistaTablero, VistaJugadores vistaJugadores,
                    IConsultaModeloPresentacion modelo, ControlEjercerTurno controlTurno,
                    ControlOpcionesPartida controlOpciones) {
        super(titulo);
        this.vistaJugadores = vistaJugadores;
        this.modelo = modelo;
        this.controlTurno = controlTurno;
        this.controlOpciones = controlOpciones;
        armar(vistaTablero);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
        registrarEscape();
    }

    private void armar(VistaTablero vistaTablero) {
        pnlJuego.setBackground(Styles.COLOR_FONDO);
        int pad = Styles.PADDING_AREA_TABLERO;
        JPanel areaTablero = new JPanel(new GridBagLayout());
        areaTablero.setOpaque(false);
        areaTablero.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        areaTablero.add(vistaTablero);
        pnlJuego.add(areaTablero, BorderLayout.CENTER);

        boolean hayNorte = false;
        for (PnlJugador panel : vistaJugadores.getPaneles().values()) {
            pnlJuego.add(panel, switch (panel.getUbicacion()) {
                case NORTE -> BorderLayout.NORTH;
                case SUR -> BorderLayout.SOUTH;
                case ESTE -> BorderLayout.EAST;
                case OESTE -> BorderLayout.WEST;
            });
            hayNorte |= panel.getUbicacion() == Ubicacion.NORTE;
        }

        BtnRedondeado btnOpciones = new BtnRedondeado("OPCIONES", Styles.COLOR_BOTON_FONDO, Styles.COLOR_BOTON_TEXTO);
        btnOpciones.addActionListener(e -> abrirOpciones());
        pnlBotonOpciones.setOpaque(false);
        pnlBotonOpciones.add(btnOpciones);
        if (!hayNorte) {
            int alto = pnlBotonOpciones.getPreferredSize().height + Styles.MARGEN_MENU * 2;
            pnlJuego.add(Box.createVerticalStrut(alto), BorderLayout.NORTH);
        }

        JLayeredPane capas = new JLayeredPane() {
            @Override
            public void doLayout() {
                pnlJuego.setBounds(0, 0, getWidth(), getHeight());
                Dimension d = pnlBotonOpciones.getPreferredSize();
                pnlBotonOpciones.setBounds(Styles.MARGEN_MENU, Styles.MARGEN_MENU, d.width, d.height);
            }

            @Override
            public Dimension getPreferredSize() {
                return pnlJuego.getPreferredSize();
            }
        };
        capas.add(pnlJuego, JLayeredPane.DEFAULT_LAYER);
        capas.add(pnlBotonOpciones, JLayeredPane.PALETTE_LAYER);
        setContentPane(capas);
    }

    public void preparar() {
        pack();
        setMinimumSize(getSize());
    }

    public void mostrar(int x, int y) {
        setLocation(x, y);
        setVisible(true);
    }

    public void enfocarSiEsMiTurno() {
        teniaTurno = modelo.esMiTurno();
        if (teniaTurno && !terminada) {
            toFront();
            requestFocus();
        }
    }

    @Override
    public void actualizar(EventoPresentacion evento) {
        switch (evento) {
            case PARTIDA_TERMINADA -> terminada = true;
            case VOTACION_PENDIENTE -> pedirVoto();
            case JUGADORES_ACTUALIZADOS -> {
                boolean tieneTurno = modelo.esMiTurno();
                if (tieneTurno && !teniaTurno && !terminada && isVisible()) {
                    SwingUtilities.invokeLater(() -> {
                        toFront();
                        requestFocus();
                    });
                }
                teniaTurno = tieneTurno;
            }
            default -> {
            }
        }
    }

    private void pedirVoto() {
        if (votoEnPantalla || terminada) {
            return;
        }
        VotacionDTO votacion = modelo.getVotacionPendiente();
        if (votacion == null) {
            return;
        }
        votoEnPantalla = true;
        SwingUtilities.invokeLater(() -> Dialogos.preguntar(this,
                votacion.getProponente() + " propone terminar la partida.\n¿Estás de acuerdo?",
                "Terminar partida", JOptionPane.QUESTION_MESSAGE, acepta -> {
                    votoEnPantalla = false;
                    controlOpciones.votarFin(votacion.getVotante(), acepta);
                }));
    }

    private void abrirOpciones() {
        if (!terminada) {
            new DlgOpciones(this, vistaJugadores, controlOpciones).setVisible(true);
        }
    }

    private void confirmarSalida() {
        if (terminada) {
            controlOpciones.cerrarVentana();
            return;
        }
        Dialogos.preguntar(this, "¿Abandonar la partida?\nTus cuadros desaparecerán y se cerrará esta ventana.",
                getTitle(), JOptionPane.WARNING_MESSAGE, acepta -> {
                    if (acepta) {
                        controlOpciones.abandonar();
                    }
                });
    }

    private void registrarEscape() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancelarSeleccion");
        getRootPane().getActionMap().put("cancelarSeleccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlTurno.cancelarSeleccion();
            }
        });
    }
}
