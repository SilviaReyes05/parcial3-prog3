package marco;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Ejecutor de pruebas.
 *
 *   java -cp out marco.Juez PruebasLab03 resultados.jsonl 10000
 *
 * Escribe una linea JSON por evento y hace flush inmediato. Si la JVM muere a
 * media ejecucion (System.exit del estudiante, StackOverflow, OutOfMemory),
 * el archivo conserva todo lo que alcanzo a ejecutarse y el corrector marca
 * el resto como no ejecutado.
 */
public class Juez {

    private static PrintStream stdoutReal;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java marco.Juez <Clase1,Clase2> <salida.jsonl|-> [timeoutMs]");
            System.exit(2);
        }
        String[] nombresClase = args[0].split(",");
        boolean legible = "-".equals(args[1]);   // modo estudiante: texto en consola
        Path salida = legible ? Paths.get(System.getProperty("java.io.tmpdir"), "juez.jsonl")
                              : Paths.get(args[1]);
        long timeoutMs = args.length > 2 ? Long.parseLong(args[2]) : 10_000L;

        stdoutReal = System.out;

        try (BufferedWriter out = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {
            List<Method> metodos = new ArrayList<>();
            Map<Method, Class<?>> duenio = new LinkedHashMap<>();
            for (String nc : nombresClase) {
                Class<?> clase;
                try {
                    clase = Class.forName(nc.trim());
                } catch (Throwable t) {
                    emitir(out, "{\"tipo\":\"fatal\",\"mensaje\":" + json(descripcion(t)) + "}");
                    return;
                }
                List<Method> propios = new ArrayList<>();
                for (Method m : clase.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(Prueba.class)) propios.add(m);
                }
                propios.sort(Comparator.comparing(Method::getName));
                for (Method m : propios) duenio.put(m, clase);
                metodos.addAll(propios);
            }

            // Manifiesto: el corrector conoce el conjunto completo de pruebas
            // aunque la JVM muera despues de la primera.
            StringBuilder man = new StringBuilder("{\"tipo\":\"manifiesto\",\"pruebas\":[");
            for (int i = 0; i < metodos.size(); i++) {
                Prueba p = metodos.get(i).getAnnotation(Prueba.class);
                if (i > 0) man.append(',');
                man.append("{\"id\":").append(json(metodos.get(i).getName()))
                   .append(",\"nombre\":").append(json(p.value()))
                   .append(",\"puntos\":").append(p.puntos())
                   .append(",\"oculta\":").append(p.oculta())
                   .append('}');
            }
            man.append("]}");
            emitir(out, man.toString());

            double obtenidos = 0, totales = 0;
            for (Method m : metodos) {
                double[] r = ejecutarUna(out, duenio.get(m), m, timeoutMs, legible);
                obtenidos += r[0];
                totales += r[1];
            }
            emitir(out, "{\"tipo\":\"fin\"}");

            if (legible) {
                stdoutReal.println("-".repeat(70));
                stdoutReal.printf("Puntaje: %.0f de %.0f  (%.0f%%)%n",
                        obtenidos, totales, totales == 0 ? 0 : obtenidos / totales * 100);
                if (obtenidos < totales) {
                    stdoutReal.println("Aun hay criterios sin cumplir. Corrija y vuelva a ejecutar.");
                } else {
                    stdoutReal.println("Todas las pruebas publicas pasan. Ya puede entregar en Moodle.");
                    stdoutReal.println("Recuerde que la calificacion incluye ademas casos ocultos.");
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir resultados: " + e.getMessage());
            System.exit(3);
        }
    }

    private static double[] ejecutarUna(BufferedWriter out, Class<?> clase, Method m,
                                        long timeoutMs, boolean legible) throws IOException {
        Prueba p = m.getAnnotation(Prueba.class);
        final Throwable[] fallo = new Throwable[1];

        Runnable cuerpo = () -> {
            try {
                Object inst = Modifier.isStatic(m.getModifiers())
                        ? null : clase.getDeclaredConstructor().newInstance();
                m.setAccessible(true);
                m.invoke(inst);
            } catch (InvocationTargetException e) {
                fallo[0] = e.getCause() != null ? e.getCause() : e;
            } catch (Throwable t) {
                fallo[0] = t;
            }
        };

        Thread hilo = new Thread(cuerpo, "prueba-" + m.getName());
        // Daemon: si el codigo del estudiante cicla infinitamente, el hilo queda
        // abandonado pero no impide que la JVM termine.
        hilo.setDaemon(true);
        hilo.start();
        try {
            hilo.join(timeoutMs);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String estado, mensaje;
        double puntos;

        if (hilo.isAlive()) {
            estado = "TIMEOUT";
            mensaje = "La prueba excedio " + timeoutMs + " ms. Causa tipica: un ciclo "
                    + "que nunca termina o un Scanner esperando datos que no llegan.";
            puntos = 0.0;
        } else if (fallo[0] == null) {
            estado = "OK";
            mensaje = "";
            puntos = p.puntos();
        } else if (fallo[0] instanceof AssertionError) {
            estado = "FALLO";
            mensaje = fallo[0].getMessage() == null ? "Condicion no cumplida" : fallo[0].getMessage();
            puntos = 0.0;
        } else {
            estado = "ERROR";
            mensaje = descripcion(fallo[0]);
            puntos = 0.0;
        }

        emitir(out, "{\"tipo\":\"resultado\",\"id\":" + json(m.getName())
                + ",\"nombre\":" + json(p.value())
                + ",\"estado\":" + json(estado)
                + ",\"puntos\":" + puntos
                + ",\"puntosMax\":" + p.puntos()
                + ",\"mensaje\":" + json(mensaje) + "}");

        if (legible) {
            stdoutReal.printf("[%s] %s%n", "OK".equals(estado) ? " OK " : "FALLA", p.value());
            if (!mensaje.isEmpty()) stdoutReal.println("        " + mensaje);
        }
        return new double[]{puntos, p.puntos()};
    }

    /** Descripcion corta y util de una excepcion, con la linea del codigo del estudiante. */
    private static String descripcion(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getSimpleName());
        if (t.getMessage() != null) sb.append(": ").append(t.getMessage());
        for (StackTraceElement e : t.getStackTrace()) {
            String c = e.getClassName();
            if (!c.startsWith("marco.") && !c.startsWith("java.") && !c.startsWith("jdk.")
                    && !c.startsWith("sun.") && !c.startsWith("Pruebas")) {
                sb.append("  [en ").append(c).append('.').append(e.getMethodName())
                  .append(", linea ").append(e.getLineNumber()).append(']');
                break;
            }
        }
        return sb.toString();
    }

    private static void emitir(BufferedWriter out, String linea) throws IOException {
        out.write(linea);
        out.newLine();
        out.flush();
    }

    /** Escape JSON minimo pero correcto. */
    static String json(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }
}
