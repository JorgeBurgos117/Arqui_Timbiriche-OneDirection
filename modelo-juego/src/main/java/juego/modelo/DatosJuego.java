package juego.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import juego.objetosNegocio.Linea;
import juego.objetosNegocio.Punto;
import juego.objetosNegocio.EstadoPartida;

public class DatosJuego implements IModeloJuego {

    private static final class Jugador {

        final int id;
        final String nombre;
        int puntos;
        boolean activo = true;

        Jugador(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        JugadorInfo info() {
            return new JugadorInfo(id, nombre, puntos, activo);
        }
    }

    private final int filas;
    private final int columnas;

    private final Map<Integer, Jugador> jugadores = new LinkedHashMap<>();
    private final List<Integer> ordenTurnos;
    private int indiceTurno = 0;

    private final Map<Linea, Integer> lineas = new LinkedHashMap<>();
    private final Map<Punto, Integer> cuadros = new LinkedHashMap<>();
    private Punto seleccion;

    private EstadoPartida estado = EstadoPartida.EN_CURSO;
    private MotivoFin motivoFin;
    private Integer idGanador;

    private Integer idProponenteFin;
    private final Set<Integer> pendientesFin = new LinkedHashSet<>();
    private int propuestasRechazadas = 0;

    private final List<IObservadorJuego> observadores = new CopyOnWriteArrayList<>();

    public DatosJuego(TamanioTablero tamanio, List<String> nombres, Random azar) {
        if (nombres.size() != tamanio.getJugadores()) {
            throw new IllegalArgumentException("El tablero " + tamanio + " es para "
                    + tamanio.getJugadores() + " jugadores, llegaron " + nombres.size());
        }
        this.filas = tamanio.getPuntosPorLado();
        this.columnas = tamanio.getPuntosPorLado();

        for (int i = 0; i < nombres.size(); i++) {
            int id = i + 1;
            jugadores.put(id, new Jugador(id, nombres.get(i)));
        }
        List<Integer> orden = new ArrayList<>(jugadores.keySet());
        Collections.shuffle(orden, azar);
        this.ordenTurnos = List.copyOf(orden);
    }

    @Override
    public synchronized EstadoJuego get() {
        List<JugadorInfo> lista = new ArrayList<>();
        for (int id : ordenTurnos) {
            lista.add(jugadores.get(id).info());
        }
        PropuestaFin propuesta = idProponenteFin == null
                ? null
                : new PropuestaFin(idProponenteFin, new ArrayList<>(pendientesFin));
        List<Punto> candidatos = seleccion == null ? List.of() : candidatosDe(seleccion);

        return new EstadoJuego(filas, columnas, lista, idEnTurno(), lineas, cuadros,
                seleccion, candidatos, estado, motivoFin, idGanador, propuesta, propuestasRechazadas);
    }

    @Override
    public void agregarObservador(IObservadorJuego observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(IObservadorJuego observador) {
        observadores.remove(observador);
    }

    void notificar() {
        for (IObservadorJuego o : observadores) {
            o.update();
        }
    }

    synchronized boolean enCurso() {
        return estado == EstadoPartida.EN_CURSO;
    }

    synchronized Integer idEnTurno() {
        return estado == EstadoPartida.EN_CURSO ? ordenTurnos.get(indiceTurno) : null;
    }

    synchronized boolean esTurnoDe(int idJugador) {
        Integer enTurno = idEnTurno();
        return enTurno != null && enTurno == idJugador;
    }

    synchronized boolean esActivo(int idJugador) {
        Jugador j = jugadores.get(idJugador);
        return j != null && j.activo;
    }

    synchronized List<Integer> idsActivosEnOrden() {
        List<Integer> activos = new ArrayList<>();
        for (int id : ordenTurnos) {
            if (jugadores.get(id).activo) {
                activos.add(id);
            }
        }
        return activos;
    }

    synchronized Punto getSeleccion() {
        return seleccion;
    }

    synchronized boolean dentro(Punto c) {
        return c.fila() >= 0 && c.fila() < filas && c.columna() >= 0 && c.columna() < columnas;
    }

    synchronized boolean existeLinea(Linea arista) {
        return lineas.containsKey(arista);
    }

    synchronized List<Punto> candidatosDe(Punto origen) {
        List<Punto> lista = new ArrayList<>(4);
        agregarSiLibre(lista, origen, new Punto(origen.fila() - 1, origen.columna()));
        agregarSiLibre(lista, origen, new Punto(origen.fila() + 1, origen.columna()));
        agregarSiLibre(lista, origen, new Punto(origen.fila(), origen.columna() - 1));
        agregarSiLibre(lista, origen, new Punto(origen.fila(), origen.columna() + 1));
        return lista;
    }

    private void agregarSiLibre(List<Punto> lista, Punto origen, Punto destino) {
        if (dentro(destino) && !lineas.containsKey(new Linea(origen, destino))) {
            lista.add(destino);
        }
    }

    synchronized boolean esCuadroValido(Punto esquina) {
        return esquina.fila() >= 0 && esquina.fila() < filas - 1
                && esquina.columna() >= 0 && esquina.columna() < columnas - 1;
    }

    synchronized boolean cuadroAsignado(Punto esquina) {
        return cuadros.containsKey(esquina);
    }

    synchronized boolean cuadroCerrado(Punto esquina) {
        int f = esquina.fila();
        int c = esquina.columna();
        Punto supIzq = new Punto(f, c);
        Punto supDer = new Punto(f, c + 1);
        Punto infIzq = new Punto(f + 1, c);
        Punto infDer = new Punto(f + 1, c + 1);
        return lineas.containsKey(new Linea(supIzq, supDer))
                && lineas.containsKey(new Linea(infIzq, infDer))
                && lineas.containsKey(new Linea(supIzq, infIzq))
                && lineas.containsKey(new Linea(supDer, infDer));
    }

    synchronized boolean tableroLleno() {
        return cuadros.size() == (filas - 1) * (columnas - 1);
    }

    synchronized int puntosDe(int idJugador) {
        return jugadores.get(idJugador).puntos;
    }

    synchronized boolean hayPropuestaFin() {
        return idProponenteFin != null;
    }

    synchronized Integer getIdProponenteFin() {
        return idProponenteFin;
    }

    synchronized boolean estaPendienteDeVotar(int idJugador) {
        return pendientesFin.contains(idJugador);
    }

    synchronized boolean sinVotosPendientes() {
        return pendientesFin.isEmpty();
    }

    synchronized void setSeleccion(Punto punto) {
        this.seleccion = punto;
    }

    synchronized void agregarLinea(Linea arista, int idJugador) {
        lineas.put(arista, idJugador);
    }

    synchronized void asignarCuadro(Punto esquina, int idJugador) {
        cuadros.put(esquina, idJugador);
        jugadores.get(idJugador).puntos++;
    }

    synchronized void avanzarTurno() {
        int n = ordenTurnos.size();
        for (int paso = 1; paso <= n; paso++) {
            int indice = (indiceTurno + paso) % n;
            if (jugadores.get(ordenTurnos.get(indice)).activo) {
                indiceTurno = indice;
                return;
            }
        }
    }

    synchronized void desactivar(int idJugador) {
        jugadores.get(idJugador).activo = false;
        pendientesFin.remove(idJugador);
    }

    synchronized void iniciarPropuestaFin(int idProponente, List<Integer> pendientes) {
        this.idProponenteFin = idProponente;
        this.pendientesFin.clear();
        this.pendientesFin.addAll(pendientes);
    }

    synchronized void registrarVotoAFavor(int idJugador) {
        pendientesFin.remove(idJugador);
    }

    synchronized void rechazarPropuestaFin() {
        descartarPropuestaFin();
        propuestasRechazadas++;
    }

    synchronized void descartarPropuestaFin() {
        idProponenteFin = null;
        pendientesFin.clear();
    }

    synchronized void terminar(MotivoFin motivo, Integer ganador) {
        this.estado = EstadoPartida.TERMINADA;
        this.motivoFin = motivo;
        this.idGanador = ganador;
        this.seleccion = null;
        descartarPropuestaFin();
    }
}
