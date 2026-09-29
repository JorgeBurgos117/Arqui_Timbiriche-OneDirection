package vista.componentes.tablero;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JLayeredPane;
import controlador.IControlJuego;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Geometria;


public class PnlTablero extends JLayeredPane implements PnlPunto.EscuchaPunto {

    private static final Integer CAPA_CUADROS = 10;
    private static final Integer CAPA_LINEAS = 20;
    private static final Integer CAPA_PUNTOS = 30;

    private final int filas;
    private final int columnas;

    private final PnlMatrizCuadros pnlMatrizCuadros;
    private final PnlMatrizLineas pnlMatrizLineas;
    private final PnlMatrizPuntos pnlMatrizPuntos;

    private IControlJuego control;
    private PaletaColor paletaTurno;

    private Coordenada seleccion;
    private final Set<Coordenada> candidatos = new HashSet<>();

    public PnlTablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;

        this.pnlMatrizCuadros = new PnlMatrizCuadros(filas, columnas);
        this.pnlMatrizLineas = new PnlMatrizLineas(filas, columnas);
        this.pnlMatrizPuntos = new PnlMatrizPuntos(filas, columnas, this);

        setOpaque(false);
        add(pnlMatrizCuadros, CAPA_CUADROS);
        add(pnlMatrizLineas, CAPA_LINEAS);
        add(pnlMatrizPuntos, CAPA_PUNTOS);

        Dimension d = new Dimension(Geometria.anchoTablero(columnas), Geometria.altoTablero(filas));
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        pnlMatrizCuadros.setBounds(0, 0, w, h);
        pnlMatrizLineas.setBounds(0, 0, w, h);
        pnlMatrizPuntos.setBounds(0, 0, w, h);
    }

    public void setControl(IControlJuego control) {
        this.control = control;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setPaletaTurno(PaletaColor paleta) {
        this.paletaTurno = paleta;
        pnlMatrizPuntos.setPaletaTurno(paleta);
    }

    // ------------------------------------------------- ordenes desde el control
    public void setSeleccion(Coordenada punto, List<Coordenada> nuevosCandidatos) {
        limpiarSeleccion();
        this.seleccion = punto;
        this.candidatos.addAll(nuevosCandidatos == null ? List.of() : nuevosCandidatos);

        pnlMatrizPuntos.setEstado(punto, PnlPunto.Estado.SELECCIONADO);
        for (Coordenada c : candidatos) {
            pnlMatrizPuntos.setEstado(c, PnlPunto.Estado.CANDIDATO);
        }
    }

    public void limpiarSeleccion() {
        pnlMatrizLineas.ocultarPreview();
        if (seleccion != null) {
            pnlMatrizPuntos.setEstado(seleccion, PnlPunto.Estado.NORMAL);
        }
        for (Coordenada c : candidatos) {
            pnlMatrizPuntos.setEstado(c, PnlPunto.Estado.NORMAL);
        }
        candidatos.clear();
        seleccion = null;
    }

    public void trazarLinea(Coordenada a, Coordenada b, PaletaColor paleta) {
        pnlMatrizLineas.trazar(a, b, paleta);
    }

    public void pintarCuadro(Coordenada esquina, PaletaColor paleta, char inicial) {
        pnlMatrizCuadros.pintar(esquina, paleta, inicial);
    }

    public void reiniciar() {
        limpiarSeleccion();
        pnlMatrizLineas.reiniciar();
        pnlMatrizCuadros.reiniciar();
        pnlMatrizPuntos.limpiarEstados();
    }

    // ------------------------------------------------------ eventos de los puntos
    @Override
    public void puntoEntro(Coordenada coordenada) {
        if (seleccion != null && candidatos.contains(coordenada) && paletaTurno != null) {
            pnlMatrizLineas.mostrarPreview(seleccion, coordenada, paletaTurno);
        }
    }

    @Override
    public void puntoSalio(Coordenada coordenada) {
        pnlMatrizLineas.ocultarPreview();
    }

    @Override
    public void puntoPresionado(Coordenada coordenada) {
        if (control != null) {
            control.puntoPresionado(coordenada);
        }
    }

    /** Lista de vecinos existentes en la matriz. Utilidad geometrica, no reglas. */
    public List<Coordenada> vecinosDe(Coordenada c) {
        List<Coordenada> lista = new ArrayList<>(4);
        if (c.fila() > 0) {
            lista.add(new Coordenada(c.fila() - 1, c.columna()));
        }
        if (c.fila() < filas - 1) {
            lista.add(new Coordenada(c.fila() + 1, c.columna()));
        }
        if (c.columna() > 0) {
            lista.add(new Coordenada(c.fila(), c.columna() - 1));
        }
        if (c.columna() < columnas - 1) {
            lista.add(new Coordenada(c.fila(), c.columna() + 1));
        }
        return lista;
    }
}