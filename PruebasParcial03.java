import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import marco.Prueba;
import marco.Verificar;

/**
 * Pruebas PUBLICAS del Parcial 3 (PROG3, Unidad III). Se entregan al estudiante.
 *
 * Una prueba por metodo, con el mismo ejemplo que aparece en el comentario del
 * metodo en la plantilla. Peso: 4 puntos por metodo (20 publicos) + 4 puntos por
 * metodo en PruebasParcial03Ocultas (20 reservados) = 40. Cada metodo vale lo
 * mismo: 8 de 40 = 20 % de la parte practica.
 *
 * Contenidos: arreglos unidimensionales (metodos 1 y 2) y colecciones List, Set
 * y Map (metodos 3 a 5), de lo sencillo a lo intermedio.
 *
 * Los metodos del estudiante se invocan por reflexion para que este archivo
 * compile aunque la entrega tenga una firma modificada: en ese caso solo falla
 * la prueba de ese metodo, no toda la entrega.
 */
public class PruebasParcial03 {

    static final String CLASE = "Parcial3ArreglosColecciones";

    // ---------------------------------------- PARTE A: arreglos unidimensionales

    @Prueba(value = "METODO 1 sumarCantidades: suma los elementos del arreglo", puntos = 4.0)
    public void m1SumarCantidades() {
        Method m = metodo("sumarCantidades", int.class, int[].class);
        Verificar.igual(50, llamar(m, (Object) new int[] {15, 30, 5}),
                "sumarCantidades({15, 30, 5})");
        Verificar.igual(7, llamar(m, (Object) new int[] {7}),
                "sumarCantidades({7})");
    }

    @Prueba(value = "METODO 2 indiceDelMayor: retorna la POSICION del mayor (la primera si se repite)", puntos = 4.0)
    public void m2IndiceDelMayor() {
        Method m = metodo("indiceDelMayor", int.class, int[].class);
        Verificar.igual(1, llamar(m, (Object) new int[] {4, 9, 2, 9}),
                "indiceDelMayor({4, 9, 2, 9}) debe retornar la primera posicion del 9");
        Verificar.igual(3, llamar(m, (Object) new int[] {1, 2, 3, 8}),
                "indiceDelMayor({1, 2, 3, 8})");
    }

    // -------------------------------------------------- PARTE B: colecciones

    @Prueba(value = "METODO 3 filtrarStockBajo: List con los nombres cuya cantidad es menor que el minimo", puntos = 4.0)
    public void m3FiltrarStockBajo() {
        Method m = metodo("filtrarStockBajo", List.class, String[].class, int[].class, int.class);
        Object r = llamar(m, new String[] {"Agua", "Café", "Leche", "Pan"},
                new int[] {12, 3, 0, 10}, 10);
        Verificar.igual(List.of("Café", "Leche"), r,
                "filtrarStockBajo del ejemplo del enunciado (minimo 10)");
    }

    @Prueba(value = "METODO 4 categoriasUnicas: Set sin duplicados, con trim y en orden alfabetico", puntos = 4.0)
    public void m4CategoriasUnicas() {
        Method m = metodo("categoriasUnicas", java.util.Set.class, List.class);
        Object r = llamar(m, List.of("Snacks", " Bebidas", "Lácteos", "Bebidas ", ""));
        Verificar.igual(List.of("Bebidas", "Lácteos", "Snacks"), enOrden(r),
                "categoriasUnicas del ejemplo del enunciado (use TreeSet para el orden)");
    }

    @Prueba(value = "METODO 5 construirInventario: Map codigo -> cantidad que SUMA los codigos repetidos", puntos = 4.0)
    public void m5ConstruirInventario() {
        Method m = metodo("construirInventario", Map.class, String[].class, int[].class);
        Object r = llamar(m, new String[] {"P01", "P02", "P01", "P03"}, new int[] {10, 5, 20, 7});
        Verificar.igual(Map.of("P01", 30, "P02", 5, "P03", 7), r,
                "construirInventario del ejemplo del enunciado (P01 aparece dos veces: 10 + 20)");
    }

    // ------------------------------------------------------------ utilidades

    /** Busca el metodo con la firma exacta del enunciado y exige que sea public static. */
    static Method metodo(String nombre, Class<?> retorno, Class<?>... params) {
        Method m = Verificar.tieneMetodo(Verificar.clase(CLASE), nombre, retorno, params);
        if (!Modifier.isStatic(m.getModifiers()))
            throw new AssertionError("El metodo " + nombre + " debe ser public static. "
                    + "No cambie la firma que trae la plantilla.");
        return m;
    }

    /**
     * Invoca un metodo static del estudiante. Traduce a un mensaje claro los dos
     * errores mas comunes; cualquier otra excepcion se propaga tal cual para que el
     * Juez la reporte con la linea del codigo del estudiante.
     */
    static Object llamar(Method m, Object... args) {
        try {
            return m.invoke(null, args);
        } catch (InvocationTargetException e) {
            Throwable c = e.getCause();
            if (c instanceof UnsupportedOperationException) {
                String msg = c.getMessage();
                if (msg != null && msg.startsWith("TODO"))
                    throw new AssertionError("El metodo " + m.getName() + " aun no esta resuelto: "
                            + "borre la linea throw new UnsupportedOperationException(\"TODO ...\") "
                            + "y escriba su solucion.");
                throw new AssertionError("El metodo " + m.getName() + " intento modificar una "
                        + "coleccion que recibio como parametro y que no se puede modificar. "
                        + "Trabaje sobre una copia, por ejemplo new ArrayList<>(lista).");
            }
            if (c instanceof RuntimeException) throw (RuntimeException) c;
            if (c instanceof Error) throw (Error) c;
            throw new AssertionError("El metodo " + m.getName() + " lanzo " + c);
        } catch (IllegalAccessException e) {
            throw new AssertionError("El metodo " + m.getName() + " debe ser public.");
        }
    }

    /**
     * Exige IllegalArgumentException. Si el metodo aun tiene el TODO, se muestra
     * ese mensaje y no un confuso "se lanzo AssertionError".
     */
    static void exigeIllegalArgument(Method m, String mensaje, Object... args) {
        try {
            llamar(m, args);
        } catch (IllegalArgumentException e) {
            return;
        } catch (RuntimeException e) {
            throw new AssertionError(mensaje + " (se lanzo " + e.getClass().getSimpleName()
                    + " en lugar de IllegalArgumentException)");
        }
        throw new AssertionError(mensaje + " (no se lanzo ninguna excepcion)");
    }

    /** Copia una coleccion a una lista conservando su orden de recorrido. */
    static List<Object> enOrden(Object coleccion) {
        if (coleccion == null)
            throw new AssertionError("El metodo retorno null. Debe retornar una coleccion (vacia si no hay datos).");
        return new ArrayList<>((Collection<?>) coleccion);
    }
}
