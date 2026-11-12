package pe.edu.upeu.celmax.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {
    private String idInventario;
    @NotNull(message = "El celular es obligatorio")
    private Celular idCelular;
    @NotNull(message = "La sucursal es obligatoria")
    private Sucursal idSucursal;
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;
}
