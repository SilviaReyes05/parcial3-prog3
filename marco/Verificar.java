package marco;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Biblioteca de aserciones para el curso de Programacion Orientada a Objetos.
 *
 * Bloque 1: aserciones basicas (Unidades I y II).
 * Bloque 2: ejecucion de programas de consola con entrada simulada (Unidades I y II).
 * Bloque 3: verificaciones estructurales por reflexion (Unidades III y IV):
 *           encapsulamiento, constructores, herencia, sobrescritura, abstraccion.
 *
 * Todos los mensajes estan escritos para que el estudiante los lea directamente
 * en la retroalimentacion de Moodle.
 */
public final class Verificar {

    private Verificar() {}

    // ---------------------------------------------------------------- basicas

    public static void esVerdadero(boolean cond, String mensaje) {
        if (!cond) throw new AssertionError(mensaje);
    }

    public static void igual(Object esperado, Object real, String mensaje) {
        if (!Objects.equals(esperado, real))
            throw new AssertionError(mensaje + " (esperado: " + esperado + " / obtenido: " + real + ")");
    }

    public static void igual(double esperado, double real, double tolerancia, String mensaje) {
        if (Double.isNaN(real) || Math.abs(esperado - real) > tolerancia)
            throw new AssertionError(mensaje + " (esperado: " + esperado + " / obtenido: " + real + ")");
    }

    public static void lanza(Class<? extends Throwable> tipo, Ejecutable accion, String mensaje) {
        try {
            accion.correr();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) return;
            throw new AssertionError(mensaje + " (se lanzo " + t.getClass().getSimpleName()
                    + " en lugar de " + tipo.getSimpleName() + ")");
        }
        throw new AssertionError(mensaje + " (no se lanzo ninguna excepcion)");
    }

    @FunctionalInterface public interface Ejecutable { void correr() throws Throwable; }

    // ------------------------------------------------- consola (Unidades I-II)

    /**
     * Ejecuta el main de una clase alimentando la entrada estandar y devuelve
     * lo que el programa imprimio. Uso tipico en Unidad II:
     *
     *   String s = Verificar.ejecutarMain("Promedio", "8.5\n7.0\n9.25\n");
     *   Verificar.contieneNumero(s, 8.25, 0.01, "Debe imprimir el promedio 8.25");
     */
    public static String ejecutarMain(String clase, String entrada) {
        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;
        InputStream inOriginal = System.in;
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturado, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(capturado, true, StandardCharsets.UTF_8));
            System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));

            Class<?> c = Class.forName(clase);
            Method main = c.getMethod("main", String[].class);
            main.invoke(null, (Object) new String[0]);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("No se encontro la clase publica '" + clase
                    + "'. Revise el nombre del archivo y de la clase en la plantilla.");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("La clase '" + clase
                    + "' no declara 'public static void main(String[] args)'.");
        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            if (causa instanceof NoSuchElementException)
                throw new AssertionError("El programa pidio mas datos de los que el caso de prueba "
                        + "entrega. Verifique cuantas lecturas hace con Scanner.");
            throw new AssertionError("El programa termino con "
                    + causa.getClass().getSimpleName()
                    + (causa.getMessage() != null ? ": " + causa.getMessage() : ""));
        } catch (IllegalAccessException e) {
            throw new AssertionError("El metodo main no es accesible (debe ser public static).");
        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
            System.setIn(inOriginal);
        }
        return capturado.toString(StandardCharsets.UTF_8);
    }

    /**
     * Busca un numero en la salida con tolerancia. Acepta coma o punto decimal y
     * separadores de miles, para no castigar diferencias de formato en Unidad I.
     */
    public static void contieneNumero(String salida, double esperado, double tolerancia, String mensaje) {
        for (String token : salida.replace(',', '.').split("[^0-9.\\-]+")) {
            if (token.isEmpty() || token.equals(".") || token.equals("-")) continue;
            try {
                if (Math.abs(Double.parseDouble(token) - esperado) <= tolerancia) return;
            } catch (NumberFormatException ignored) { }
        }
        throw new AssertionError(mensaje + " (no se encontro el valor " + esperado
                + " en la salida del programa)");
    }

    /** Comparacion de texto ignorando mayusculas, tildes y espacios sobrantes. */
    public static void contieneTexto(String salida, String fragmento, String mensaje) {
        if (!normalizar(salida).contains(normalizar(fragmento)))
            throw new AssertionError(mensaje + " (no se encontro '" + fragmento + "' en la salida)");
    }

    public static String normalizar(String s) {
        String n = java.text.Normalizer.normalize(s == null ? "" : s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
        return n.replaceAll("\\s+", " ").trim();
    }

    // ------------------------------------------- estructura (Unidades III-IV)

    public static Class<?> clase(String nombre) {
        try {
            return Class.forName(nombre);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("No existe la clase '" + nombre
                    + "'. Debe llamarse exactamente asi, sin 'package' y en su propio archivo .java.");
        }
    }

    /** Criterio F1 Unidad III: encapsulamiento. Todos los atributos de instancia privados. */
    public static void atributosPrivados(Class<?> c) {
        List<String> malos = new ArrayList<>();
        for (Field f : c.getDeclaredFields()) {
            if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) continue;
            if (!Modifier.isPrivate(f.getModifiers())) malos.add(f.getName());
        }
        if (!malos.isEmpty())
            throw new AssertionError("Rompe el encapsulamiento: el atributo "
                    + String.join(", ", malos) + " de " + c.getSimpleName()
                    + " debe declararse private.");
    }

    /** Verifica getter y setter con el nombre y tipo convencionales de Java. */
    public static void tieneGetterYSetter(Class<?> c, String atributo, Class<?> tipo) {
        String suf = Character.toUpperCase(atributo.charAt(0)) + atributo.substring(1);
        String getter = (tipo == boolean.class ? "is" : "get") + suf;
        tieneMetodo(c, getter, tipo);
        tieneMetodo(c, "set" + suf, void.class, tipo);
    }

    public static Method tieneMetodo(Class<?> c, String nombre, Class<?> retorno, Class<?>... params) {
        try {
            Method m = c.getDeclaredMethod(nombre, params);
            if (!Modifier.isPublic(m.getModifiers()))
                throw new AssertionError("El metodo " + c.getSimpleName() + "." + nombre
                        + " existe pero no es public.");
            if (retorno != null && !m.getReturnType().equals(retorno))
                throw new AssertionError("El metodo " + c.getSimpleName() + "." + nombre
                        + " debe retornar " + retorno.getSimpleName()
                        + " y retorna " + m.getReturnType().getSimpleName() + ".");
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Falta el metodo public " + firma(nombre, retorno, params)
                    + " en la clase " + c.getSimpleName() + ".");
        }
    }

    public static Constructor<?> tieneConstructor(Class<?> c, Class<?>... params) {
        try {
            Constructor<?> k = c.getDeclaredConstructor(params);
            k.setAccessible(true);
            return k;
        } catch (NoSuchMethodException e) {
            StringBuilder sb = new StringBuilder();
            for (Class<?> p : params) { if (sb.length() > 0) sb.append(", "); sb.append(p.getSimpleName()); }
            throw new AssertionError("Falta el constructor " + c.getSimpleName()
                    + "(" + sb + "). Recuerde que al declarar un constructor con parametros, "
                    + "el constructor por defecto deja de existir.");
        }
    }

    /** Criterio F1 Unidad III: sobrecarga de constructores. */
    public static void sobrecargaConstructores(Class<?> c, int minimo) {
        int n = c.getDeclaredConstructors().length;
        if (n < minimo)
            throw new AssertionError("La clase " + c.getSimpleName() + " debe tener al menos "
                    + minimo + " constructores sobrecargados y solo declara " + n + ".");
    }

    /** Criterio F1 Unidad IV: herencia. */
    public static void heredaDe(Class<?> sub, Class<?> sup) {
        if (!sup.equals(sub.getSuperclass()))
            throw new AssertionError(sub.getSimpleName() + " debe heredar directamente de "
                    + sup.getSimpleName() + " usando 'extends'. Superclase actual: "
                    + sub.getSuperclass().getSimpleName() + ".");
    }

    public static void implementa(Class<?> c, Class<?> interfaz) {
        for (Class<?> i : c.getInterfaces()) if (i.equals(interfaz)) return;
        throw new AssertionError(c.getSimpleName() + " debe implementar la interfaz "
                + interfaz.getSimpleName() + ".");
    }

    public static void esAbstracta(Class<?> c) {
        if (!Modifier.isAbstract(c.getModifiers()))
            throw new AssertionError("La clase " + c.getSimpleName() + " debe declararse abstract.");
    }

    /** Criterio F1 Unidad IV: sobrescritura real, no heredada de la superclase. */
    public static void sobrescribe(Class<?> sub, String metodo, Class<?>... params) {
        try {
            sub.getDeclaredMethod(metodo, params);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(sub.getSimpleName() + " debe sobrescribir el metodo "
                    + metodo + " con @Override, no solo heredarlo.");
        }
    }

    /**
     * Criterio F1 Unidad IV: polimorfismo dinamico. Comprueba que invocar el metodo
     * sobre una referencia de la superclase ejecute la version de la subclase.
     */
    public static void enlaceDinamico(Object instanciaSub, Class<?> superclase,
                                      String metodo, Object esperado) {
        try {
            Method m = superclase.getMethod(metodo);
            Object real = m.invoke(instanciaSub);
            if (!Objects.equals(esperado, real))
                throw new AssertionError("El polimorfismo dinamico no funciona: al invocar "
                        + metodo + "() sobre una referencia de tipo " + superclase.getSimpleName()
                        + " se esperaba '" + esperado + "' y se obtuvo '" + real + "'.");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("La superclase " + superclase.getSimpleName()
                    + " no declara el metodo " + metodo + "().");
        } catch (Exception e) {
            throw new AssertionError("Error al invocar " + metodo + "(): " + e);
        }
    }

    // ------------------------------------------------------------- utilidades

    public static Object nueva(Class<?> c, Object... args) {
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            if (k.getParameterCount() != args.length) continue;
            try {
                k.setAccessible(true);
                return k.newInstance(args);
            } catch (IllegalArgumentException ignored) {
            } catch (InvocationTargetException e) {
                throw new AssertionError("El constructor de " + c.getSimpleName()
                        + " lanzo " + e.getCause());
            } catch (Exception e) {
                throw new AssertionError("No se pudo instanciar " + c.getSimpleName() + ": " + e);
            }
        }
        throw new AssertionError("No hay un constructor de " + c.getSimpleName()
                + " compatible con los argumentos de la prueba.");
    }

    public static Object invocar(Object obj, String metodo, Object... args) {
        for (Method m : obj.getClass().getMethods()) {
            if (!m.getName().equals(metodo) || m.getParameterCount() != args.length) continue;
            try {
                m.setAccessible(true);
                return m.invoke(obj, args);
            } catch (IllegalArgumentException ignored) {
            } catch (InvocationTargetException e) {
                Throwable c = e.getCause();
                if (c instanceof RuntimeException) throw (RuntimeException) c;
                throw new AssertionError("El metodo " + metodo + " lanzo " + c);
            } catch (Exception e) {
                throw new AssertionError("No se pudo invocar " + metodo + ": " + e);
            }
        }
        throw new AssertionError("No se encontro el metodo publico " + metodo
                + " con " + args.length + " parametro(s) en " + obj.getClass().getSimpleName() + ".");
    }

    private static String firma(String nombre, Class<?> retorno, Class<?>... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(retorno == null ? "?" : retorno.getSimpleName()).append(' ').append(nombre).append('(');
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(params[i].getSimpleName());
        }
        return sb.append(')').toString();
    }
}
