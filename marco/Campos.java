package marco;

import java.util.*;
import java.util.regex.*;

/**
 * Utilidades para calificar salidas con formato "Etiqueta: valor", donde el
 * valor correcto es distinto para cada estudiante.
 */
public final class Campos {

    private Campos() {}

    /**
     * Extrae el valor que sigue a una etiqueta. Tolera tildes, mayusculas y
     * espacios: "Carne:", "CARNÉ :" y "carné:" se consideran la misma etiqueta.
     * Devuelve null si la etiqueta no aparece.
     */
    public static String valor(String salida, String etiqueta) {
        String objetivo = Verificar.normalizar(etiqueta).replace(":", "");
        for (String linea : salida.split("\\R")) {
            int i = linea.indexOf(':');
            if (i < 0) continue;
            String izq = Verificar.normalizar(linea.substring(0, i));
            if (izq.equals(objetivo)) {
                String v = linea.substring(i + 1).trim();
                return v.isEmpty() ? null : v;
            }
        }
        return null;
    }

    /** Exige que la etiqueta exista y traiga un valor no vacio. */
    public static String exigir(String salida, String etiqueta) {
        String v = valor(salida, etiqueta);
        if (v == null)
            throw new AssertionError("No se encontro la linea \"" + etiqueta
                    + ": <valor>\" en la salida. Respete la etiqueta y los dos puntos.");
        return v;
    }

    /**
     * Detecta que el estudiante dejo el texto de la plantilla sin reemplazar.
     * Sin esta comprobacion, entregar la plantilla intacta obtendria el puntaje
     * completo en un laboratorio de datos personales.
     */
    public static void noEsMarcador(String valor, String etiqueta) {
        String v = Verificar.normalizar(valor);
        String[] marcadores = {"completar", "escriba aqui", "su nombre", "nombre completo",
                               "tu nombre", "xxx", "xxxx", "...", "---", "n/a", "na",
                               "carne", "correo", "carrera", "ejemplo", "sin definir",
                               "juan perez", "nombre apellido", "aqui"};
        for (String m : marcadores) {
            if (v.equals(m) || v.startsWith(m + " ") || v.contains("completar")) {
                throw new AssertionError("El campo \"" + etiqueta + "\" conserva el texto de "
                        + "la plantilla (\"" + valor + "\"). Debe sustituirlo por su dato real.");
            }
        }
        if (v.length() < 2)
            throw new AssertionError("El campo \"" + etiqueta + "\" esta practicamente vacio.");
    }

    public static void cumplePatron(String valor, String regex, String etiqueta, String ejemplo) {
        if (!Pattern.matches(regex, valor.trim()))
            throw new AssertionError("El campo \"" + etiqueta + "\" no tiene el formato "
                    + "institucional. Se obtuvo \"" + valor + "\" y se esperaba algo como "
                    + ejemplo + ".");
    }

    /**
     * Compara el nombre impreso con el de la lista de clase sin exigir
     * coincidencia literal: acepta orden distinto y nombres incompletos,
     * siempre que compartan al menos dos palabras significativas.
     */
    public static void nombreCoincide(String impreso, String oficial) {
        Set<String> a = palabras(impreso), b = palabras(oficial);
        a.retainAll(b);
        if (a.size() < 2)
            throw new AssertionError("El nombre impreso (\"" + impreso + "\") no corresponde "
                    + "al estudiante inscrito en esta actividad. Verifique que esta entregando "
                    + "su propio archivo.");
    }

    private static Set<String> palabras(String s) {
        Set<String> r = new HashSet<>();
        for (String w : Verificar.normalizar(s).split("[^a-z0-9]+"))
            if (w.length() > 2) r.add(w);
        return r;
    }
}
