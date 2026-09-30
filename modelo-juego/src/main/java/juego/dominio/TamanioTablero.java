package juego.dominio;

public enum TamanioTablero {
    DOS_JUGADORES(2, 10),
    TRES_JUGADORES(3, 20),
    CUATRO_JUGADORES(4, 30);

    private final int jugadores;
    private final int puntosPorLado;

    TamanioTablero(int jugadores, int puntosPorLado) {
        this.jugadores = jugadores;
        this.puntosPorLado = puntosPorLado;
    }

    public int getJugadores() {
        return jugadores;
    }

    public int getPuntosPorLado() {
        return puntosPorLado;
    }

    public int getCuadrosTotales() {
        return (puntosPorLado - 1) * (puntosPorLado - 1);
    }

    public static TamanioTablero paraJugadores(int jugadores) {
        for (TamanioTablero t : values()) {
            if (t.jugadores == jugadores) {
                return t;
            }
        }
        throw new IllegalArgumentException("Solo se admiten de 2 a 4 jugadores, llegaron " + jugadores);
    }
}
