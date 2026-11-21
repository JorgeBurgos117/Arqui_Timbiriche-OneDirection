package mensajeria;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

// JSON plano hecho a mano (sin librerías): solo objetos de un nivel con strings, números y null
public class SerializadorJSON implements ISerializador {

    @Override
    public String serializar(IMensajeAdmon mensaje) {
        if (mensaje == null) {
            throw new SerializacionException("No se puede serializar un mensaje nulo");
        }
        StringBuilder sb = new StringBuilder("{");
        campo(sb, "remitente", mensaje.getRemitente()).append(',');
        campo(sb, "destinatario", mensaje.getDestinatario()).append(',');
        campo(sb, "contenido", mensaje.getContenido()).append(',');
        campo(sb, "fechaHora", mensaje.getFechaHora() == null ? null : mensaje.getFechaHora().toString()).append(',');
        campo(sb, "ipOrigen", mensaje.getIpOrigen()).append(',');
        sb.append("\"puertoOrigen\":").append(mensaje.getPuertoOrigen()).append(',');
        campo(sb, "ipDestino", mensaje.getIpDestino()).append(',');
        sb.append("\"puertoDestino\":").append(mensaje.getPuertoDestino());
        return sb.append('}').toString();
    }

    @Override
    public IMensajeAdmon deserializar(String texto) {
        if (texto == null) {
            throw new SerializacionException("Texto nulo");
        }
        Map<String, String> campos = leerObjeto(texto.trim());
        Mensaje m = new Mensaje();
        m.setRemitente(campos.get("remitente"));
        m.setDestinatario(campos.get("destinatario"));
        m.setContenido(campos.get("contenido"));
        m.setIpOrigen(campos.get("ipOrigen"));
        m.setPuertoOrigen(entero(campos.get("puertoOrigen")));
        m.setIpDestino(campos.get("ipDestino"));
        m.setPuertoDestino(entero(campos.get("puertoDestino")));
        String fecha = campos.get("fechaHora");
        try {
            m.setFechaHora(fecha == null ? null : LocalDateTime.parse(fecha));
        } catch (DateTimeParseException e) {
            throw new SerializacionException("fechaHora inválida: " + fecha);
        }
        return m;
    }

    private StringBuilder campo(StringBuilder sb, String nombre, String valor) {
        cadena(sb, nombre);
        sb.append(':');
        if (valor == null) {
            sb.append("null");
        } else {
            cadena(sb, valor);
        }
        return sb;
    }

    private void cadena(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    private Map<String, String> leerObjeto(String t) {
        Map<String, String> campos = new HashMap<>();
        int[] pos = {0};
        esperar(t, pos, '{');
        saltarEspacios(t, pos);
        if (pos[0] < t.length() && t.charAt(pos[0]) == '}') {
            pos[0]++;
            return campos;
        }
        while (true) {
            saltarEspacios(t, pos);
            String nombre = leerCadena(t, pos);
            saltarEspacios(t, pos);
            esperar(t, pos, ':');
            saltarEspacios(t, pos);
            campos.put(nombre, leerValor(t, pos));
            saltarEspacios(t, pos);
            if (pos[0] >= t.length()) {
                throw new SerializacionException("JSON incompleto");
            }
            char c = t.charAt(pos[0]++);
            if (c == '}') {
                return campos;
            }
            if (c != ',') {
                throw new SerializacionException("Se esperaba ',' o '}' en la posición " + (pos[0] - 1));
            }
        }
    }

    private String leerValor(String t, int[] pos) {
        if (pos[0] >= t.length()) {
            throw new SerializacionException("JSON incompleto");
        }
        if (t.charAt(pos[0]) == '"') {
            return leerCadena(t, pos);
        }
        int inicio = pos[0];
        while (pos[0] < t.length() && ",} \t\r\n".indexOf(t.charAt(pos[0])) < 0) {
            pos[0]++;
        }
        String crudo = t.substring(inicio, pos[0]);
        return crudo.equals("null") ? null : crudo;
    }

    private String leerCadena(String t, int[] pos) {
        esperar(t, pos, '"');
        StringBuilder sb = new StringBuilder();
        while (pos[0] < t.length()) {
            char c = t.charAt(pos[0]++);
            if (c == '"') {
                return sb.toString();
            }
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (pos[0] >= t.length()) {
                break;
            }
            char e = t.charAt(pos[0]++);
            switch (e) {
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case 'u' -> {
                    if (pos[0] + 4 > t.length()) {
                        throw new SerializacionException("Escape \\u incompleto");
                    }
                    try {
                        sb.append((char) Integer.parseInt(t.substring(pos[0], pos[0] + 4), 16));
                    } catch (NumberFormatException ex) {
                        throw new SerializacionException("Escape \\u inválido");
                    }
                    pos[0] += 4;
                }
                default -> sb.append(e);
            }
        }
        throw new SerializacionException("Cadena sin cerrar");
    }

    private void esperar(String t, int[] pos, char esperado) {
        if (pos[0] >= t.length() || t.charAt(pos[0]) != esperado) {
            throw new SerializacionException("Se esperaba '" + esperado + "' en la posición " + pos[0]);
        }
        pos[0]++;
    }

    private void saltarEspacios(String t, int[] pos) {
        while (pos[0] < t.length() && Character.isWhitespace(t.charAt(pos[0]))) {
            pos[0]++;
        }
    }

    private int entero(String valor) {
        if (valor == null) {
            return 0;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new SerializacionException("Número inválido: " + valor);
        }
    }
}
