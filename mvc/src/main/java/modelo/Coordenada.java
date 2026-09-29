package modelo;

public record Coordenada(int fila, int columna) {

    public boolean esAdyacenteA(Coordenada otra) {
        int df = Math.abs(fila - otra.fila);
        int dc = Math.abs(columna - otra.columna);
        return df + dc == 1;
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}