package vista.componentes.tablero;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import modelo.Coordenada;
import modelo.PaletaColor;
import vista.util.Geometria;
import vista.util.Styles;

public class PnlMatrizCuadros extends JPanel {

    private final Map<Coordenada, PnlCuadro> cuadros = new LinkedHashMap<>();

    public PnlMatrizCuadros(int filas, int columnas) {
        setLayout(null);
        setOpaque(false);
        construirCuadros(filas, columnas);
    }

    private void construirCuadros(int filas, int columnas) {
        int inset = Styles.INSET_CUADRO;
        int lado = Styles.ESPACIADO_PUNTOS - inset * 2;

        for (int f = 0; f < filas - 1; f++) {
            for (int c = 0; c < columnas - 1; c++) {
                Coordenada esquina = new Coordenada(f, c);
                PnlCuadro cuadro = new PnlCuadro(esquina);
                cuadro.setBounds(Geometria.px(c) + inset, Geometria.py(f) + inset, lado, lado);
                cuadros.put(esquina, cuadro);
                add(cuadro);
            }
        }
    }

    public void pintar(Coordenada esquina, PaletaColor paleta, char inicial) {
        PnlCuadro cuadro = cuadros.get(esquina);
        if (cuadro != null) {
            cuadro.pintar(paleta, inicial);
        }
    }

    public void reiniciar() {
        cuadros.values().forEach(PnlCuadro::reiniciar);
    }
}