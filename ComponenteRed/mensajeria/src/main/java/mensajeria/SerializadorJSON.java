package mensajeria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import mensajeria.api.IMensajeAdmon;
import mensajeria.api.ISerializador;

public class SerializadorJSON implements ISerializador {
    @Override
    public String serializar(IMensajeAdmon m) {
        Map<String, String> campos = new LinkedHashMap<>();
        campos.put("remitente", m.getRemitente());
        campos.put("destinatario", m.getDestinatario());
        campos.put("contenido", m.getContenido());
        campos.put("fechaHora", m.getFechaHora() == null ? null : m.getFechaHora().toString());
        campos.put("ipOrigen", m.getIpOrigen());
        campos.put("puertoOrigen", String.valueOf(m.getPuertoOrigen()));
        campos.put("ipDestino", m.getIpDestino());
        campos.put("puertoDestino", String.valueOf(m.getPuertoDestino()));
        StringBuilder json = new StringBuilder("{");
        boolean primero = true;
        for (Map.Entry<String, String> campo : campos.entrySet()) {
            if (!primero) json.append(',');
            primero = false;
            json.append('"').append(campo.getKey()).append('"').append(':');
            String valor = campo.getValue();
            if (valor == null) json.append("null");
            else json.append('"').append(escapar(valor)).append('"');
        }
        return json.append('}').toString();
    }

    @Override
    public IMensajeAdmon deserializar(String texto) {
        Mensaje m = new Mensaje();
        m.setRemitente(valor(texto, "remitente"));
        m.setDestinatario(valor(texto, "destinatario"));
        m.setContenido(valor(texto, "contenido"));
        String fecha = valor(texto, "fechaHora");
        if (fecha != null && !fecha.isBlank()) m.setFechaHora(LocalDateTime.parse(fecha));
        m.setIpOrigen(valor(texto, "ipOrigen"));
        m.setPuertoOrigen(entero(texto, "puertoOrigen"));
        m.setIpDestino(valor(texto, "ipDestino"));
        m.setPuertoDestino(entero(texto, "puertoDestino"));
        return m;
    }

    private static String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String valor(String json, String clave) {
        String busca = "\"" + clave + "\"";
        int inicioClave = json.indexOf(busca);
        if (inicioClave < 0) return null;
        int inicio = json.indexOf(':', inicioClave + busca.length());
        if (inicio < 0) return null;
        inicio++;
        while (inicio < json.length() && Character.isWhitespace(json.charAt(inicio))) inicio++;
        if (inicio >= json.length() || json.charAt(inicio) != '"') return null;
        StringBuilder valor = new StringBuilder();
        boolean escape = false;
        for (int i = inicio + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> valor.append('\n');
                    case 'r' -> valor.append('\r');
                    case 't' -> valor.append('\t');
                    default -> valor.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                return valor.toString();
            } else {
                valor.append(c);
            }
        }
        return null;
    }

    private static int entero(String json, String clave) {
        String valor = valorNumerico(json, clave);
        try {
            return valor == null ? 0 : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String valorNumerico(String json, String clave) {
        String busca = "\"" + clave + "\"";
        int inicioClave = json.indexOf(busca);
        if (inicioClave < 0) return null;
        int inicio = json.indexOf(':', inicioClave + busca.length());
        if (inicio < 0) return null;
        inicio++;
        while (inicio < json.length() && Character.isWhitespace(json.charAt(inicio))) inicio++;
        int fin = inicio;
        while (fin < json.length() && (Character.isDigit(json.charAt(fin)) || json.charAt(fin) == '-')) fin++;
        return fin == inicio ? null : json.substring(inicio, fin);
    }
}
