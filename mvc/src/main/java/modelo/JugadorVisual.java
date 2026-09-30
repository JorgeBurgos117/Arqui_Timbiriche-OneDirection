package modelo;

public record JugadorVisual(
        int id,
        String nombre,
        char inicial,
        PaletaColor paleta,
        int puntos,
        Ubicacion ubicacion,
        boolean enTurno) {
}
