package marco;

import java.lang.annotation.*;

/**
 * Marca un metodo como prueba calificada.
 *
 *   @Prueba(value = "El saldo inicial debe ser cero", puntos = 5)
 *   public void saldoInicial() { ... }
 *
 * value  = texto que vera el estudiante en su retroalimentacion.
 * puntos = peso de la prueba. La nota final es (puntos obtenidos / puntos totales).
 * oculta = si es true, no se entrega al estudiante en el verificador publico.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Prueba {
    String value();
    double puntos() default 1.0;
    boolean oculta() default false;
}
