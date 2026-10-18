package marco;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

/**
 * Puente entre el corrector y las pruebas cuando la salida correcta es distinta
 * para cada estudiante (por ejemplo, el Laboratorio 1: datos personales).
 *
 * LECTURA  El corrector escribe 'contexto.properties' en la carpeta de trabajo
 *          con los datos que Moodle ya conoce del estudiante (nombre, correo) y
 *          los que aporte la lista de clase (carne, carrera). Las pruebas
 *          consultan esos valores con Contexto.get("correo").
 *
 * ESCRITURA Las pruebas registran lo que el estudiante imprimio con
 *          Contexto.registrar("carne", valor). El corrector lee ese archivo al
 *          final y detecta datos duplicados entre estudiantes.
 *
 * Si el archivo no existe (el estudiante ejecuta el verificador en su casa),
 * get() devuelve null y las pruebas de identidad se limitan a validar formato.
 */
public final class Contexto {

    private static final Properties DATOS = new Properties();
    private static final Properties REGISTRO = new Properties();
    private static boolean cargado = false;

    private Contexto() {}

    private static synchronized void cargar() {
        if (cargado) return;
        cargado = true;
        Path p = Paths.get("contexto.properties");
        if (Files.exists(p)) {
            try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                DATOS.load(r);
            } catch (IOException ignored) { }
        }
    }

    /** true cuando se ejecuta dentro del corrector con datos del estudiante. */
    public static boolean disponible() {
        cargar();
        return !DATOS.isEmpty();
    }

    public static String get(String clave) {
        cargar();
        String v = DATOS.getProperty(clave);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    /** Guarda un dato leido de la salida del estudiante para analisis posterior. */
    public static synchronized void registrar(String clave, String valor) {
        REGISTRO.setProperty(clave, valor == null ? "" : valor);
        try (Writer w = Files.newBufferedWriter(Paths.get("extraido.properties"),
                StandardCharsets.UTF_8)) {
            REGISTRO.store(w, "Datos leidos de la salida del estudiante");
        } catch (IOException ignored) { }
    }
}
