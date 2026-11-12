package pe.edu.upeu.celmax.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sucursal {
    private String idSucursal;
    @NotNull(message = "El número de tienda es obligatorio")
    @Positive(message = "El número de tienda debe ser mayor a 0")
    private Integer numeroTienda;
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    @NotBlank(message = "El tipo de tienda es obligatorio")
    private String tipo;
}
