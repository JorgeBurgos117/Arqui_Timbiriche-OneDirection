package vista.componentes.tablero;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import modelo.Arista;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Geometria;
import vista.util.Styles;

public class PnlMatrizLineas extends JPanel {

    private final Map<Arista, PnlLinea> lineas = new LinkedHashMap<>();
    private PnlLinea lineaEnPreview;

    public PnlMatrizLineas(int filas, int columnas) {
        setLayout(null);
        setOpaque(false);
        construirLineas(filas, columnas);
    }

    private void construirLineas(int filas, int columnas) {
        int grosorArea = Styles.GROSOR_LINEA + 6;
        int espaciado = Styles.ESPACIADO_PUNTOS;

        // Horizontales
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas - 1; c++) {
                Arista arista = new Arista(new Coordenada(f, c), new Coordenada(f, c + 1));
                PnlLinea linea = new PnlLinea(arista);
                linea.setBounds(Geometria.px(c), Geometria.py(f) - grosorArea / 2, espaciado, grosorArea);
                lineas.put(arista, linea);
                add(linea);
            }
        }

        // Verticales
        for (int f = 0; f < filas - 1; f++) {
            for (int c = 0; c < columnas; c++) {
                Arista arista = new Arista(new Coordenada(f, c), new Coordenada(f + 1, c));
                PnlLinea linea = new PnlLinea(arista);
                linea.setBounds(Geometria.px(c) - grosorArea / 2, Geometria.py(f), grosorArea, espaciado);
                lineas.put(arista, linea);
                add(linea);
            }
        }
    }

    public PnlLinea getLinea(Coordenada a, Coordenada b) {
        return lineas.get(new Arista(a, b));
    }

    public void mostrarPreview(Coordenada a, Coordenada b, PaletaColor paleta) {
        ocultarPreview();
        PnlLinea linea = getLinea(a, b);
        if (linea != null) {
            linea.mostrarPreview(paleta);
            lineaEnPreview = linea;
        }
    }

    public void ocultarPreview() {
        if (lineaEnPreview != null) {
            lineaEnPreview.ocultar();
            lineaEnPreview = null;
        }
    }

    public void trazar(Coordenada a, Coordenada b, PaletaColor paleta) {
        PnlLinea linea = getLinea(a, b);
        if (linea != null) {
            if (linea == lineaEnPreview) {
                lineaEnPreview = null;
            }
            linea.trazar(paleta);
        }
    }

    public void reiniciar() {
        lineaEnPreview = null;
        lineas.values().forEach(PnlLinea::reiniciar);
    }
}