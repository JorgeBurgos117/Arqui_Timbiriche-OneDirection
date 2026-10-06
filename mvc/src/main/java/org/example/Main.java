package org.example;

import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controlador.ControlDemoConsola;
import controlador.IControlJuego;
import modelo.DatosJugador;
import vista.componentes.FrmPrincipal;
import vista.util.Styles;

//import vista.FrmPrincipal;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            //list ade jugadores de ejemplo
            List<DatosJugador> jugadores = List.of(
                    new DatosJugador(1, "Asereje", Styles.PALETAS.get(0)),
                    new DatosJugador(2, "Ja", Styles.PALETAS.get(1)),
                    new DatosJugador(3, "Deje", Styles.PALETAS.get(2)),
                    new DatosJugador(4, "Dejebere", Styles.PALETAS.get(3))
            );

            //pov bl south
            int idLocal = 1;

            int filas = Styles.FILAS_PUNTOS_DEFAULT;
            int columnas = Styles.COLUMNAS_PUNTOS_DEFAULT;

            FrmPrincipal frm = new FrmPrincipal(jugadores, idLocal, filas, columnas);
            ControlDemoConsola control = new ControlDemoConsola(frm, jugadores, filas, columnas);
            frm.setControl((IControlJuego) control);
            frm.mostrar();
            control.iniciar();
        });
    }
}