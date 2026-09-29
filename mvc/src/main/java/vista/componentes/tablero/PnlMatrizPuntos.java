package vista.componentes.tablero;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Geometria;
import vista.util.Styles;


public class PnlMatrizPuntos extends JPanel {

    private final Map<Coordenada, PnlPunto> puntos = new LinkedHashMap<>();
    private final int filas;
    private final int columnas;

    public PnlMatrizPuntos(int filas, int columnas, PnlPunto.EscuchaPunto escucha) {
        this.filas = filas;
        this.columnas = columnas;
        setLayout(null);
        setOpaque(false);
        construirPuntos(escucha);
    }

    private void construirPuntos(PnlPunto.EscuchaPunto escucha) {
        int tam = Styles.TAM_AREA_PUNTO;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Coordenada coord = new Coordenada(f, c);
                PnlPunto punto = new PnlPunto(coord, escucha);
                punto.setBounds(Geometria.px(c) - tam / 2, Geometria.py(f) - tam / 2, tam, tam);
                puntos.put(coord, punto);
                add(punto);
            }
        }
    }

    public PnlPunto getPunto(Coordenada coordenada) {
        return puntos.get(coordenada);
    }

    public void setPaletaTurno(PaletaColor paleta) {
        puntos.values().forEach(p -> p.setPaletaTurno(paleta));
    }

    public void setEstado(Coordenada coordenada, PnlPunto.Estado estado) {
        PnlPunto punto = puntos.get(coordenada);
        if (punto != null) {
            punto.setEstado(estado);
        }
    }

    public void limpiarEstados() {
        puntos.values().forEach(p -> p.setEstado(PnlPunto.Estado.NORMAL));
    }
}