package controlador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.Arista;
import modelo.Coordenada;
import modelo.DatosJugador;
import vista.IVistaJuego;

/**
 * Control provisional con la logica minima de timbiriche, para poder probar la
 * vista. Imprime el estado en consola en cada jugada; cuando exista el modelo
 * real esta clase se reemplaza sin tocar la vista.
 */
public class ControlDemoConsola implements IControlJuego {

    private final IVistaJuego vista;
    private final List<DatosJugador> jugadores;
    private final int filas;
    private final int columnas;

    private final Set<Arista> lineas = new HashSet<>();
    private final Map<Coordenada, Integer> cuadros = new LinkedHashMap<>();
    private final Map<Integer, Integer> puntos = new LinkedHashMap<>();

    private int indiceTurno = 0;
    private Coordenada seleccion;

    public ControlDemoConsola(IVistaJuego vista, List<DatosJugador> jugadores, int filas, int columnas) {
        this.vista = vista;
        this.jugadores = jugadores;
        this.filas = filas;
        this.columnas = columnas;
        jugadores.forEach(j -> puntos.put(j.id(), 0));
    }

    public void iniciar() {
        jugadores.forEach(j -> vista.setPuntos(j.id(), 0));
        vista.setTurno(jugadorEnTurno().id());
        imprimirTablero();
    }

    private DatosJugador jugadorEnTurno() {
        return jugadores.get(indiceTurno);
    }

    // ------------------------------------------------------------ IControlJuego
    @Override
    public void puntoPresionado(Coordenada coordenada) {
        if (seleccion == null) {
            seleccionar(coordenada);
            return;
        }
        if (coordenada.equals(seleccion)) {
            System.out.println("Seleccion cancelada en " + coordenada);
            cancelarSeleccion();
            return;
        }
        if (!candidatosDe(seleccion).contains(coordenada)) {
            seleccionar(coordenada);
            return;
        }
        aplicarJugada(seleccion, coordenada);
    }

    @Override
    public void seleccionCancelada() {
        if (seleccion != null) {
            System.out.println("Seleccion cancelada con ESC");
            cancelarSeleccion();
        }
    }

    @Override
    public void opcionSeleccionada(String nombreOpcion) {
        System.out.println("[CONTROL] opcion recibida: " + nombreOpcion);
    }

    // ----------------------------------------------------------------- interna
    private void seleccionar(Coordenada coordenada) {
        List<Coordenada> candidatos = candidatosDe(coordenada);
        if (candidatos.isEmpty()) {
            System.out.println("El punto " + coordenada + " no tiene jugadas disponibles");
            cancelarSeleccion();
            return;
        }
        seleccion = coordenada;
        vista.setSeleccion(coordenada, candidatos);
        System.out.println(jugadorEnTurno().nombre() + " selecciono " + coordenada + " -> " + candidatos);
    }

    private void cancelarSeleccion() {
        seleccion = null;
        vista.limpiarSeleccion();
    }

    private void aplicarJugada(Coordenada a, Coordenada b) {
        DatosJugador jugador = jugadorEnTurno();
        lineas.add(new Arista(a, b));
        vista.limpiarSeleccion();
        seleccion = null;
        vista.trazarLinea(a, b, jugador.id());
        System.out.println(jugador.nombre() + " trazo " + a + "-" + b);

        int cerrados = revisarCuadros(a, b, jugador);
        if (cerrados == 0) {
            indiceTurno = (indiceTurno + 1) % jugadores.size();
            vista.setTurno(jugadorEnTurno().id());
        } else {
            System.out.println(jugador.nombre() + " cerro " + cerrados + " cuadro(s) y repite turno");
        }
        imprimirTablero();
    }

    private int revisarCuadros(Coordenada a, Coordenada b, DatosJugador jugador) {
        List<Coordenada> posibles = new ArrayList<>(2);
        boolean horizontal = a.fila() == b.fila();
        int f = Math.min(a.fila(), b.fila());
        int c = Math.min(a.columna(), b.columna());

        if (horizontal) {
            posibles.add(new Coordenada(f - 1, c));
            posibles.add(new Coordenada(f, c));
        } else {
            posibles.add(new Coordenada(f, c - 1));
            posibles.add(new Coordenada(f, c));
        }

        int cerrados = 0;
        for (Coordenada esquina : posibles) {
            if (!esCuadroValido(esquina) || cuadros.containsKey(esquina)) {
                continue;
            }
            if (estaCerrado(esquina)) {
                cuadros.put(esquina, jugador.id());
                puntos.merge(jugador.id(), 1, Integer::sum);
                vista.pintarCuadro(esquina, jugador.id());
                vista.setPuntos(jugador.id(), puntos.get(jugador.id()));
                cerrados++;
            }
        }
        return cerrados;
    }

    private boolean esCuadroValido(Coordenada esquina) {
        return esquina.fila() >= 0 && esquina.fila() < filas - 1
                && esquina.columna() >= 0 && esquina.columna() < columnas - 1;
    }

    private boolean estaCerrado(Coordenada esquina) {
        int f = esquina.fila();
        int c = esquina.columna();
        Coordenada supIzq = new Coordenada(f, c);
        Coordenada supDer = new Coordenada(f, c + 1);
        Coordenada infIzq = new Coordenada(f + 1, c);
        Coordenada infDer = new Coordenada(f + 1, c + 1);

        return lineas.contains(new Arista(supIzq, supDer))
                && lineas.contains(new Arista(infIzq, infDer))
                && lineas.contains(new Arista(supIzq, infIzq))
                && lineas.contains(new Arista(supDer, infDer));
    }

    private List<Coordenada> candidatosDe(Coordenada origen) {
        List<Coordenada> lista = new ArrayList<>(4);
        agregarSiValido(lista, origen, new Coordenada(origen.fila() - 1, origen.columna()));
        agregarSiValido(lista, origen, new Coordenada(origen.fila() + 1, origen.columna()));
        agregarSiValido(lista, origen, new Coordenada(origen.fila(), origen.columna() - 1));
        agregarSiValido(lista, origen, new Coordenada(origen.fila(), origen.columna() + 1));
        return lista;
    }

    private void agregarSiValido(List<Coordenada> lista, Coordenada origen, Coordenada destino) {
        boolean dentro = destino.fila() >= 0 && destino.fila() < filas
                && destino.columna() >= 0 && destino.columna() < columnas;
        if (dentro && !lineas.contains(new Arista(origen, destino))) {
            lista.add(destino);
        }
    }

    /** Render de texto: el juego es funcional aunque no hubiera GUI. */
    private void imprimirTablero() {
        StringBuilder sb = new StringBuilder("\n");
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                sb.append('*');
                if (c < columnas - 1) {
                    boolean hay = lineas.contains(new Arista(new Coordenada(f, c), new Coordenada(f, c + 1)));
                    sb.append(hay ? "---" : "   ");
                }
            }
            sb.append('\n');
            if (f < filas - 1) {
                for (int c = 0; c < columnas; c++) {
                    boolean hay = lineas.contains(new Arista(new Coordenada(f, c), new Coordenada(f + 1, c)));
                    sb.append(hay ? '|' : ' ');
                    if (c < columnas - 1) {
                        Integer duenio = cuadros.get(new Coordenada(f, c));
                        sb.append(duenio == null ? "   " : " " + inicialDe(duenio) + " ");
                    }
                }
                sb.append('\n');
            }
        }
        jugadores.forEach(j -> sb.append(j.nombre()).append(": ").append(puntos.get(j.id())).append("   "));
        System.out.println(sb);
    }

    private char inicialDe(int idJugador) {
        return jugadores.stream()
                .filter(j -> j.id() == idJugador)
                .findFirst()
                .map(DatosJugador::inicial)
                .orElse('?');
    }
}