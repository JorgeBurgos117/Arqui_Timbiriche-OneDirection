package modelo;

public record Arista(Coordenada a, Coordenada b) {

    public Arista {
        if (esMayor(a, b)) {
            Coordenada tmp = a;
            a = b;
            b = tmp;
        }
    }

    private static boolean esMayor(Coordenada x, Coordenada y) {
        if (x.fila() != y.fila()) {
            return x.fila() > y.fila();
        }
        return x.columna() > y.columna();
    }

    public boolean esHorizontal() {
        return a.fila() == b.fila();
    }

    @Override
    public String toString() {
        return a + "-" + b;
    }
}