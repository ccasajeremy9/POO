package pe.edu.upeu.celmax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    private String idDetalle;
    private Venta idVenta;
    /** Opcional: la línea es de un celular ... */
    private Celular idCelular;
    /** ... o de un accesorio. */
    private Accesorio idAccesorio;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // ---- Datos de apoyo (no son columnas de la tabla) ----
    /** Fila de Inventario de donde sale el celular (para descontar stock). */
    private String idInventario;
    /** Texto para mostrar en tablas: modelo del celular o nombre del accesorio. */
    private String descripcion;

    /** "Celular" o "Accesorio", para mostrar en las tablas. */
    public String getTipo() {
        return idCelular != null ? "Celular" : "Accesorio";
    }
}
