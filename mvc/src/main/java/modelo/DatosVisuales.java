package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;
import juego.dominio.Arista;
import juego.dominio.Coordenada;
import juego.dominio.EstadoJuego;
import juego.dominio.JugadorInfo;
import juego.dominio.PropuestaFin;
import juego.modelo.IModeloJuego;
import juego.modelo.IObservadorJuego;

public class DatosVisuales implements IModeloVista, IObservadorJuego {

    private final IModeloJuego modeloJuego;
    private final int idLocal;
    private final boolean todosLocales;

    private final GeometriaTablero geometria;
    private final Map<Integer, PaletaColor> paletas = new LinkedHashMap<>();
    private final Map<Integer, Ubicacion> ubicaciones;
    private final List<IObservadorVista> observadores = new CopyOnWriteArrayList<>();
    private final List<Aviso> avisos = new ArrayList<>();

    private EstadoJuego estado;
    private Coordenada hover;

    public DatosVisuales(IModeloJuego modeloJuego, int idLocal, boolean todosLocales, int ladoTablero) {
        this.modeloJuego = modeloJuego;
        this.idLocal = idLocal;
        this.todosLocales = todosLocales;
        this.estado = modeloJuego.get();
        this.geometria = new GeometriaTablero(estado.filas(), estado.columnas(), ladoTablero);

        List<Integer> idsEnTurno = estado.jugadores().stream().map(JugadorInfo::id).toList();
        this.ubicaciones = Ubicacion.distribuir(idsEnTurno, idLocal);

        List<Integer> idsOrdenados = new ArrayList<>(idsEnTurno);
        Collections.sort(idsOrdenados);
        for (int i = 0; i < idsOrdenados.size(); i++) {
            paletas.put(idsOrdenados.get(i), PaletaColor.DISPONIBLES.get(i % PaletaColor.DISPONIBLES.size()));
        }
    }

    @Override
    public void update() {
        if (SwingUtilities.isEventDispatchThread()) {
            refrescar();
        } else {
            SwingUtilities.invokeLater(this::refrescar);
        }
    }

    private void refrescar() {
        EstadoJuego anterior = estado;
        estado = modeloJuego.get();
        detectarAvisos(anterior, estado);
        if (!estado.enCurso()) {
            hover = null;
        }
        notificar();
    }

    private void detectarAvisos(EstadoJuego antes, EstadoJuego ahora) {
        for (JugadorInfo j : ahora.jugadores()) {
            JugadorInfo previo = antes.jugador(j.id());
            if (previo != null && previo.activo() && !j.activo()) {
                publicarAviso(j.nombre() + " abandonó la partida.");
            }
        }
        if (ahora.propuestasRechazadas() > antes.propuestasRechazadas()) {
            publicarAviso("La propuesta para terminar la partida fue rechazada.");
        }
    }

    private void publicarAviso(String texto) {
        avisos.add(new Aviso(avisos.size() + 1, texto));
    }

    @Override
    public void agregarObservador(IObservadorVista observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(IObservadorVista observador) {
        observadores.remove(observador);
    }

    private void notificar() {
        for (IObservadorVista o : observadores) {
            o.update();
        }
    }

    Integer idActuante() {
        if (!estado.enCurso()) {
            return null;
        }
        if (todosLocales) {
            return estado.idEnTurno();
        }
        return estado.esActivo(idLocal) ? idLocal : null;
    }

    boolean esLocal(int idJugador) {
        return todosLocales || idJugador == idLocal;
    }

    void setHover(Coordenada punto) {
        if (!Objects.equals(hover, punto)) {
            hover = punto;
            notificar();
        }
    }

    void setPaletas(Map<Integer, PaletaColor> nuevas) {
        paletas.putAll(nuevas);
        notificar();
    }

    @Override
    public GeometriaTablero getGeometria() {
        return geometria;
    }

    @Override
    public List<JugadorVisual> getJugadores() {
        List<JugadorVisual> lista = new ArrayList<>();
        for (JugadorInfo j : estado.jugadores()) {
            if (j.activo()) {
                lista.add(aVisual(j));
            }
        }
        return lista;
    }

    @Override
    public JugadorVisual getJugadorEnTurno() {
        Integer id = estado.idEnTurno();
        return id == null ? null : aVisual(estado.jugador(id));
    }

    private JugadorVisual aVisual(JugadorInfo j) {
        return new JugadorVisual(j.id(), j.nombre(), inicialDe(j.nombre()), paletas.get(j.id()),
                j.puntos(), ubicaciones.get(j.id()), Objects.equals(estado.idEnTurno(), j.id()));
    }

    private static char inicialDe(String nombre) {
        return nombre == null || nombre.isBlank() ? '?' : Character.toUpperCase(nombre.charAt(0));
    }

    @Override
    public List<LineaVisual> getLineas() {
        List<LineaVisual> lista = new ArrayList<>();
        estado.lineas().forEach((arista, id) ->
                lista.add(new LineaVisual(arista, estado.esActivo(id) ? paletas.get(id) : null)));
        return lista;
    }

    @Override
    public List<CuadroVisual> getCuadros() {
        List<CuadroVisual> lista = new ArrayList<>();
        estado.cuadros().forEach((esquina, id) -> {
            JugadorInfo j = estado.jugador(id);
            if (j != null && j.activo()) {
                lista.add(new CuadroVisual(esquina, paletas.get(id), inicialDe(j.nombre())));
            }
        });
        return lista;
    }

    @Override
    public Coordenada getSeleccion() {
        return estado.seleccion();
    }

    @Override
    public Set<Coordenada> getCandidatos() {
        return new HashSet<>(estado.candidatos());
    }

    @Override
    public Coordenada getHover() {
        return hover;
    }

    @Override
    public Arista getLineaPreview() {
        Coordenada seleccion = estado.seleccion();
        if (seleccion == null || hover == null || !estado.candidatos().contains(hover)) {
            return null;
        }
        return new Arista(seleccion, hover);
    }

    @Override
    public PaletaColor getPaletaTurno() {
        Integer enTurno = estado.idEnTurno();
        if (enTurno == null || estado.propuestaFin() != null || !esLocal(enTurno)) {
            return null;
        }
        return paletas.get(enTurno);
    }

    @Override
    public List<PaletaColor> getPaletasDisponibles() {
        return PaletaColor.DISPONIBLES;
    }

    @Override
    public VotoPendiente getVotoPendiente() {
        PropuestaFin propuesta = estado.propuestaFin();
        if (propuesta == null || !estado.enCurso()) {
            return null;
        }
        String proponente = estado.jugador(propuesta.idProponente()).nombre();
        for (int id : propuesta.pendientes()) {
            if (esLocal(id)) {
                return new VotoPendiente(id, estado.jugador(id).nombre(), proponente);
            }
        }
        return null;
    }

    @Override
    public List<Aviso> getAvisos() {
        return List.copyOf(avisos);
    }

    @Override
    public boolean isPartidaTerminada() {
        return !estado.enCurso();
    }

    @Override
    public String getTextoFin() {
        if (estado.enCurso()) {
            return "";
        }
        String motivo = switch (estado.motivoFin()) {
            case TABLERO_LLENO -> "Se completaron todos los cuadros.";
            case ABANDONO -> "Los demás jugadores abandonaron la partida.";
            case ACUERDO -> "Todos acordaron terminar la partida.";
        };
        String resultado = estado.idGanador() == null
                ? "Empate."
                : "Ganador: " + estado.jugador(estado.idGanador()).nombre();
        return motivo + " " + resultado;
    }

    @Override
    public List<FilaResultado> getResultados() {
        List<JugadorInfo> activos = new ArrayList<>();
        List<JugadorInfo> retirados = new ArrayList<>();
        for (JugadorInfo j : estado.jugadores()) {
            (j.activo() ? activos : retirados).add(j);
        }
        activos.sort(Comparator.comparingInt(JugadorInfo::puntos).reversed());

        List<FilaResultado> filas = new ArrayList<>();
        int posicion = 0;
        int puntosAnteriores = -1;
        for (int i = 0; i < activos.size(); i++) {
            JugadorInfo j = activos.get(i);
            if (j.puntos() != puntosAnteriores) {
                posicion = i + 1;
                puntosAnteriores = j.puntos();
            }
            boolean ganador = Objects.equals(estado.idGanador(), j.id());
            filas.add(new FilaResultado(posicion, j.nombre(), paletas.get(j.id()), j.puntos(), false, ganador));
        }
        for (JugadorInfo j : retirados) {
            filas.add(new FilaResultado(0, j.nombre(), paletas.get(j.id()), 0, true, false));
        }
        return filas;
    }
}
