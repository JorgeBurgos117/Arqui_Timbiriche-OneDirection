package juego.modelo;

import java.util.ArrayList;
import java.util.List;
import juego.dominio.Accion;
import juego.dominio.Arista;
import juego.dominio.Coordenada;
import juego.dominio.MotivoFin;

public class ControlJuego implements IControlJuego {

    private final DatosJuego datos;
    private final ColaAcciones cola;

    public ControlJuego(DatosJuego datos, ColaAcciones cola) {
        this.datos = datos;
        this.cola = cola;
    }

    @Override
    public synchronized void validar(Coordenada punto, int idJugador) {
        if (!puedeJugar(idJugador) || !datos.dentro(punto)) {
            return;
        }
        Coordenada seleccion = datos.getSeleccion();
        Accion accion;

        if (seleccion != null && punto.equals(seleccion)) {
            accion = new Accion.CancelarSeleccion(idJugador);
        } else if (seleccion != null && datos.candidatosDe(seleccion).contains(punto)) {
            accion = new Accion.TrazarLinea(idJugador, seleccion, punto);
        } else if (!datos.candidatosDe(punto).isEmpty()) {
            accion = new Accion.SeleccionarPunto(idJugador, punto);
        } else if (seleccion != null) {
            accion = new Accion.CancelarSeleccion(idJugador);
        } else {
            return;
        }
        ejecutar(accion);
    }

    @Override
    public synchronized void cancelarSeleccion(int idJugador) {
        if (puedeJugar(idJugador) && datos.getSeleccion() != null) {
            ejecutar(new Accion.CancelarSeleccion(idJugador));
        }
    }

    @Override
    public synchronized void abandonar(int idJugador) {
        if (datos.enCurso() && datos.esActivo(idJugador)) {
            ejecutar(new Accion.Abandonar(idJugador));
        }
    }

    @Override
    public synchronized void proponerFin(int idJugador) {
        if (datos.enCurso() && datos.esActivo(idJugador) && !datos.hayPropuestaFin()
                && datos.idsActivosEnOrden().size() > 1) {
            ejecutar(new Accion.ProponerFin(idJugador));
        }
    }

    @Override
    public synchronized void votarFin(int idJugador, boolean acepta) {
        if (datos.enCurso() && datos.estaPendienteDeVotar(idJugador)) {
            ejecutar(new Accion.VotarFin(idJugador, acepta));
        }
    }

    @Override
    public synchronized void recibir(Accion accion) {
        aplicar(accion);
    }

    private boolean puedeJugar(int idJugador) {
        return datos.enCurso() && !datos.hayPropuestaFin() && datos.esTurnoDe(idJugador);
    }

    private void ejecutar(Accion accion) {
        aplicar(accion);
        cola.add(accion);
    }

    private void aplicar(Accion accion) {
        switch (accion) {
            case Accion.SeleccionarPunto s -> datos.setSeleccion(s.punto());
            case Accion.CancelarSeleccion c -> datos.setSeleccion(null);
            case Accion.TrazarLinea t -> trazarLinea(t);
            case Accion.Abandonar a -> abandonarPartida(a.idJugador());
            case Accion.ProponerFin p -> iniciarPropuesta(p.idJugador());
            case Accion.VotarFin v -> registrarVoto(v);
        }
        datos.notificar();
    }

    private void trazarLinea(Accion.TrazarLinea t) {
        Arista arista = new Arista(t.a(), t.b());
        if (datos.existeLinea(arista)) {
            return;
        }
        datos.agregarLinea(arista, t.idJugador());
        datos.setSeleccion(null);

        int cerrados = 0;
        for (Coordenada esquina : cuadrosVecinos(arista)) {
            if (datos.esCuadroValido(esquina) && !datos.cuadroAsignado(esquina) && datos.cuadroCerrado(esquina)) {
                datos.asignarCuadro(esquina, t.idJugador());
                cerrados++;
            }
        }

        if (datos.tableroLleno()) {
            datos.terminar(MotivoFin.TABLERO_LLENO, calcularGanador());
        } else if (cerrados == 0) {
            datos.avanzarTurno();
        }
    }

    private List<Coordenada> cuadrosVecinos(Arista arista) {
        int f = arista.a().fila();
        int c = arista.a().columna();
        List<Coordenada> lista = new ArrayList<>(2);
        if (arista.esHorizontal()) {
            lista.add(new Coordenada(f - 1, c));
            lista.add(new Coordenada(f, c));
        } else {
            lista.add(new Coordenada(f, c - 1));
            lista.add(new Coordenada(f, c));
        }
        return lista;
    }

    private void abandonarPartida(int idJugador) {
        boolean estabaEnTurno = datos.esTurnoDe(idJugador);
        Integer proponente = datos.getIdProponenteFin();

        datos.desactivar(idJugador);
        if (estabaEnTurno) {
            datos.setSeleccion(null);
        }
        if (proponente != null && proponente == idJugador) {
            datos.descartarPropuestaFin();
        }

        List<Integer> activos = datos.idsActivosEnOrden();
        if (activos.size() == 1) {
            datos.terminar(MotivoFin.ABANDONO, activos.get(0));
            return;
        }
        if (estabaEnTurno) {
            datos.avanzarTurno();
        }
        if (datos.hayPropuestaFin() && datos.sinVotosPendientes()) {
            datos.terminar(MotivoFin.ACUERDO, calcularGanador());
        }
    }

    private void iniciarPropuesta(int idProponente) {
        List<Integer> pendientes = new ArrayList<>(datos.idsActivosEnOrden());
        pendientes.remove(Integer.valueOf(idProponente));
        datos.setSeleccion(null);
        datos.iniciarPropuestaFin(idProponente, pendientes);
        if (pendientes.isEmpty()) {
            datos.terminar(MotivoFin.ACUERDO, calcularGanador());
        }
    }

    private void registrarVoto(Accion.VotarFin voto) {
        if (!voto.acepta()) {
            datos.rechazarPropuestaFin();
            return;
        }
        datos.registrarVotoAFavor(voto.idJugador());
        if (datos.sinVotosPendientes()) {
            datos.terminar(MotivoFin.ACUERDO, calcularGanador());
        }
    }

    private Integer calcularGanador() {
        Integer ganador = null;
        int mejor = -1;
        boolean empate = false;
        for (int id : datos.idsActivosEnOrden()) {
            int puntos = datos.puntosDe(id);
            if (puntos > mejor) {
                mejor = puntos;
                ganador = id;
                empate = false;
            } else if (puntos == mejor) {
                empate = true;
            }
        }
        return empate ? null : ganador;
    }
}
