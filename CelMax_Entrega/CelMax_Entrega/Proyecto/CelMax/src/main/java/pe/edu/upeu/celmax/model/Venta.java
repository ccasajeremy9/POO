package pe.edu.upeu.celmax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    private String idVenta;
    private Cliente idCliente;
    private LocalDate fechaVenta;
    /** columna pNeto: total sin IGV */
    private Double precioNeto;
    private Double igv;
    /** columna pTotal: total a pagar */
    private Double precioTotal;
    private Usuario idUsuario;
    @Builder.Default
    private List<DetalleVenta> detalles = new ArrayList<>();
}
