package pe.edu.upeu.celmax.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.celmax.enums.TipoProducto;

/**
 * Opción del combo "Producto" en la pantalla de venta.
 * Un celular se vende desde una fila de Inventario (sucursal + precio + stock);
 * un accesorio se vende con su propio precio y stock.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVenta {
    private TipoProducto tipo;
    private String idProducto;     // idCelular o idAccesorio
    private String idInventario;   // solo para celulares
    private String descripcion;
    private Double precio;
    private Integer stock;

    @Override
    public String toString() {
        return (tipo == TipoProducto.CELULAR ? "[Celular] " : "[Accesorio] ") + descripcion
                + "  |  S/ " + String.format("%.2f", precio) + "  |  stock: " + stock;
    }
}
