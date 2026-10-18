import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/*
 * ============================================================================
 *  UNIVERSIDAD TECNOLÓGICA DE EL SALVADOR (UTEC)
 *  Facultad de Informática y Ciencias Aplicadas · Escuela de Informática
 *  Programación III (PROG3) · Ciclo 02-2026
 *  EXAMEN PARCIAL 3 · Segunda parte: ejercicio práctico (60 %)
 *  Unidad 3: Diseñar aplicaciones utilizando arreglos, clases y métodos
 * ============================================================================
 *
 *  Nombre: Silvia Arely Reyes Rodriguez   Carnet: 17-2696-2024
 *
 *  CASO: inventario en memoria de «DistriSalva, S.A. de C.V.»
 *  ----------------------------------------------------------------------------
 *  DistriSalva necesita una clase de utilidades que procese su inventario en
 *  memoria usando ARREGLOS (Parte A) y COLECCIONES (Parte B). Usted debe
 *  completar los 5 métodos donde dice COMPLETAR (TODO). Van de lo más
 *  sencillo a lo intermedio; resuélvalos en orden. Todos valen lo mismo:
 *  20 % de la parte práctica cada uno (60 % del parcial).
 *
 *  Cada método trae una sección PISTAS con los pasos sugeridos. Léalas antes
 *  de escribir código: le dicen qué estructura usar y qué casos límite se
 *  van a revisar.
 *
 *  Su archivo será calificado AUTOMÁTICAMENTE por el evaluador Java
 *  (auto-grader) sobre 10 puntos. La nota combina las PRUEBAS PÚBLICAS, que
 *  usted puede ejecutar con el verificador del paquete, y CASOS RESERVADOS
 *  (valores frontera, null, vacíos, empates y excepciones) que no se publican.
 *
 *  REGLAS OBLIGATORIAS (si no las cumple, el auto-grader no podrá calificar)
 *  ----------------------------------------------------------------------------
 *   1. NO cambie el nombre de la clase ni el nombre del archivo:
 *      Parcial3ArreglosColecciones.java
 *   2. NO cambie la firma de ningún método (nombre, parámetros, tipo de
 *      retorno, public static).
 *   3. NO agregue la instrucción "package" al inicio del archivo.
 *   4. NO use Scanner ni lea datos del teclado. NO use System.exit().
 *   5. Los métodos DEVUELVEN el resultado con return; no lo imprimen.
 *   6. Solo puede usar clases de java.util (ya están importadas las
 *      necesarias; puede agregar otras de java.util si las necesita).
 *   7. Puede crear métodos auxiliares "private static" si lo desea.
 *   8. Antes de entregar, BORRE la línea
 *          throw new UnsupportedOperationException("TODO ...");
 *      de cada método que complete. Un método con esa línea vale 0.
 *   9. El archivo DEBE COMPILAR. Si no compila, la parte práctica vale 0.
 *  10. Guarde el archivo en codificación UTF-8.
 *
 *  ANTES DE ENTREGAR — verificador del paquete «paquete_parcial3.zip»
 *  ----------------------------------------------------------------------------
 *   - Pruebe rápido con el método main() que está al final de este archivo.
 *   - Luego ejecute el verificador, que compila y corre las pruebas públicas:
 *       Windows : doble clic en verificar.bat
 *       Mac     : en la Terminal, dentro de la carpeta, ejecute ./verificar.sh
 *   - Pasar todas las pruebas públicas NO garantiza el 10: también cuentan
 *     los casos reservados. Revise los casos límite que indica cada método.
 *
 *  ENTREGA: suba ÚNICAMENTE este archivo (Parcial3ArreglosColecciones.java)
 *  en la tarea «Parcial 3 — Ejercicio práctico» de Moodle antes de la hora
 *  límite. No suba la carpeta marco ni el archivo de pruebas.
 * ============================================================================
 */
public class Parcial3ArreglosColecciones {

    // =========================================================================
    //  PARTE A — ARREGLOS UNIDIMENSIONALES (40 % de la parte práctica)
    // =========================================================================

    /**
     * MÉTODO 1 (20 %) — Nivel: SENCILLO — Arreglo: acumulador.
     *
     * Devuelve la suma de todas las cantidades del arreglo.
     *
     * Reglas:
     *  - Si el arreglo es null o está vacío, devuelve 0.
     *
     * Ejemplos:
     *  sumarCantidades(new int[]{15, 30, 5})  ->  50
     *  sumarCantidades(new int[]{})           ->  0
     *  sumarCantidades(null)                  ->  0
     *
     * PISTAS:
     *  1. Lo primero: si cantidades es null, retorne 0. Si no lo valida, el
     *     for lanzará NullPointerException.
     *  2. Declare un acumulador ANTES del ciclo:  int suma = 0;
     *  3. Recorra el arreglo con un for clásico (i desde 0 hasta
     *     cantidades.length - 1) o con un for-each (for (int c : cantidades)).
     *  4. Dentro del ciclo sume cada elemento:  suma += ...;
     *  5. Retorne el acumulador DESPUÉS del ciclo (no dentro).
     *  Nota: con un arreglo vacío el for no se ejecuta y suma queda en 0,
     *  así que ese caso no necesita un if aparte.
     */
    
 public static int sumarCantidades(int[] cantidades) {
        if (cantidades == null) {
            return 0;
        }
        int suma = 0;
        for (int c : cantidades) {
            suma += c;
        }
        return suma;
    }
    /**
     * MÉTODO 2 (20 %) — Nivel: SENCILLO / INTERMEDIO — Arreglo: búsqueda del mayor.
     *
     * Devuelve el ÍNDICE (la posición, no el valor) del elemento mayor.
     *

     
     * Reglas:
     *  - Si el mayor se repite, devuelve la PRIMERA posición donde aparece.
     *  - Si el arreglo es null o está vacío, devuelve -1.
     *  - Funciona también con números negativos.
     *
     * Ejemplos:
     *  indiceDelMayor(new int[]{4, 9, 2, 9})  ->  1
     *  indiceDelMayor(new int[]{-8, -3, -5})  ->  1
     *  indiceDelMayor(new int[]{})            ->  -1
     *
     * PISTAS:
     *  1. Valide primero: si es null O su length es 0, retorne -1.
     *  2. Suponga que el mayor está en la posición 0:  int indice = 0;
     *     NO inicialice el mayor con el valor 0: con {-8, -3, -5} fallaría,
     *     porque ningún elemento es mayor que 0.
     *  3. Recorra desde i = 1 y compare cantidades[i] con cantidades[indice].
     *  4. Si es mayor, actualice indice = i.
     *  5. Use > y no >= : con >= un valor repetido reemplazaría al primero y
     *     devolvería la ÚLTIMA posición.
     */
    
    
public static int indiceDelMayor(int[] cantidades) {
        if (cantidades == null || cantidades.length == 0) {
            return -1;
        }
        int indice = 0;
        for (int i = 1; i < cantidades.length; i++) {
            if (cantidades[i] > cantidades[indice]) {
                indice = i;
            }
        }
        return indice;
    }
    
    // =========================================================================
    //  PARTE B — COLECCIONES (60 % de la parte práctica)
    // =========================================================================

    /**
     * MÉTODO 3 (20 %) — Nivel: INTERMEDIO — List (ArrayList): filtrar.
     *
     * nombres[i] y cantidades[i] describen el mismo producto (arreglos
     * paralelos). Devuelve una lista con los nombres de los productos cuya
     * cantidad es ESTRICTAMENTE MENOR que minimo, en el mismo orden en que
     * aparecen en el arreglo.
     *
     * Reglas:
     *  - Si ningún producto cumple, devuelve una lista VACÍA (nunca null).
     *  - Si los arreglos tienen distinta longitud, o alguno es null,
     *    lance IllegalArgumentException.
     *
     * Ejemplo:
     *  nombres     = {"Agua", "Café", "Leche", "Pan"}
     *  cantidades  = {  12,     3,      0,      10 }
     *  filtrarStockBajo(nombres, cantidades, 10)  ->  [Café, Leche]
     *
     * PISTAS:
     *  1. Validación (una sola condición con ||):
     *       nombres == null || cantidades == null
     *       || nombres.length != cantidades.length
     *     Si se cumple:  throw new IllegalArgumentException("mensaje");
     *  2. Cree la lista vacía:  List<String> resultado = new ArrayList<>();
     *  3. Recorra con un for clásico, porque necesita el índice i para leer
     *     nombres[i] y cantidades[i] a la vez.
     *  4. Si cantidades[i] < minimo  ->  resultado.add(nombres[i]);
     *     Ojo: es < y no <=. Pan tiene 10 y el mínimo es 10: NO entra.
     *  5. Retorne la lista. Si nadie cumplió, ya está vacía (no retorne null).
     */
    
 public static List<String> filtrarStockBajo(String[] nombres, int[] cantidades, int minimo) {
        if (nombres == null || cantidades == null || nombres.length != cantidades.length) {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos y deben tener la misma longitud.");
        }
        List<String> resultado = new ArrayList<>();
        for (int i = 0; i < nombres.length; i++) {
            if (cantidades[i] < minimo) {
                resultado.add(nombres[i]);
            }
        }
        return resultado;
    }
    
    /**
     * MÉTODO 4 (20 %) — Nivel: INTERMEDIO — Set (TreeSet): únicos y ordenados.
     *
     * Devuelve un conjunto con las categorías SIN DUPLICADOS y en ORDEN
     * ALFABÉTICO ascendente.
     *
     * Reglas:
     *  - Quite los espacios al inicio y al final de cada categoría (trim).
     *  - Ignore los elementos null y los que queden vacíos ("") tras el trim.
     *  - Distingue mayúsculas de minúsculas ("Snacks" y "snacks" son distintas).
     *  - Si la lista es null, devuelve un conjunto vacío.
     *
     * Ejemplo:
     *  categoriasUnicas(List.of("Snacks", " Bebidas", "Lácteos", "Bebidas ", ""))
     *      ->  [Bebidas, Lácteos, Snacks]
     *
     * PISTAS:
     *  1. Cree el conjunto al inicio:  Set<String> resultado = new TreeSet<>();
     *     Así, si la lista es null, puede retornarlo vacío de inmediato.
     *  2. TreeSet hace dos trabajos por usted: no acepta duplicados (add de un
     *     valor repetido simplemente no lo agrega) y mantiene el orden
     *     alfabético. Con HashSet NO quedarían ordenadas.
     *  3. Recorra con for-each:  for (String c : categorias) { ... }
     *  4. Primero revise c != null (antes de llamar métodos sobre c). Luego
     *     guarde c.trim() en una variable y agréguela solo si no está vacía
     *     (isEmpty()).
     *  5. Agregue el texto YA recortado: " Bebidas" y "Bebidas " deben
     *     terminar siendo el mismo "Bebidas".
     */
    

     public static Set<String> categoriasUnicas(List<String> categorias) {
        Set<String> resultado = new TreeSet<>();
        if (categorias == null) {
            return resultado;
        }
        for (String c : categorias) {
            if (c != null) {
                String trimmed = c.trim();
                if (!trimmed.isEmpty()) {
                    resultado.add(trimmed);
                }
            }
        }
        return resultado;
    }
    /**
     * MÉTODO 5 (20 %) — Nivel: INTERMEDIO — Map (HashMap): clave -> valor acumulado.
     *
     * codigos[i] y cantidades[i] corresponden a un mismo ingreso de bodega.
     * Devuelve un mapa código -> cantidad TOTAL. Si un código aparece varias
     * veces, SUME sus cantidades.
     *
     * Reglas:
     *  - Si los arreglos tienen distinta longitud, o alguno es null,
     *    lance IllegalArgumentException.
     *  - Si los arreglos están vacíos, devuelve un mapa vacío.
     *
     * Ejemplo:
     *  codigos     = {"P01", "P02", "P01", "P03"}
     *  cantidades  = {  10,     5,    20,     7 }
     *  construirInventario(codigos, cantidades)  ->  {P01=30, P02=5, P03=7}
     *
     * PISTAS:
     *  1. La validación es la misma del método 3 (null o longitudes distintas
     *     -> IllegalArgumentException).
     *  2. Cree el mapa:  Map<String, Integer> inventario = new HashMap<>();
     *  3. Recorra con un for clásico y, para cada código, piense: «¿cuánto
     *     llevaba acumulado este código?»
     *       - Si ya existe (containsKey), el nuevo total es get(...) + cantidad.
     *       - Si no existe, el total es solo la cantidad.
     *     Atajo:  inventario.getOrDefault(codigo, 0) devuelve 0 si no existe.
     *  4. Guarde el total con put(codigo, total).
     *     Error típico: put(codigo, cantidades[i]) REEMPLAZA el valor anterior
     *     en lugar de sumarlo (P01 quedaría en 20 y no en 30).
     *  5. El orden en que se imprime un HashMap no importa; lo que se revisa es
     *     que cada código tenga su total correcto.
     */

    public static Map<String, Integer> construirInventario(String[] codigos, int[] cantidades) {
        if (codigos == null || cantidades == null || codigos.length != cantidades.length) {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos y deben tener la misma longitud.");
        }
        Map<String, Integer> inventario = new HashMap<>();
        for (int i = 0; i < codigos.length; i++) {
            String codigo = codigos[i];
            int cantidad = cantidades[i];
            int total = inventario.getOrDefault(codigo, 0) + cantidad;
            inventario.put(codigo, total);
        }
        return inventario;
    }

    // =========================================================================
    //  PRUEBAS RÁPIDAS — puede modificar main() libremente; el auto-grader no
    //  lo usa. Compare la salida con los valores "esperado".
    //  Sugerencia: agregue sus propios casos límite (null, vacío, negativos).
    // =========================================================================
     public static void main(String[] args) {
        probar("1. sumarCantidades     esperado 50",
                () -> sumarCantidades(new int[]{15, 30, 5}));
        probar("2. indiceDelMayor      esperado 1",
                () -> indiceDelMayor(new int[]{4, 9, 2, 9}));
        probar("3. filtrarStockBajo    esperado [Café, Leche]",
                () -> filtrarStockBajo(new String[]{"Agua", "Café", "Leche", "Pan"},
                        new int[]{12, 3, 0, 10}, 10));
        probar("4. categoriasUnicas    esperado [Bebidas, Lácteos, Snacks]",
                () -> categoriasUnicas(List.of("Snacks", " Bebidas", "Lácteos", "Bebidas ", "")));
        probar("5. construirInventario esperado P01=30, P02=5, P03=7 (en cualquier orden)",
                () -> construirInventario(new String[]{"P01", "P02", "P01", "P03"},
                        new int[]{10, 5, 20, 7}));
    }

    /** Ejecuta una prueba y muestra el resultado o el error, sin detener main. */

private static void probar(String descripcion, java.util.function.Supplier<Object> prueba) {
        try {
            System.out.println(descripcion + "  ->  obtenido " + prueba.get());
        } catch (RuntimeException e) {
            System.out.println(descripcion + "  ->  ERROR " + e);
        }
    }
}


